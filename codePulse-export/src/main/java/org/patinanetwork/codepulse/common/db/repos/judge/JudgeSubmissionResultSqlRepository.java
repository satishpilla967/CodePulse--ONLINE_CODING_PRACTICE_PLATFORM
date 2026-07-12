package org.patinanetwork.codepulse.common.db.repos.judge;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codepulse.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeSubmissionResult;
import org.patinanetwork.codepulse.common.db.models.judge.SubmissionStatus;
import org.springframework.stereotype.Component;

@Component
public class JudgeSubmissionResultSqlRepository implements JudgeSubmissionResultRepository {

    private final DataSource ds;

    public JudgeSubmissionResultSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private JudgeSubmissionResult parse(final ResultSet rs) throws SQLException {
        return JudgeSubmissionResult.builder()
                .id(rs.getString("id"))
                .judgeSubmissionId(rs.getString("judge_submission_id"))
                .testCaseId(rs.getString("test_case_id"))
                .judge0Token(Optional.ofNullable(rs.getString("judge0_token")))
                .status(SubmissionStatus.valueOf(rs.getString("status")))
                .stdout(Optional.ofNullable(rs.getString("stdout")))
                .stderr(Optional.ofNullable(rs.getString("stderr")))
                .runtimeMs(Optional.ofNullable((Integer) rs.getObject("runtime_ms")))
                .memoryKb(Optional.ofNullable((Integer) rs.getObject("memory_kb")))
                .build();
    }

    @Override
    public JudgeSubmissionResult createResult(final JudgeSubmissionResult result) {
        String sql =
                """
            INSERT INTO `judge_submission_result`
                (id, judge_submission_id, test_case_id, judge0_token, status)
            VALUES
                (:id, :judgeSubmissionId, :testCaseId, :judge0Token, :status)
            """;

        result.setId(UUID.randomUUID().toString());

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", result.getId());
            stmt.setString("judgeSubmissionId", result.getJudgeSubmissionId());
            stmt.setString("testCaseId", result.getTestCaseId());
            stmt.setString("judge0Token", result.getJudge0Token().orElse(null));
            stmt.setString("status", result.getStatus().name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating judge submission result", e);
        }

        return result;
    }

    @Override
    public List<JudgeSubmissionResult> getResultsBySubmissionId(final String submissionId) {
        String sql = "SELECT * FROM `judge_submission_result` WHERE judge_submission_id = :submissionId";
        List<JudgeSubmissionResult> results = new ArrayList<>();
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("submissionId", submissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(parse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving judge submission results", e);
        }
        return results;
    }

    @Override
    public void updateResult(
            final String id,
            final SubmissionStatus status,
            final String stdout,
            final String stderr,
            final Integer runtimeMs,
            final Integer memoryKb) {
        String sql =
                """
            UPDATE `judge_submission_result`
            SET status = :status,
                stdout = :stdout,
                stderr = :stderr,
                runtime_ms = :runtimeMs,
                memory_kb = :memoryKb
            WHERE id = :id
            """;
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("status", status.name());
            stmt.setString("stdout", stdout);
            stmt.setString("stderr", stderr);
            if (runtimeMs != null) {
                stmt.setInt("runtimeMs", runtimeMs);
            } else {
                stmt.setNull("runtimeMs", java.sql.Types.INTEGER);
            }
            if (memoryKb != null) {
                stmt.setInt("memoryKb", memoryKb);
            } else {
                stmt.setNull("memoryKb", java.sql.Types.INTEGER);
            }
            stmt.setString("id", id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating judge submission result", e);
        }
    }
}
