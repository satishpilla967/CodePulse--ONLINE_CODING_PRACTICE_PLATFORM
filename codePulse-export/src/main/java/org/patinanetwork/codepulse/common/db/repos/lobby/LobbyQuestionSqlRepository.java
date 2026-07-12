package org.patinanetwork.codepulse.common.db.repos.lobby;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codepulse.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codepulse.common.db.models.lobby.LobbyQuestion;
import org.springframework.stereotype.Component;

@Component
public class LobbyQuestionSqlRepository implements LobbyQuestionRepository {

    private final DataSource ds;

    public LobbyQuestionSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private LobbyQuestion parseResultSetToLobbyQuestion(final ResultSet resultSet) throws SQLException {
        return LobbyQuestion.builder()
                .id(resultSet.getString("id"))
                .lobbyId(resultSet.getString("lobbyId"))
                .questionBankId(Optional.ofNullable(resultSet.getString("questionBankId")))
                .problemId(Optional.ofNullable(resultSet.getString("problemId")))
                .userSolvedCount(resultSet.getInt("userSolvedCount"))
                .createdAt(resultSet.getObject("createdAt", OffsetDateTime.class))
                .build();
    }

    @Override
    public void createLobbyQuestion(final LobbyQuestion lobbyQuestion) {
        String sql = """
            INSERT INTO "LobbyQuestion"
                (id, "lobbyId", "questionBankId", "problemId", "userSolvedCount", "createdAt")
            VALUES
                (:id, :lobbyId, :questionBankId, :problemId, :userSolvedCount, :createdAt)
            """;
        lobbyQuestion.setId(UUID.randomUUID().toString());
        lobbyQuestion.setCreatedAt(OffsetDateTime.now());
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", lobbyQuestion.getId());
            stmt.setString("lobbyId", lobbyQuestion.getLobbyId());
            stmt.setString(
                    "questionBankId", lobbyQuestion.getQuestionBankId().orElse(null));
            stmt.setString("problemId", lobbyQuestion.getProblemId().orElse(null));
            stmt.setInt("userSolvedCount", lobbyQuestion.getUserSolvedCount());
            stmt.setObject("createdAt", lobbyQuestion.getCreatedAt());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create lobbyQuestion", e);
        }
    }

    @Override
    public Optional<LobbyQuestion> findLobbyQuestionById(final String id) {
        String sql = """
            SELECT
                id,
                "lobbyId",
                "questionBankId",
                "problemId",
                "userSolvedCount",
                "createdAt"
            FROM
                "LobbyQuestion"
            WHERE
                id = :id
            """;
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobbyQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobbyQuestion by id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<LobbyQuestion> findLobbyQuestionsByLobbyId(final String lobbyId) {
        List<LobbyQuestion> result = new java.util.ArrayList<>();
        String sql = """
            SELECT
                id,
                "lobbyId",
                "questionBankId",
                "problemId",
                "userSolvedCount",
                "createdAt"
            FROM
                "LobbyQuestion"
            WHERE
                "lobbyId" = :lobbyId
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("lobbyId", lobbyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToLobbyQuestion(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobbyQuestions by lobbyId", e);
        }
    }

    @Override
    public List<LobbyQuestion> findLobbyQuestionsByLobbyIdAndQuestionBankId(
            final String lobbyId, final String questionBankId) {
        List<LobbyQuestion> result = new java.util.ArrayList<>();
        String sql = """
            SELECT
                id,
                "lobbyId",
                "questionBankId",
                "problemId",
                "userSolvedCount",
                "createdAt"
            FROM
                "LobbyQuestion"
            WHERE
                "lobbyId" = :lobbyId AND "questionBankId" = :questionBankId
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("lobbyId", lobbyId);
            stmt.setString("questionBankId", questionBankId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToLobbyQuestion(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobbyQuestions by lobbyId and questionBankId", e);
        }
    }

    @Override
    public Optional<LobbyQuestion> findMostRecentLobbyQuestionByLobbyId(final String lobbyId) {
        String sql = """
            SELECT
                id,
                "lobbyId",
                "questionBankId",
                "problemId",
                "userSolvedCount",
                "createdAt"
            FROM
                "LobbyQuestion"
            WHERE
                "lobbyId" = :lobbyId
            ORDER BY
                "createdAt" DESC
            LIMIT 1
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("lobbyId", lobbyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobbyQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find most recent lobbyQuestion by lobbyId", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean updateQuestionLobby(final LobbyQuestion lobbyQuestion) {
        String sql = """
            UPDATE "LobbyQuestion"
            SET
                "userSolvedCount" = :userSolvedCount
            WHERE
                id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setInt("userSolvedCount", lobbyQuestion.getUserSolvedCount());
            stmt.setString("id", lobbyQuestion.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update lobbyQuestion", e);
        }
    }

    @Override
    public List<LobbyQuestion> findAllLobbyQuestions() {
        List<LobbyQuestion> result = new java.util.ArrayList<>();
        String sql = """
            SELECT
                id,
                "lobbyId",
                "questionBankId",
                "problemId",
                "userSolvedCount",
                "createdAt"
            FROM
                "LobbyQuestion"
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToLobbyQuestion(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all lobbyQuestions", e);
        }
    }

    @Override
    public boolean deleteLobbyQuestionById(final String id) {
        String sql = """
            DELETE FROM "LobbyQuestion"
            WHERE id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete lobbyQuestion", e);
        }
    }
}
