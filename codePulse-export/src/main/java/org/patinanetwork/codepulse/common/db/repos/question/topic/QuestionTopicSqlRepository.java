package org.patinanetwork.codepulse.common.db.repos.question.topic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codepulse.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codepulse.common.db.models.question.topic.LeetcodeTopicEnum;
import org.patinanetwork.codepulse.common.db.models.question.topic.QuestionTopic;
import org.springframework.stereotype.Component;

@Component
public class QuestionTopicSqlRepository implements QuestionTopicRepository {

    private final DataSource ds;

    public QuestionTopicSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private QuestionTopic mapResultSetToQuestionTopic(final ResultSet rs) throws SQLException {
        return QuestionTopic.builder()
                .id(rs.getString("id"))
                .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
                .questionId(rs.getString("questionId"))
                .questionBankId(rs.getString("questionBankId"))
                .topicSlug(rs.getString("topicSlug"))
                .topic(LeetcodeTopicEnum.fromValue(rs.getString("topic")))
                .build();
    }

    @Override
    public List<QuestionTopic> findQuestionTopicsByQuestionId(final String questionId) {
        List<QuestionTopic> result = new ArrayList<>();

        String sql = """
                SELECT
                    id,
                    "questionId",
                    "questionBankId",
                    "topicSlug",
                    "createdAt",
                    "topic"
                FROM
                    "QuestionTopic" qt
                WHERE
                    qt."questionId" = :questionId
                """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("questionId", questionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToQuestionTopic(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find question topics by question ID", e);
        }
    }

    @Override
    public List<QuestionTopic> findQuestionTopicsByQuestionBankId(final String questionBankId) {
        List<QuestionTopic> result = new ArrayList<>();

        String sql = """
                SELECT
                    id,
                    "questionId",
                    "questionBankId",
                    "topicSlug",
                    "createdAt",
                    "topic"
                FROM
                    "QuestionTopic" qt
                WHERE
                    qt."questionBankId" = :questionBankId
                """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("questionBankId", questionBankId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToQuestionTopic(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find question topics by question bank ID", e);
        }
    }

    @Override
    public Optional<QuestionTopic> findQuestionTopicById(final String id) {
        String sql = """
                SELECT
                    id,
                    "questionId",
                    "questionBankId",
                    "topicSlug",
                    "createdAt",
                    "topic"
                FROM
                    "QuestionTopic" qt
                WHERE
                    qt.id = :id
                """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToQuestionTopic(rs));
                }
            }

            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get question topic by ID", e);
        }
    }

    @Override
    public Optional<QuestionTopic> findQuestionTopicByQuestionIdAndTopicEnum(
            final String questionId, final LeetcodeTopicEnum topicEnum) {
        String sql = """
                SELECT
                    id,
                    "questionId",
                    "questionBankId",
                    "topicSlug",
                    "createdAt",
                    "topic"
                FROM
                    "QuestionTopic" qt
                WHERE
                    qt."questionId" = :questionId
                AND
                    qt.topic = :topic
                """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("questionId", questionId);
            stmt.setString("topic", topicEnum.getLeetcodeEnum());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToQuestionTopic(rs));
                }
            }

            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get question topic by question ID and topic enum", e);
        }
    }

    @Override
    public void createQuestionTopic(final QuestionTopic questionTopic) {
        String sql = """
                INSERT INTO "QuestionTopic" ("id", "questionId", "questionBankId", "topicSlug", "topic", "createdAt")
                VALUES (:id, :questionId, :questionBankId, :topicSlug, :topic, :createdAt)
                """;

        questionTopic.setId(UUID.randomUUID().toString());
        questionTopic.setCreatedAt(java.time.LocalDateTime.now());

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", questionTopic.getId());
            stmt.setString(
                    "questionId",
                    questionTopic.getQuestionId().orElse(null));
            stmt.setString(
                    "questionBankId",
                    questionTopic.getQuestionBankId().orElse(null));
            stmt.setString("topicSlug", questionTopic.getTopicSlug());
            stmt.setString("topic", questionTopic.getTopic().getLeetcodeEnum());
            stmt.setObject("createdAt", questionTopic.getCreatedAt());

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create question topic", e);
        }
    }

    @Override
    public boolean updateQuestionTopicById(final QuestionTopic questionTopic) {
        String sql = """
                UPDATE "QuestionTopic"
                SET
                    "questionId" = :questionId,
                    "questionBankId" = :questionBankId,
                    "topicSlug" = :topicSlug,
                    "topic" = :topic
                WHERE id = :id
                """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", questionTopic.getId());
            stmt.setString("questionId", questionTopic.getQuestionId().orElse(null));
            stmt.setString("questionBankId", questionTopic.getQuestionBankId().orElse(null));
            stmt.setString("topicSlug", questionTopic.getTopicSlug());
            stmt.setString("topic", questionTopic.getTopic().getLeetcodeEnum());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update question topic by ID", e);
        }
    }

    @Override
    public boolean deleteQuestionTopicById(final String id) {
        String sql = """
                DELETE FROM "QuestionTopic"
                WHERE id = :id
                """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete tag by tag ID", e);
        }
    }
}
