package org.patinanetwork.codepulse.common.db.repos.question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codepulse.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codepulse.common.db.models.question.Question;
import org.patinanetwork.codepulse.common.db.models.question.QuestionDifficulty;
import org.patinanetwork.codepulse.common.db.models.question.QuestionWithUser;
import org.patinanetwork.codepulse.common.db.models.question.topic.LeetcodeTopicEnum;
import org.patinanetwork.codepulse.common.db.repos.question.topic.QuestionTopicRepository;
import org.patinanetwork.codepulse.common.db.repos.question.topic.service.QuestionTopicService;
import org.springframework.stereotype.Component;

@Component
public class QuestionSqlRepository implements QuestionRepository {

    private DataSource ds;
    private final QuestionTopicRepository questionTopicRepository;
    private final QuestionTopicService questionTopicService;

    private Question mapResultSetToQuestion(final ResultSet rs) throws SQLException {
        var questionId = rs.getString("id");
        var userId = rs.getString("userId");
        var questionSlug = rs.getString("questionSlug");
        var questionDifficulty = QuestionDifficulty.valueOf(rs.getString("questionDifficulty"));
        var questionNumber = rs.getInt("questionNumber");
        var questionLink = rs.getString("questionLink");
        int points = rs.getInt("pointsAwarded");
        Optional<Integer> pointsAwarded = rs.wasNull() ? Optional.empty() : Optional.of(points);
        var questionTitle = rs.getString("questionTitle");
        var acceptanceRate = rs.getFloat("acceptanceRate");
        var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
        var submittedAt = rs.getTimestamp("submittedAt").toLocalDateTime();

        return Question.builder()
                .id(questionId)
                .userId(userId)
                .questionSlug(questionSlug)
                .questionDifficulty(questionDifficulty)
                .questionNumber(questionNumber)
                .questionLink(questionLink)
                .pointsAwarded(pointsAwarded)
                .questionTitle(questionTitle)
                .description(Optional.ofNullable(rs.getString("description")))
                .acceptanceRate(acceptanceRate)
                .createdAt(createdAt)
                .submittedAt(submittedAt)
                .runtime(Optional.ofNullable(rs.getString("runtime")))
                .memory(Optional.ofNullable(rs.getString("memory")))
                .code(Optional.ofNullable(rs.getString("code")))
                .language(Optional.ofNullable(rs.getString("language")))
                .submissionId(Optional.ofNullable(rs.getString("submissionId")))
                .topics(questionTopicRepository.findQuestionTopicsByQuestionId(questionId))
                .build();
    }

    private QuestionWithUser mapResultSetToQuestionWithUser(final ResultSet rs) throws SQLException {
        var questionId = rs.getString("id");
        var userId = rs.getString("userId");
        var questionSlug = rs.getString("questionSlug");
        var questionDifficulty = QuestionDifficulty.valueOf(rs.getString("questionDifficulty"));
        var questionNumber = rs.getInt("questionNumber");
        var questionLink = rs.getString("questionLink");
        int points = rs.getInt("pointsAwarded");
        Optional<Integer> pointsAwarded = rs.wasNull() ? Optional.empty() : Optional.of(points);
        var questionTitle = rs.getString("questionTitle");
        var acceptanceRate = rs.getFloat("acceptanceRate");
        var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
        var submittedAt = rs.getTimestamp("submittedAt").toLocalDateTime();

        return QuestionWithUser.builder()
                .id(questionId)
                .userId(userId)
                .questionSlug(questionSlug)
                .questionDifficulty(questionDifficulty)
                .questionNumber(questionNumber)
                .questionLink(questionLink)
                .pointsAwarded(pointsAwarded)
                .questionTitle(questionTitle)
                .description(Optional.ofNullable(rs.getString("description")))
                .acceptanceRate(acceptanceRate)
                .createdAt(createdAt)
                .submittedAt(submittedAt)
                .runtime(Optional.ofNullable(rs.getString("runtime")))
                .memory(Optional.ofNullable(rs.getString("memory")))
                .code(Optional.ofNullable(rs.getString("code")))
                .language(Optional.ofNullable(rs.getString("language")))
                .submissionId(Optional.ofNullable(rs.getString("submissionId")))
                .discordName(Optional.ofNullable(rs.getString("discordName")))
                .leetcodeUsername(Optional.ofNullable(rs.getString("leetcodeUsername")))
                .nickname(Optional.ofNullable(rs.getString("nickname")))
                .topics(questionTopicRepository.findQuestionTopicsByQuestionId(questionId))
                .build();
    }

