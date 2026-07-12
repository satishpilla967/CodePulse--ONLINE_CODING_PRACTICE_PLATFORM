package org.patinanetwork.codepulse.common.db.repos.judge;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codepulse.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codepulse.common.db.models.judge.TestCase;
import org.springframework.stereotype.Component;

@Component
public class TestCaseSqlRepository implements TestCaseRepository {

    private final DataSource ds;

    public TestCaseSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private TestCase parse(final ResultSet rs) throws SQLException {
        return TestCase.builder()
                .id(rs.getString("id"))
                .problemId(rs.getString("problem_id"))
                .input(rs.getString("input"))
                .expectedOutput(rs.getString("expected_output"))
                .isHidden(rs.getBoolean("is_hidden"))
                .displayOrder(rs.getInt("display_order"))
                .build();
    }

    @Override
    public TestCase createTestCase(final TestCase testCase) {
        String sql =
                """
            INSERT INTO `test_case`
                (id, problem_id, input, expected_output, is_hidden, display_order)
            VALUES
                (:id, :problemId, :input, :expectedOutput, :isHidden, :displayOrder)
            """;

        testCase.setId(UUID.randomUUID().toString());

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", testCase.getId());
            stmt.setString("problemId", testCase.getProblemId());
            stmt.setString("input", testCase.getInput());
            stmt.setString("expectedOutput", testCase.getExpectedOutput());
            stmt.setBoolean("isHidden", testCase.isHidden());
            stmt.setInt("displayOrder", testCase.getDisplayOrder());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating test case", e);
        }

        return testCase;
    }

    @Override
    public List<TestCase> getTestCasesByProblemId(final String problemId) {
        String sql = "SELECT * FROM `test_case` WHERE problem_id = :problemId ORDER BY display_order ASC";
        List<TestCase> results = new ArrayList<>();
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("problemId", problemId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(parse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving test cases", e);
        }
        return results;
    }

    @Override
    public List<TestCase> getPublicTestCasesByProblemId(final String problemId) {
        String sql =
                "SELECT * FROM `test_case` WHERE problem_id = :problemId AND is_hidden = FALSE ORDER BY display_order ASC";
        List<TestCase> results = new ArrayList<>();
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("problemId", problemId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(parse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving public test cases", e);
        }
        return results;
    }

    @Override
    public void deleteTestCase(final String id) {
        String sql = "DELETE FROM `test_case` WHERE id = :id";
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting test case", e);
        }
    }
}
