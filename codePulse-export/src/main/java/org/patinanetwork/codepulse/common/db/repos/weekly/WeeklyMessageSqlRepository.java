package org.patinanetwork.codepulse.common.db.repos.weekly;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codepulse.common.db.models.weekly.WeeklyMessage;
import org.springframework.stereotype.Component;

@Component
public class WeeklyMessageSqlRepository implements WeeklyMessageRepository {

    private DataSource ds;

    public WeeklyMessageSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private WeeklyMessage parseResultSetToWeeklyMessage(final ResultSet resultSet) throws SQLException {
        return WeeklyMessage.builder()
                .id(resultSet.getString("id"))
                .createdAt(resultSet.getTimestamp("createdAt").toLocalDateTime())
                .build();
    }

    private void updateWeeklyMessageWithResultSet(final WeeklyMessage message, final ResultSet resultSet)
            throws SQLException {
        message.setId(resultSet.getString("id"));
        message.setCreatedAt(resultSet.getTimestamp("createdAt").toLocalDateTime());
    }

    @Override
    public WeeklyMessage getLatestWeeklyMessage() {
        String sql = """
            SELECT
                id,
                "createdAt"
            FROM
                "WeeklyMessage"
            ORDER BY
                "createdAt" DESC
            LIMIT 1
                                """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToWeeklyMessage(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving latest weekly message", e);
        }

        return null;
    }

    @Override
    public WeeklyMessage getWeeklyMessageById(final String id) {
        String sql = """
            SELECT
                id,
                "createdAt"
            FROM
                "WeeklyMessage"
            WHERE
                id = ?
            LIMIT 1
                                """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToWeeklyMessage(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get weekly message by ID", e);
        }

        return null;
    }

    @Override
    public boolean createLatestWeeklyMessage(final WeeklyMessage message) {
        String sql = """
                INSERT INTO "WeeklyMessage"
                    (id, "createdAt")
                VALUES
                    (?, ?)
            """;

        String id = UUID.randomUUID().toString();
        java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setObject(2, createdAt);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                message.setId(id);
                message.setCreatedAt(createdAt);
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create new latest weekly message", e);
        }

        return false;
    }

    @Override
    public boolean createLatestWeeklyMessage() {
        String sql = """
                INSERT INTO "WeeklyMessage"
                    (id)
                VALUES
                    (?)
            """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, UUID.randomUUID().toString());

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create new latest weekly message", e);
        }
    }

    @Override
    public boolean deleteWeeklyMessageById(final String id) {
        String sql = """
            DELETE FROM
                "WeeklyMessage"
            WHERE
                id = ?
            """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete weekly message", e);
        }
    }

    @Override
    public boolean deleteLatestWeeklyMessage() {
        String sql = """
            WITH to_delete AS (
                SELECT *
                FROM "WeeklyMessage"
                ORDER BY "createdAt" DESC
                LIMIT 1
            )
            DELETE FROM "WeeklyMessage" wm
            USING to_delete td
            WHERE wm.id = td.id
            """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete weekly message", e);
        }
    }
}
