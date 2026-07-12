package org.patinanetwork.codepulse.common.db.repos.usertag;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codepulse.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codepulse.common.db.models.usertag.Tag;
import org.patinanetwork.codepulse.common.db.models.usertag.UserTag;
import org.patinanetwork.codepulse.common.db.repos.usertag.options.UserTagFilterOptions;
import org.springframework.stereotype.Component;

@Component
public class UserTagSqlRepository implements UserTagRepository {

    private DataSource ds;

    public UserTagSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private UserTag parseResultSetToTag(final ResultSet rs) throws SQLException {
        var id = rs.getString("id");
        var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
        var userId = rs.getString("userId");
        var tag = Tag.valueOf(rs.getString("tag"));
        return new UserTag(id, createdAt, userId, tag);
    }

    @Override
    public Optional<UserTag> findTagByTagId(final String tagId) {
        String sql = """
            SELECT
                id,
                "createdAt",
                "userId",
                tag
            FROM
                "UserTag"
            WHERE
                id = :id
                    """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", tagId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToTag(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user tag by tag ID", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<UserTag> findTagByUserIdAndTag(final String userId, final Tag tag) {
        String sql = """
            SELECT
                id,
                "createdAt",
                "userId",
                tag
            FROM
                "UserTag"
            WHERE
                tag = :tag
                AND
                "userId" = :userId
                    """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("tag", tag.name());
            stmt.setString("userId", userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToTag(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user tag by user ID and tag", e);
        }

        return Optional.empty();
    }

    @Override
    public ArrayList<UserTag> findTagsByUserId(final String userId) {
        ArrayList<UserTag> tags = new ArrayList<>();
        String sql = """
            SELECT
                id,
                "createdAt",
                "userId",
                tag
            FROM
                "UserTag"
            WHERE
                "userId" = :userId
                    """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("userId", userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var tag = parseResultSetToTag(rs);
                    if (tag != null) {
                        tags.add(tag);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user tag by user ID and tag", e);
        }

        return tags;
    }

    @Override
    public ArrayList<UserTag> findTagsByUserId(final String userId, final UserTagFilterOptions options) {
        ArrayList<UserTag> tags = new ArrayList<>();
        String sql = """
            SELECT
                id,
                "createdAt",
                "userId",
                tag
            FROM
                "UserTag"
            WHERE
                "userId" = :userId
            AND
                (:pointOfTime IS NULL OR "createdAt" <= :pointOfTime)
                    """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("userId", userId);
            if (options.getPointOfTime() == null) {
                stmt.setNull("pointOfTime", Types.TIMESTAMP_WITH_TIMEZONE);
            } else {
                stmt.setObject("pointOfTime", options.getPointOfTime());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var tag = parseResultSetToTag(rs);
                    if (tag != null) {
                        tags.add(tag);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user tags by user ID with filter options", e);
        }

        return tags;
    }

    @Override
    public void createTag(final UserTag userTag) {
        userTag.setId(UUID.randomUUID().toString());
        userTag.setCreatedAt(java.time.LocalDateTime.now());
        String sql = """
                INSERT INTO "UserTag"
                    (id, "userId", tag, "createdAt")
                VALUES
                    (:id, :userId, :tag, :createdAt)
            """;
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", userTag.getId());
            stmt.setString("userId", userTag.getUserId());
            stmt.setString("tag", userTag.getTag().name());
            stmt.setObject("createdAt", userTag.getCreatedAt());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user tag by user ID", e);
        }
    }

    @Override
    public boolean deleteTagByTagId(final String tagId) {
        String sql = """
            DELETE FROM
                "UserTag"
            WHERE
                id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", tagId);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete tag by tag ID", e);
        }
    }

    @Override
    public boolean deleteTagByUserIdAndTag(final String userId, final Tag tag) {
        String sql = """
                DELETE FROM
                    "UserTag"
                WHERE
                    "userId" = :userId
                    AND
                    tag = :tag
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("userId", userId);
            stmt.setString("tag", tag.name());

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete tag by user ID and tag", e);
        }
    }
}
