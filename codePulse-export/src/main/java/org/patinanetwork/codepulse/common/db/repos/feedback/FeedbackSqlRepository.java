package org.patinanetwork.codepulse.common.db.repos.feedback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codepulse.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codepulse.common.db.models.feedback.Feedback;
import org.springframework.stereotype.Component;

@Component
public class FeedbackSqlRepository implements FeedbackRepository {

    private final DataSource ds;

    public FeedbackSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private Feedback parseResultSetToFeedback(final ResultSet resultSet) throws SQLException {
        return Feedback.builder()
                .id(resultSet.getString("id"))
                .title(resultSet.getString("title"))
                .description(resultSet.getString("description"))
                .email(Optional.ofNullable(resultSet.getString("email")))
                .createdAt(resultSet.getObject("createdAt", OffsetDateTime.class))
                .build();
    }

    @Override
    public Optional<Feedback> findFeedbackById(final String id) {
        String sql = """
                SELECT
                    id,
                    title,
                    description,
                    email,
                    "createdAt"
                FROM
                    "Report"
                WHERE
                    id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToFeedback(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch feedback by id", e);
        }

        return Optional.empty();
    }

    @Override
    public void createFeedback(final Feedback feedback) {
        String sql = """
               INSERT INTO "Report"
                   (id, title, description, email, "createdAt")
               VALUES
                   (:id, :title, :description, :email, :createdAt)
            """;

        feedback.setId(UUID.randomUUID().toString());
        feedback.setCreatedAt(OffsetDateTime.now());

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", feedback.getId());
            stmt.setString("title", feedback.getTitle());
            stmt.setString("description", feedback.getDescription());
            stmt.setString("email", feedback.getEmail().orElse(null));
            stmt.setObject("createdAt", feedback.getCreatedAt());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create feedback", e);
        }
    }

    @Override
    public boolean deleteFeedbackById(final String id) {
        String sql = """
                DELETE FROM
                    "Report"
                WHERE
                    id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", id);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete feedback by ID", e);
        }
    }

    @Override
    public boolean updateFeedback(final Feedback feedback) {
        String sql = """
                UPDATE "Report"
                SET
                    title = :title,
                    description = :description,
                    email = :email
                WHERE
                    id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("title", feedback.getTitle());
            stmt.setString("description", feedback.getDescription());
            stmt.setString("email", feedback.getEmail().orElse(null));
            stmt.setString("id", feedback.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update feedback", e);
        }
    }
}
