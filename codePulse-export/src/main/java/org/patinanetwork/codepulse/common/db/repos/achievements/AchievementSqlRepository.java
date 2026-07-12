package org.patinanetwork.codepulse.common.db.repos.achievements;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codepulse.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codepulse.common.db.models.achievements.Achievement;
import org.patinanetwork.codepulse.common.db.models.achievements.AchievementPlaceEnum;
import org.patinanetwork.codepulse.common.db.models.usertag.Tag;
import org.patinanetwork.codepulse.common.time.StandardizedOffsetDateTime;
import org.springframework.stereotype.Component;

@Component
public class AchievementSqlRepository implements AchievementRepository {

    private final DataSource ds;

    public AchievementSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private Achievement parseResultSetToAchievement(final ResultSet rs) throws SQLException {
        var id = rs.getString("id");
        var userId = rs.getString("userId");
        var place = AchievementPlaceEnum.valueOf(rs.getString("place"));
        var leaderboard = Optional.ofNullable(rs.getString("leaderboard"))
                .map(Tag::valueOf)
                .orElse(null);
        var title = rs.getString("title");
        var description = rs.getString("description");
        var isActive = rs.getBoolean("isActive");
        var createdAt = StandardizedOffsetDateTime.normalize(rs.getObject("createdAt", OffsetDateTime.class));
        OffsetDateTime deletedAt =
                StandardizedOffsetDateTime.normalize(rs.getObject("deletedAt", OffsetDateTime.class));
        return Achievement.builder()
                .id(id)
                .userId(userId)
                .place(place)
                .leaderboard(leaderboard)
                .title(title)
                .description(description)
                .isActive(isActive)
                .createdAt(createdAt)
                .deletedAt(deletedAt)
                .build();
    }

    @Override
    public void createAchievement(final Achievement achievement) {
        achievement.setId(UUID.randomUUID().toString());
        achievement.setCreatedAt(StandardizedOffsetDateTime.normalize(OffsetDateTime.now()));
        String sql = """
            INSERT INTO "Achievement"
                (id, "userId", place, leaderboard, title, description, "isActive", "deletedAt", "createdAt")
            VALUES
                (:id, :userId, :place, :leaderboard, :title, :description, :isActive, :deletedAt, :createdAt)
            """;
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", achievement.getId());
            stmt.setString("userId", achievement.getUserId());
            stmt.setString("place", achievement.getPlace().name());
            stmt.setString(
                    "leaderboard",
                    Optional.ofNullable(achievement.getLeaderboard())
                            .map(Enum::name)
                            .orElse(null));
            stmt.setString("title", achievement.getTitle());
            stmt.setString("description", achievement.getDescription());
            stmt.setBoolean("isActive", achievement.isActive());
            stmt.setObject("deletedAt", achievement.getDeletedAt());
            stmt.setObject("createdAt", achievement.getCreatedAt());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create achievement", e);
        }
    }

    @Override
    public Achievement updateAchievement(final Achievement achievement) {
        String sql = """
            UPDATE
                "Achievement"
            SET
                place = :place,
                leaderboard = :leaderboard,
                title = :title,
                description = :description,
                "isActive" = :isActive,
                "deletedAt" = :deletedAt
            WHERE
                id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("place", achievement.getPlace().name());
            stmt.setString(
                    "leaderboard",
                    Optional.ofNullable(achievement.getLeaderboard())
                            .map(Enum::name)
                            .orElse(null));
            stmt.setString("title", achievement.getTitle());
            stmt.setString("description", achievement.getDescription());
            stmt.setBoolean("isActive", achievement.isActive());
            stmt.setObject("deletedAt", achievement.getDeletedAt());
            stmt.setString("id", achievement.getId());

            stmt.executeUpdate();
            return getAchievementById(achievement.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update achievement", e);
        }
    }

    @Override
    public boolean deleteAchievementById(final String id) {
        String sql = """
            UPDATE
                "Achievement"
            SET
                "deletedAt" = :deletedAt
            WHERE
                id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("deletedAt", LocalDateTime.now());
            stmt.setString("id", id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete achievement by ID", e);
        }
    }

    @Override
    public Achievement getAchievementById(final String id) {
        String sql = """
            SELECT
                id,
                "userId",
                place,
                leaderboard,
                title,
                description,
                "isActive",
                "createdAt",
                "deletedAt"
            FROM
                "Achievement"
            WHERE
                id = :id
                AND "deletedAt" IS NULL
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToAchievement(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get achievement by ID", e);
        }

        return null;
    }

    @Override
    public List<Achievement> getAchievementsByUserId(final String userId) {
        List<Achievement> achievements = new ArrayList<>();
        String sql = """
            SELECT
                id,
                "userId",
                place,
                leaderboard,
                title,
                description,
                "isActive",
                "createdAt",
                "deletedAt"
            FROM
                "Achievement"
            WHERE
                "userId" = :userId
                AND "deletedAt" IS NULL
            ORDER BY
                "createdAt" DESC,
                (leaderboard IS NULL) DESC
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("userId", userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var achievement = parseResultSetToAchievement(rs);
                    achievements.add(achievement);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get achievements by user ID", e);
        }

        return achievements;
    }
}
