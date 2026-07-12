package org.patinanetwork.codepulse.common.db.repos.user;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.patinanetwork.codepulse.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codepulse.common.db.models.user.UserMetrics;
import org.patinanetwork.codepulse.common.db.repos.user.options.UserMetricsFilterOptions;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserMetricsSqlRepository implements UserMetricsRepository {

    private final DataSource ds;

    public UserMetricsSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private UserMetrics parseResultSetToUserMetrics(final ResultSet rs) throws SQLException {
        return UserMetrics.builder()
                .id(rs.getString("id"))
                .userId(rs.getString("userId"))
                .points(rs.getInt("points"))
                .createdAt(rs.getObject("createdAt", OffsetDateTime.class))
                .deletedAt(Optional.ofNullable(rs.getObject("deletedAt", OffsetDateTime.class)))
                .build();
    }

    @Override
    public void createUserMetrics(final UserMetrics userMetrics) {
        String sql = """
                INSERT INTO "UserMetrics"
                    (id, "userId", points, "createdAt")
                VALUES
                    (:id, :userId, :points, :createdAt)
                """;

        userMetrics.setId(UUID.randomUUID().toString());
        userMetrics.setCreatedAt(OffsetDateTime.now());

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", userMetrics.getId());
            stmt.setString("userId", userMetrics.getUserId());
            stmt.setInt("points", userMetrics.getPoints());
            stmt.setObject("createdAt", userMetrics.getCreatedAt());

            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Exception thrown in UserMetricsSqlRepository.createUserMetrics", e);
            throw new RuntimeException("Failed to create user metrics", e);
        }
    }

    @Override
    public Optional<UserMetrics> findUserMetricsById(final String id) {
        String sql = """
                SELECT
                    id,
                    "userId",
                    points,
                    "createdAt",
                    "deletedAt"
                FROM
                    "UserMetrics"
                WHERE
                    id = :id
                    AND "deletedAt" IS NULL
                """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToUserMetrics(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Exception thrown in UserMetricsSqlRepository.findUserMetricsById", e);
            throw new RuntimeException("Failed to fetch user metrics by id", e);
        }

        return Optional.empty();
    }

    @Override
    public List<UserMetrics> findUserMetrics(final String userId, final UserMetricsFilterOptions options) {
        String sql = """
                SELECT
                    id,
                    "userId",
                    points,
                    "createdAt",
                    "deletedAt"
                FROM
                    "UserMetrics"
                WHERE
                    "userId" = :userId
                    AND "deletedAt" IS NULL
                    AND (:from IS NULL OR "createdAt" >= :from)
                    AND (:to IS NULL OR "createdAt" <= :to)
                LIMIT CASE WHEN :pageSize > 0 THEN :pageSize END
                OFFSET :offset
                """;

        List<UserMetrics> results = new ArrayList<>();

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("userId", userId);
            stmt.setObject("from", options.getFrom(), Types.TIMESTAMP_WITH_TIMEZONE);
            stmt.setObject("to", options.getTo(), Types.TIMESTAMP_WITH_TIMEZONE);
            stmt.setInt("pageSize", options.getPageSize());
            stmt.setInt("offset", options.getPageSize() > 0 ? (options.getPage() - 1) * options.getPageSize() : 0);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(parseResultSetToUserMetrics(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Exception thrown in UserMetricsSqlRepository.findUserMetrics", e);
            throw new RuntimeException("Failed to fetch user metrics", e);
        }

        return results;
    }

    @Override
    public int countUserMetrics(final String userId, final UserMetricsFilterOptions options) {
        String sql = """
                SELECT COUNT(*)
                FROM
                    "UserMetrics"
                WHERE
                    "userId" = :userId
                    AND "deletedAt" IS NULL
                    AND (:from IS NULL OR "createdAt" >= :from)
                    AND (:to IS NULL OR "createdAt" <= :to)
                """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("userId", userId);
            stmt.setObject("from", options.getFrom(), Types.TIMESTAMP_WITH_TIMEZONE);
            stmt.setObject("to", options.getTo(), Types.TIMESTAMP_WITH_TIMEZONE);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            log.error("Exception thrown in UserMetricsSqlRepository.countUserMetrics", e);
            throw new RuntimeException("Failed to count user metrics", e);
        }

        return 0;
    }

    @Override
    public boolean deleteUserMetricsById(final String id) {
        String sql = """
                UPDATE "UserMetrics"
                SET
                    "deletedAt" = NOW()
                WHERE
                    id = :id
                    AND "deletedAt" IS NULL
                """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", id);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected == 1;
        } catch (SQLException e) {
            log.error("Exception thrown in UserMetricsSqlRepository.deleteUserMetricsById", e);
            throw new RuntimeException("Failed to delete user metrics by id", e);
        }
    }
}