    public QuestionSqlRepository(
            final DataSource ds,
            final QuestionTopicRepository questionTopicRepository,
            final QuestionTopicService questionTopicService) {
        this.ds = ds;
        this.questionTopicRepository = questionTopicRepository;
        this.questionTopicService = questionTopicService;
    }

    @Override
    public Question createQuestion(final Question question) {
        String sql = """
                INSERT INTO "Question" (
                    id,
                    "userId",
                    "questionSlug",
                    "questionDifficulty",
                    "questionNumber",
                    "questionLink",
                    "pointsAwarded",
                    "questionTitle",
                    description,
                    "acceptanceRate",
                    "submittedAt",
                    runtime,
                    memory,
                    code,
                    language,
                    "submissionId"
                )
                VALUES
                    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        question.setId(UUID.randomUUID().toString());

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, question.getId());
            stmt.setString(2, question.getUserId());
            stmt.setString(3, question.getQuestionSlug());
            stmt.setString(4, question.getQuestionDifficulty().name());
            stmt.setInt(5, question.getQuestionNumber());
            stmt.setString(6, question.getQuestionLink());

            if (question.getPointsAwarded().isPresent()) {
                stmt.setInt(7, question.getPointsAwarded().get());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            stmt.setString(8, question.getQuestionTitle());
            stmt.setString(9, question.getDescription().orElse(null));

            stmt.setFloat(10, question.getAcceptanceRate());
            stmt.setObject(11, question.getSubmittedAt());
            stmt.setString(12, question.getRuntime().orElse(null));
            stmt.setString(13, question.getMemory().orElse(null));
            stmt.setString(14, question.getCode().orElse(null));
            stmt.setString(15, question.getLanguage().orElse(null));
            stmt.setString(16, question.getSubmissionId().orElse(null));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return getQuestionById(question.getId())
                        .orElseThrow(() -> new RuntimeException("Failed to retrieve created question."));
            } else {
                throw new RuntimeException("Failed to create question.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create question", e);
        }
    }

    @Override
    public Optional<Question> getQuestionById(final String id) {
        String sql = """
                SELECT
                    id,
                    "userId",
                    "questionSlug",
                    "questionDifficulty",
                    "questionNumber",
                    "questionLink",
                    "pointsAwarded",
                    "questionTitle",
                    description,
                    "acceptanceRate",
                    "createdAt",
                    "submittedAt",
                    runtime,
                    memory,
                    code,
                    language,
                    "submissionId"
                FROM
                    "Question"
                WHERE
                    id = ?
            """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve question", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<QuestionWithUser> getQuestionWithUserById(final String id) {
        String sql = """
                SELECT
                    q.id,
                    q."userId",
                    q."questionSlug",
                    q."questionDifficulty",
                    q."questionNumber",
                    q."questionLink",
                    q."pointsAwarded",
                    q."questionTitle",
                    q.description,
                    q."acceptanceRate",
                    q."createdAt",
                    q."submittedAt",
                    q.runtime,
                    q.memory,
                    q.code,
                    q.language,
                    q."submissionId",
                    u."discordName",
                    u."leetcodeUsername",
                    u.nickname
                FROM "Question" q
                JOIN "User" u ON q."userId" = u.id
                WHERE q.id = ?
            """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToQuestionWithUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve question", e);
        }

        return Optional.empty();
    }

    @Override
    public ArrayList<Question> getQuestionsByUserId(
            final String userId,
            final int page,
            final int pageSize,
            final String query,
            final boolean pointFilter,
            final LeetcodeTopicEnum[] topics,
            final OffsetDateTime startDate,
            final OffsetDateTime endDate) {
        ArrayList<Question> questions = new ArrayList<>();

        // MySQL has no array types / ANY()/DISTINCT ON. Topic filtering is expressed as an EXISTS
        // subquery against a dynamically-sized IN (...) list, and "one row per question" is achieved
        // via GROUP BY instead of Postgres' DISTINCT ON.
        StringBuilder topicsInClause = new StringBuilder();
        for (int i = 0; i < topics.length; i++) {
            if (i > 0) {
                topicsInClause.append(", ");
            }
            topicsInClause.append(":topic").append(i);
        }

        String topicFilter = topics.length == 0
                ? "1 = 1"
                : "EXISTS (SELECT 1 FROM \"QuestionTopic\" t WHERE t.\"questionId\" = q.id AND t.\"topic\" IN ("
                        + topicsInClause + "))";

        String sql =
                """
            SELECT
                q.id,
                q."userId",
                q."questionSlug",
                q."questionDifficulty",
                q."questionNumber",
                q."questionLink",
                q."pointsAwarded",
                q."questionTitle",
                q.description,
                q."acceptanceRate",
                q."createdAt",
                q."submittedAt",
                q.runtime,
                q.memory,
                q.code,
                q.language,
                q."submissionId"
            FROM
                "Question" q
            JOIN "User" u ON q."userId" = u.id
            WHERE
                q."userId" = :userId
                AND q."questionTitle" LIKE :query
                AND (NOT :pointFilter OR q."pointsAwarded" <> 0)
                AND (%s)
                AND (:startDate IS NULL OR q."createdAt" >= :startDate)
                AND (:endDate IS NULL OR q."createdAt" <= :endDate)
            GROUP BY q.id
            ORDER BY q."submittedAt" DESC
            LIMIT :pageSize OFFSET :offset
            """
                        .formatted(topicFilter);

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("userId", userId);
            stmt.setString("query", "%" + query + "%");
            stmt.setBoolean("pointFilter", pointFilter);

            for (int i = 0; i < topics.length; i++) {
                stmt.setString("topic" + i, topics[i].getLeetcodeEnum());
            }
            if (startDate == null) {
                stmt.setNull("startDate", Types.TIMESTAMP);
            } else {
                stmt.setObject("startDate", startDate);
            }
            if (endDate == null) {
                stmt.setNull("endDate", Types.TIMESTAMP);
            } else {
                stmt.setObject("endDate", endDate);
            }
            stmt.setInt("pageSize", pageSize);
            stmt.setInt("offset", (page - 1) * pageSize);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Question question = mapResultSetToQuestion(rs);
                    questions.add(question);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve questions", e);
        }

        return questions;
    }

    @Override
    public Question updateQuestion(final Question inputQuestion) {
        String sql = """
                UPDATE "Question"
                SET
                    "userId" = ?,
                    "questionSlug" = ?,
                    "questionDifficulty" = ?,
                    "questionNumber" = ?,
                    "questionLink" = ?,
                    "pointsAwarded" = ?,
                    "questionTitle" = ?,
                    description = ?,
                    "acceptanceRate" = ?,
                    "submittedAt" = ?,
                    runtime = ?,
                    memory = ?,
                    code = ?,
                    language = ?,
                    "submissionId" = ?
                WHERE
                    id = ?
            """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inputQuestion.getUserId());
            stmt.setString(2, inputQuestion.getQuestionSlug());
            stmt.setString(3, inputQuestion.getQuestionDifficulty().name());
            stmt.setInt(4, inputQuestion.getQuestionNumber());
            stmt.setString(5, inputQuestion.getQuestionLink());

            if (inputQuestion.getPointsAwarded().isPresent()) {
                stmt.setInt(6, inputQuestion.getPointsAwarded().get());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            stmt.setString(7, inputQuestion.getQuestionTitle());
            stmt.setString(8, inputQuestion.getDescription().orElse(null));
            stmt.setFloat(9, inputQuestion.getAcceptanceRate());
            stmt.setObject(10, inputQuestion.getSubmittedAt());
            stmt.setString(11, inputQuestion.getRuntime().orElse(null));
            stmt.setString(12, inputQuestion.getMemory().orElse(null));
            stmt.setString(13, inputQuestion.getCode().orElse(null));
            stmt.setString(14, inputQuestion.getLanguage().orElse(null));
            stmt.setString(15, inputQuestion.getSubmissionId().orElse(null));
            stmt.setString(16, inputQuestion.getId());

            stmt.executeUpdate();

            return getQuestionById(inputQuestion.getId())
                    .orElseThrow(() -> new RuntimeException("Failed to retrieve updated question."));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update question", e);
        }
    }

    @Override
    public boolean deleteQuestionById(final String id) {
        String sql = "DELETE FROM \"Question\" WHERE id=?";

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting session", e);
        }
    }

    @Override
    public Optional<Question> getQuestionBySlugAndUserId(final String slug, final String inputtedUserId) {
        String sql = """
                SELECT
                    id,
                    "userId",
                    "questionSlug",
                    "questionDifficulty",
                    "questionNumber",
                    "questionLink",
                    "pointsAwarded",
                    "questionTitle",
                    description,
                    "acceptanceRate",
                    "createdAt",
                    "submittedAt",
                    runtime,
                    memory,
                    code,
                    language,
                    "submissionId"
                FROM
                    "Question"
                WHERE
                    "questionSlug" = ?
                AND
                    "userId" = ?
                LIMIT 1
            """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, slug);
            stmt.setString(2, inputtedUserId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve question", e);
        }

        return Optional.empty();
    }

    @Override
    public int getQuestionCountByUserId(
            final String userId,
            final String query,
            final boolean pointFilter,
            final Set<String> topics,
            final OffsetDateTime startDate,
            final OffsetDateTime endDate) {
        LeetcodeTopicEnum[] topicEnums = questionTopicService.stringsToEnums(topics);

        StringBuilder topicsInClause = new StringBuilder();
        for (int i = 0; i < topicEnums.length; i++) {
            if (i > 0) {
                topicsInClause.append(", ");
            }
            topicsInClause.append(":topic").append(i);
        }

        String topicFilter = topicEnums.length == 0
                ? "1 = 1"
                : "EXISTS (SELECT 1 FROM \"QuestionTopic\" qt WHERE qt.\"questionId\" = q.id AND qt.\"topic\" IN ("
                        + topicsInClause + "))";

        String sql =
                """
            SELECT
                COUNT(DISTINCT q.id)
            FROM
                "Question" q
            WHERE
                q."userId" = :userId
            AND
                q."questionTitle" LIKE :title
            AND
                (NOT :pointFilter OR q."pointsAwarded" <> 0)
            AND (%s)
            AND (:startDate IS NULL OR q."createdAt" >= :startDate)
            AND (:endDate IS NULL OR q."createdAt" <= :endDate)
            """
                        .formatted(topicFilter);

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("userId", userId);
            stmt.setString("title", "%" + query + "%");
            stmt.setBoolean("pointFilter", pointFilter);
            for (int i = 0; i < topicEnums.length; i++) {
                stmt.setString("topic" + i, topicEnums[i].getLeetcodeEnum());
            }
            if (startDate == null) {
                stmt.setNull("startDate", Types.TIMESTAMP);
            } else {
                stmt.setObject("startDate", startDate);
            }
            if (endDate == null) {
                stmt.setNull("endDate", Types.TIMESTAMP);
            } else {
                stmt.setObject("endDate", endDate);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve questions", e);
        }

        return 0;
    }

    @Override
    public boolean questionExistsBySubmissionId(final String submissionId) {
        String sql = """
                SELECT
                    id
                FROM
                    "Question"
                WHERE
                    "submissionId" = ?
            """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, submissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve question", e);
        }
    }

    @Override
    public ArrayList<Question> getAllIncompleteQuestions() {
        ArrayList<Question> questions = new ArrayList<>();
        String sql = """
            SELECT
                *
            FROM
                "Question"
            WHERE
                ("runtime" IS NULL OR "runtime" = '')
                OR ("memory" IS NULL OR "memory" = '')
                OR ("code" is NULL OR "code" = '')
                OR ("language" is NULL OR "language" = '')
            """;
        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapResultSetToQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all incomplete questions", e);
        }

        return questions;
    }

    @Override
    public List<Question> getAllQuestionsWithNoTopics() {
        List<Question> result = new ArrayList<>();

        String sql = """
            SELECT
                q.id,
                q."userId",
                q."questionSlug",
                q."questionDifficulty",
                q."questionNumber",
                q."questionLink",
                q."pointsAwarded",
                q."questionTitle",
                q.description,
                q."acceptanceRate",
                q."createdAt",
                q."submittedAt",
                q.runtime,
                q.memory,
                q.code,
                q.language,
                q."submissionId"
            FROM
                "Question" q
            WHERE NOT EXISTS (
                SELECT 1
                FROM "QuestionTopic" qt
                WHERE qt."questionId" = q.id
            );
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToQuestion(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all questions with no topics", e);
        }
    }

    @Override
    public ArrayList<QuestionWithUser> getAllIncompleteQuestionsWithUser() {
        ArrayList<QuestionWithUser> questions = new ArrayList<>();
        String sql = """
            SELECT
                q.id,
                q."userId",
                q."questionSlug",
                q."questionDifficulty",
                q."questionNumber",
                q."questionLink",
                q."pointsAwarded",
                q."questionTitle",
                q.description,
                q."acceptanceRate",
                q."createdAt",
                q."submittedAt",
                q.runtime,
                q.memory,
                q.code,
                q.language,
                q."submissionId",
                u."discordName",
                u."leetcodeUsername",
                u.nickname
            FROM
                "Question" q
            JOIN
                "User" u ON q."userId" = u.id
            WHERE
                (q."runtime" IS NULL OR q."runtime" = '')
                OR (q."memory" IS NULL OR q."memory" = '')
                OR (q."code" IS NULL OR q."code" = '')
                OR (q."language" IS NULL OR q."language" = '')
            ORDER BY
                q."submittedAt" DESC
            """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    QuestionWithUser question = mapResultSetToQuestionWithUser(rs);
                    questions.add(question);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all incomplete questions with user", e);
        }

        return questions;
    }
}
