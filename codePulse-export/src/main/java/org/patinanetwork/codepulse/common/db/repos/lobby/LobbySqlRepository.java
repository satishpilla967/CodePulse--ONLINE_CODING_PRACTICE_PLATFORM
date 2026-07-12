package org.patinanetwork.codepulse.common.db.repos.lobby;

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
import org.patinanetwork.codepulse.common.db.models.lobby.Lobby;
import org.patinanetwork.codepulse.common.db.models.lobby.LobbyStatus;
import org.patinanetwork.codepulse.common.redis.RedisClient;
import org.patinanetwork.codepulse.common.time.StandardizedOffsetDateTime;
import org.patinanetwork.codepulse.scheduled.pg.PgChannel;
import org.springframework.stereotype.Component;

@Component
public class LobbySqlRepository implements LobbyRepository {

    private final DataSource ds;
    private final RedisClient redisClient;

    public LobbySqlRepository(final DataSource ds, final RedisClient redisClient) {
        this.ds = ds;
        this.redisClient = redisClient;
    }

    private Lobby parseResultSetToLobby(final ResultSet resultSet) throws SQLException {
        return Lobby.builder()
                .id(resultSet.getString("id"))
                .joinCode(resultSet.getString("joinCode"))
                .status(LobbyStatus.valueOf(resultSet.getString("status")))
                .createdAt(resultSet.getObject("createdAt", OffsetDateTime.class))
                .expiresAt(Optional.ofNullable(resultSet.getObject("expiresAt", OffsetDateTime.class)))
                .playerCount(resultSet.getInt("playerCount"))
                .winnerId(Optional.ofNullable(resultSet.getString("winnerId")))
                .tie(resultSet.getBoolean("tie"))
                .build();
    }

    @Override
    public void createLobby(final Lobby lobby) {
        String sql = """
            INSERT INTO "Lobby"
                (id, "joinCode", status, "createdAt", "expiresAt", "playerCount", "winnerId", "tie")
            VALUES
                (:id, :joinCode, :status, :createdAt, :expiresAt, :playerCount, :winnerId, :tie)
            """;

        lobby.setId(UUID.randomUUID().toString());
        lobby.setCreatedAt(OffsetDateTime.now());

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", lobby.getId());
            stmt.setString("joinCode", lobby.getJoinCode());
            stmt.setString("status", lobby.getStatus().name());
            stmt.setObject("createdAt", lobby.getCreatedAt());
            stmt.setObject(
                    "expiresAt",
                    lobby.getExpiresAt()
                            .map(StandardizedOffsetDateTime::normalize)
                            .orElse(null));
            stmt.setInt("playerCount", lobby.getPlayerCount());
            stmt.setString("winnerId", lobby.getWinnerId().orElse(null));
            stmt.setBoolean("tie", lobby.isTie());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create lobby", e);
        }

