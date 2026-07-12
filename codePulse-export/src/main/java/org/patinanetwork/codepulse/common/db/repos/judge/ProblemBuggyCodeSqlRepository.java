package org.patinanetwork.codepulse.common.db.repos.judge;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codepulse.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeLanguage;
import org.patinanetwork.codepulse.common.db.models.judge.Problem;
import org.patinanetwork.codepulse.common.db.models.judge.ProblemBuggyCode;
import org.patinanetwork.codepulse.common.db.models.question.QuestionDifficulty;
import org.springframework.stereotype.Component;

@Component
public class ProblemBuggyCodeSqlRepository implements ProblemBuggyCodeRepository {

    private final DataSource ds;

    public ProblemBuggyCodeSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private ProblemBuggyCode parse(final ResultSet rs) throws SQLException {
        return ProblemBuggyCode.builder()
                .id(rs.getString("id"))
                .problemId(rs.getString("problem_id"))
                .language(JudgeLanguage.valueOf(rs.getString("language")))
                .buggyCode(rs.getString("buggy_code"))
                .build();
    }

    private Problem parseProblem(final ResultSet rs) throws SQLException {
        return Problem.builder()
                .id(rs.getString("id"))
                .title(rs.getString("title"))
                .slug(rs.getString("slug"))
                .difficulty(QuestionDifficulty.valueOf(rs.getString("difficulty")))
                .statement(rs.getString("statement"))
                .constraints(rs.getString("constraints"))
                .timeLimitMs(rs.getInt("time_limit_ms"))
                .memoryLimitKb(rs.getInt("memory_limit_kb"))
                .createdBy(rs.getString("created_by"))
                .createdAt(rs.getObject("created_at", OffsetDateTime.class))
                .updatedAt(rs.getObject("updated_at", OffsetDateTime.class))
                .build();
    }

    @Override
    public ProblemBuggyCode createBuggyCode(final ProblemBuggyCode buggyCode) {
        String sql =
                """
            INSERT INTO `problem_buggy_code`
                (id, problem_id, language, buggy_code)
            VALUES
                (:id, :problemId, :language, :buggyCode)
            """;

        buggyCode.setId(UUID.randomUUID().toString());

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", buggyCode.getId());
            stmt.setString("problemId", buggyCode.getProblemId());
            stmt.setString("language", buggyCode.getLanguage().name());
            stmt.setString("buggyCode", buggyCode.getBuggyCode());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating buggy code", e);
        }

        return buggyCode;
    }

    @Override
    public List<ProblemBuggyCode> getBuggyCodeByProblemId(final String problemId) {
        String sql = "SELECT * FROM `problem_buggy_code` WHERE problem_id = :problemId";
        List<ProblemBuggyCode> results = new ArrayList<>();
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("problemId", problemId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(parse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving buggy code", e);
        }
        return results;
    }

    @Override
    public ProblemBuggyCode getBuggyCodeByProblemIdAndLanguage(final String problemId, final JudgeLanguage language) {
        String sql = "SELECT * FROM `problem_buggy_code` WHERE problem_id = :problemId AND language = :language";
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("problemId", problemId);
            stmt.setString("language", language.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parse(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving buggy code", e);
        }
        return null;
    }

    @Override
    public List<Problem> getProblemsWithBuggyCode() {
        String sql =
                """
            SELECT DISTINCT p.*
            FROM `problem` p
            JOIN `problem_buggy_code` pbc ON pbc.problem_id = p.id
            """;
        List<Problem> results = new ArrayList<>();
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(parseProblem(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving problems with buggy code", e);
        }
        return results;
    }
}
