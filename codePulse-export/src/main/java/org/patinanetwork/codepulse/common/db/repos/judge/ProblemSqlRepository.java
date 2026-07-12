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
import org.patinanetwork.codepulse.common.db.models.judge.Problem;
import org.patinanetwork.codepulse.common.db.models.judge.ProblemCategory;
import org.patinanetwork.codepulse.common.db.models.question.QuestionDifficulty;
import org.springframework.stereotype.Component;

@Component
public class ProblemSqlRepository implements ProblemRepository {

    private final DataSource ds;
    private final TestCaseRepository testCaseRepository;

    public ProblemSqlRepository(final DataSource ds, final TestCaseRepository testCaseRepository) {
        this.ds = ds;
        this.testCaseRepository = testCaseRepository;
    }

    private Problem parseResultSetToProblem(final ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        return Problem.builder()
                .id(id)
                .title(rs.getString("title"))
                .slug(rs.getString("slug"))
                .difficulty(QuestionDifficulty.valueOf(rs.getString("difficulty")))
                .category(ProblemCategory.valueOf(rs.getString("category")))
                .statement(rs.getString("statement"))
                .constraints(rs.getString("constraints"))
                .timeLimitMs(rs.getInt("time_limit_ms"))
                .memoryLimitKb(rs.getInt("memory_limit_kb"))
                .createdBy(rs.getString("created_by"))
                .createdAt(rs.getObject("created_at", OffsetDateTime.class))
                .updatedAt(rs.getObject("updated_at", OffsetDateTime.class))
                .testCases(testCaseRepository.getTestCasesByProblemId(id))
                .build();
    }

    @Override
    public Problem createProblem(final Problem problem) {
        String sql =
                """
            INSERT INTO `problem`
                (id, title, slug, difficulty, category, statement, constraints, time_limit_ms, memory_limit_kb, created_by, created_at, updated_at)
            VALUES
                (:id, :title, :slug, :difficulty, :category, :statement, :constraints, :timeLimitMs, :memoryLimitKb, :createdBy, :createdAt, :updatedAt)
            """;

        problem.setId(UUID.randomUUID().toString());
        OffsetDateTime now = OffsetDateTime.now();
        problem.setCreatedAt(now);
        problem.setUpdatedAt(now);
        if (problem.getCategory() == null) {
            problem.setCategory(ProblemCategory.DSA);
        }

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", problem.getId());
            stmt.setString("title", problem.getTitle());
            stmt.setString("slug", problem.getSlug());
            stmt.setString("difficulty", problem.getDifficulty().name());
            stmt.setString("category", problem.getCategory().name());
            stmt.setString("statement", problem.getStatement());
            stmt.setString("constraints", problem.getConstraints());
            stmt.setInt("timeLimitMs", problem.getTimeLimitMs());
            stmt.setInt("memoryLimitKb", problem.getMemoryLimitKb());
            stmt.setString("createdBy", problem.getCreatedBy());
            stmt.setObject("createdAt", problem.getCreatedAt());
            stmt.setObject("updatedAt", problem.getUpdatedAt());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating problem", e);
        }

        return problem;
    }

    @Override
    public Problem getProblemById(final String id) {
        String sql = "SELECT * FROM `problem` WHERE id = :id";
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToProblem(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving problem", e);
        }
        return null;
    }

    @Override
    public Problem getProblemBySlug(final String slug) {
        String sql = "SELECT * FROM `problem` WHERE slug = :slug";
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("slug", slug);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToProblem(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving problem", e);
        }
        return null;
    }

    @Override
    public java.util.Optional<Problem> getRandomProblem() {
        String sql = "SELECT * FROM `problem` ORDER BY RAND() LIMIT 1";
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return java.util.Optional.of(parseResultSetToProblem(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving random problem", e);
        }
        return java.util.Optional.empty();
    }

    @Override
    public List<Problem> getAllProblems() {
        String sql = "SELECT * FROM `problem` ORDER BY created_at DESC";
        List<Problem> problems = new ArrayList<>();
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    problems.add(parseResultSetToProblem(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving problems", e);
        }
        return problems;
    }

    @Override
    public void updateProblem(final Problem problem) {
        String sql =
                """
            UPDATE `problem`
            SET title = :title,
                slug = :slug,
                difficulty = :difficulty,
                category = :category,
                statement = :statement,
                constraints = :constraints,
                time_limit_ms = :timeLimitMs,
                memory_limit_kb = :memoryLimitKb,
                updated_at = :updatedAt
            WHERE id = :id
            """;

        problem.setUpdatedAt(OffsetDateTime.now());

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("title", problem.getTitle());
            stmt.setString("slug", problem.getSlug());
            stmt.setString("difficulty", problem.getDifficulty().name());
            stmt.setString("category", problem.getCategory().name());
            stmt.setString("statement", problem.getStatement());
            stmt.setString("constraints", problem.getConstraints());
            stmt.setInt("timeLimitMs", problem.getTimeLimitMs());
            stmt.setInt("memoryLimitKb", problem.getMemoryLimitKb());
            stmt.setObject("updatedAt", problem.getUpdatedAt());
            stmt.setString("id", problem.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating problem", e);
        }
    }

    @Override
    public void deleteProblem(final String id) {
        String sql = "DELETE FROM `problem` WHERE id = :id";
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting problem", e);
        }
    }
}