        // Redis pub/sub replaces the Postgres "lobbyUpsertNotify" trigger (AFTER INSERT).
        redisClient.publish(PgChannel.UPSERT_LOBBY.getChannelName(), lobby.getId());
    }

    @Override
    public Optional<Lobby> findLobbyById(final String id) {
        String sql = """
            SELECT
                id,
                "joinCode",
                status,
                "createdAt",
                "expiresAt",
                "playerCount",
                "winnerId",
                "tie"
            FROM
                "Lobby"
            WHERE
                id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobby(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobby by id", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Lobby> findAvailableLobbyByJoinCode(final String joinCode) {
        String sql = """
            SELECT
                id,
                "joinCode",
                status,
                "createdAt",
                "expiresAt",
                "playerCount",
                "winnerId",
                "tie"
            FROM
                "Lobby"
            WHERE
                "joinCode" = :joinCode
                AND status = :status
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("joinCode", joinCode);
            stmt.setString("status", LobbyStatus.AVAILABLE.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobby(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobby by join code and status", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Lobby> findActiveLobbyByJoinCode(final String joinCode) {
        String sql = """
            SELECT
                id,
                "joinCode",
                status,
                "createdAt",
                "expiresAt",
                "playerCount",
                "winnerId",
                "tie"
            FROM
                "Lobby"
            WHERE
                "joinCode" = :joinCode
                AND status = 'ACTIVE'
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("joinCode", joinCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobby(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobby by join code and status", e);
        }

        return Optional.empty();
    }

    @Override
    public List<Lobby> findLobbiesByStatus(final LobbyStatus status) {
        List<Lobby> result = new ArrayList<>();
        String sql = """
            SELECT
                id,
                "joinCode",
                status,
                "createdAt",
                "expiresAt",
                "playerCount",
                "winnerId",
                "tie"
            FROM
                "Lobby"
            WHERE
                status = :status
            ORDER BY
                "createdAt" DESC
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("status", status.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToLobby(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find lobbies by status", e);
        }
    }

    @Override
    public List<Lobby> findAvailableLobbies() {
        List<Lobby> result = new ArrayList<>();
        String sql = """
            SELECT
                id,
                "joinCode",
                status,
                "createdAt",
                "expiresAt",
                "playerCount",
                "winnerId",
                "tie"
            FROM
                "Lobby"
            WHERE
                status = :status
                AND "expiresAt" > NOW()
            ORDER BY
                "createdAt" DESC
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("status", LobbyStatus.AVAILABLE.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToLobby(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find available lobbies", e);
        }
    }

    @Override
    public List<Lobby> findActiveLobbies() {
        List<Lobby> result = new ArrayList<>();
        String sql = """
            SELECT
                id,
                "joinCode",
                status,
                "createdAt",
                "expiresAt",
                "playerCount",
                "winnerId",
                "tie"
            FROM
                "Lobby"
            WHERE
                status = :status
                AND "expiresAt" > NOW()
            ORDER BY
                "createdAt" DESC
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("status", LobbyStatus.ACTIVE.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToLobby(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find active lobbies", e);
        }
    }

    @Override
    public List<Lobby> findExpiredLobbies() {
        List<Lobby> result = new ArrayList<>();
        String sql = """
            SELECT
                id,
                "joinCode",
                status,
                "createdAt",
                "expiresAt",
                "playerCount",
                "winnerId",
                "tie"
            FROM
                "Lobby"
            WHERE
                status = :status
                AND "expiresAt" <= NOW()
            ORDER BY
                "createdAt" DESC
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("status", LobbyStatus.ACTIVE.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToLobby(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find expired lobbies", e);
        }
    }

    @Override
    public Optional<Lobby> findActiveLobbyByLobbyPlayerPlayerId(final String lobbyPlayerId) {
        String sql = """
            SELECT
                l.id,
                l."joinCode",
                l.status,
                l."createdAt",
                l."expiresAt",
                l."playerCount",
                l."winnerId",
                l."tie"
            FROM
                "Lobby" l
            INNER JOIN
                "LobbyPlayer" lp ON l.id = lp."lobbyId"
            WHERE
                l.status = :status
                AND lp."playerId" = :playerId
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("status", LobbyStatus.ACTIVE.name());
            stmt.setString("playerId", lobbyPlayerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobby(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find active lobby by lobby player id", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Lobby> findAvailableLobbyByLobbyPlayerPlayerId(final String lobbyPlayerId) {
        String sql = """
            SELECT
                l.id,
                l."joinCode",
                l.status,
                l."createdAt",
                l."expiresAt",
                l."playerCount",
                l."winnerId",
                l."tie"
            FROM
                "Lobby" l
            JOIN
                "LobbyPlayer" lp ON l.id = lp."lobbyId"
            WHERE
                l.status = 'AVAILABLE'
            AND
                lp."playerId" = :playerId
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("playerId", lobbyPlayerId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToLobby(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find available lobby by lobby player id", e);
        }

        return Optional.empty();
    }

    @Override
    public boolean updateLobby(final Lobby lobby) {
        String sql = """
            UPDATE "Lobby"
            SET
                status = :status,
                "playerCount" = :playerCount,
                "winnerId" = :winnerId,
                "tie" = :tie,
                "expiresAt" = :expiresAt
            WHERE
                id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("status", lobby.getStatus().name());
            stmt.setInt("playerCount", lobby.getPlayerCount());
            stmt.setString("id", lobby.getId());
            stmt.setString("winnerId", lobby.getWinnerId().orElse(null));
            stmt.setBoolean("tie", lobby.isTie());
            stmt.setObject(
                    "expiresAt",
                    lobby.getExpiresAt().isPresent()
                            ? StandardizedOffsetDateTime.normalize(
                                    lobby.getExpiresAt().get())
                            : null);

            int rowsAffected = stmt.executeUpdate();
            boolean updated = rowsAffected == 1;
            if (updated) {
                // Redis pub/sub replaces the Postgres "lobbyUpsertNotify" trigger (AFTER UPDATE).
                redisClient.publish(PgChannel.UPSERT_LOBBY.getChannelName(), lobby.getId());
            }
            return updated;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update lobby", e);
        }
    }

    @Override
    public boolean deleteLobbyById(final String id) {
        String sql = """
            DELETE FROM "Lobby"
            WHERE id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("id", id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete lobby", e);
        }
    }
}
