package org.patinanetwork.codepulse.common.db.repos.judge;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codepulse.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeLanguage;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeSubmission;
import org.patinanetwork.codepulse.common.db.models.judge.SubmissionStatus;
import org.springframework.stereotype.Component;

@Component
public class JudgeSubmissionSqlRepository implements JudgeSubmissionRepository {

    private final DataSource ds;

    public JudgeSubmissionSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private JudgeSubmission parse(final ResultSet rs) throws SQLException {
        return JudgeSubmission.builder()
                .id(rs.getString("id"))
                .userId(rs.getString("user_id"))
                .problemId(rs.getString("problem_id"))
                .lobbyId(Optional.ofNullable(rs.getString("lobby_id")))
                .language(JudgeLanguage.valueOf(rs.getString("language")))
                .sourceCode(rs.getString("source_code"))
                .status(SubmissionStatus.valueOf(rs.getString("status")))
                .testCasesPassed(rs.getInt("test_cases_passed"))
                .testCasesTotal(rs.getInt("test_cases_total"))
                .maxRuntimeMs(Optional.ofNullable((Integer) rs.getObject("max_runtime_ms")))
                .maxMemoryKb(Optional.ofNullable((Integer) rs.getObject("max_memory_kb")))
                .pointsAwarded(rs.getInt("points_awarded"))
                .createdAt(rs.getObject("created_at", OffsetDateTime.class))
                .completedAt(Optional.ofNullable(rs.getObject("completed_at", OffsetDateTime.class)))
                .build();
    }

    @Override
    public JudgeSubmission createSubmission(final JudgeSubmission submission) {
        String sql =
                """
            INSERT INTO `judge_submission`
                (id, user_id, problem_id, lobby_id, language, source_code, status, test_cases_passed, test_cases_total, created_at)
            VALUES
                (:id, :userId, :problemId, :lobbyId, :language, :sourceCode, :status, :testCasesPassed, :testCasesTotal, :createdAt)
            """;

        submission.setId(UUID.randomUUID().toString());
        submission.setCreatedAt(OffsetDateTime.now());

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", submission.getId());
            stmt.setString("userId", submission.getUserId());
            stmt.setString("problemId", submission.getProblemId());
            stmt.setString("lobbyId", submission.getLobbyId().orElse(null));
            stmt.setString("language", submission.getLanguage().name());
            stmt.setString("sourceCode", submission.getSourceCode());
            stmt.setString("status", submission.getStatus().name());
            stmt.setInt("testCasesPassed", submission.getTestCasesPassed());
            stmt.setInt("testCasesTotal", submission.getTestCasesTotal());
            stmt.setObject("createdAt", submission.getCreatedAt());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating judge submission", e);
        }

        return submission;
    }

    @Override
    public JudgeSubmission getSubmissionById(final String id) {
        String sql = "SELECT * FROM `judge_submission` WHERE id = :id";
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parse(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving judge submission", e);
        }
        return null;
    }

    @Override
    public Optional<JudgeSubmission> getAcceptedSubmissionByUserAndProblem(final String userId, final String problemId) {
        String sql =
                """
            SELECT * FROM `judge_submission`
            WHERE user_id = :userId AND problem_id = :problemId AND status = 'ACCEPTED'
            LIMIT 1
            """;
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("userId", userId);
            stmt.setString("problemId", problemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving accepted submission", e);
        }
        return Optional.empty();
    }

    @Override
    public List<JudgeSubmission> getSubmissionsByStatuses(final List<SubmissionStatus> statuses) {
        if (statuses.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < statuses.size(); i++) {
            placeholders.append(i == 0 ? "" : ", ").append(":status").append(i);
        }
        String sql = "SELECT * FROM `judge_submission` WHERE status IN (" + placeholders + ")";
        List<JudgeSubmission> results = new ArrayList<>();
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            for (int i = 0; i < statuses.size(); i++) {
                stmt.setString("status" + i, statuses.get(i).name());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(parse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving submissions by status", e);
        }
        return results;
    }

    @Override
    public List<JudgeSubmission> getSubmissionsByUserAndLobbyAndProblem(
            final String userId, final String lobbyId, final String problemId) {
        String sql =
                """
            SELECT * FROM `judge_submission`
            WHERE user_id = :userId AND lobby_id = :lobbyId AND problem_id = :problemId
            ORDER BY created_at ASC
            """;
        List<JudgeSubmission> results = new ArrayList<>();
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("userId", userId);
            stmt.setString("lobbyId", lobbyId);
            stmt.setString("problemId", problemId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(parse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving submissions", e);
        }
        return results;
    }

    @Override
    public void updateSubmissionStatus(
            final String id,
            final SubmissionStatus status,
            final int testCasesPassed,
            final int testCasesTotal,
            final Integer maxRuntimeMs,
            final Integer maxMemoryKb,
            final int pointsAwarded) {
        String sql =
                """
            UPDATE `judge_submission`
            SET status = :status,
                test_cases_passed = :testCasesPassed,
                test_cases_total = :testCasesTotal,
                max_runtime_ms = :maxRuntimeMs,
                max_memory_kb = :maxMemoryKb,
                points_awarded = :pointsAwarded,
                completed_at = :completedAt
            WHERE id = :id
            """;
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("status", status.name());
            stmt.setInt("testCasesPassed", testCasesPassed);
            stmt.setInt("testCasesTotal", testCasesTotal);
            if (maxRuntimeMs != null) {
                stmt.setInt("maxRuntimeMs", maxRuntimeMs);
            } else {
                stmt.setNull("maxRuntimeMs", java.sql.Types.INTEGER);
            }
            if (maxMemoryKb != null) {
                stmt.setInt("maxMemoryKb", maxMemoryKb);
            } else {
                stmt.setNull("maxMemoryKb", java.sql.Types.INTEGER);
            }
            stmt.setInt("pointsAwarded", pointsAwarded);
            stmt.setObject("completedAt", OffsetDateTime.now());
            stmt.setString("id", id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating judge submission", e);
        }
    }
}
