package org.patinanetwork.codepulse.common.db.repos.judge;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codepulse.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codepulse.common.db.models.judge.JudgeLanguage;
import org.patinanetwork.codepulse.common.db.models.judge.ProblemStarterCode;
import org.springframework.stereotype.Component;

@Component
public class ProblemStarterCodeSqlRepository implements ProblemStarterCodeRepository {

    private final DataSource ds;

    public ProblemStarterCodeSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private ProblemStarterCode parse(final ResultSet rs) throws SQLException {
        return ProblemStarterCode.builder()
                .id(rs.getString("id"))
                .problemId(rs.getString("problem_id"))
                .language(JudgeLanguage.valueOf(rs.getString("language")))
                .starterCode(rs.getString("starter_code"))
                .build();
    }

    @Override
    public ProblemStarterCode createStarterCode(final ProblemStarterCode starterCode) {
        String sql =
                """
            INSERT INTO `problem_starter_code`
                (id, problem_id, language, starter_code)
            VALUES
                (:id, :problemId, :language, :starterCode)
            """;

        starterCode.setId(UUID.randomUUID().toString());

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", starterCode.getId());
            stmt.setString("problemId", starterCode.getProblemId());
            stmt.setString("language", starterCode.getLanguage().name());
            stmt.setString("starterCode", starterCode.getStarterCode());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating starter code", e);
        }

        return starterCode;
    }

    @Override
    public List<ProblemStarterCode> getStarterCodeByProblemId(final String problemId) {
        String sql = "SELECT * FROM `problem_starter_code` WHERE problem_id = :problemId";
        List<ProblemStarterCode> results = new ArrayList<>();
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("problemId", problemId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(parse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving starter code", e);
        }
        return results;
    }

    @Override
    public ProblemStarterCode getStarterCodeByProblemIdAndLanguage(final String problemId, final JudgeLanguage language) {
        String sql = "SELECT * FROM `problem_starter_code` WHERE problem_id = :problemId AND language = :language";
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
            throw new RuntimeException("Error while retrieving starter code", e);
        }
        return null;
    }
}
