package dev.unleashed.smp.database;

import dev.unleashed.smp.models.PlayerData;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Data Access Object for {@link PlayerData}. All methods are asynchronous and return futures.
 */
public final class PlayerDataDao {

    private final DatabaseManager db;
    private final String table;

    PlayerDataDao(@NotNull DatabaseManager db, @NotNull String prefix) {
        this.db = db;
        this.table = prefix + "player_data";
    }

    public @NotNull CompletableFuture<PlayerData> loadOrCreate(@NotNull UUID uuid, @NotNull String username) {
        return db.query(conn -> {
            try {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT username, lucky_uses, last_lucky, mutants_killed, created_at FROM " + table + " WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        final PlayerData data = new PlayerData(uuid, rs.getString("username"));
                        data.setLuckyUses(rs.getInt("lucky_uses"));
                        data.setLastLucky(rs.getLong("last_lucky"));
                        data.setMutantsKilled(rs.getInt("mutants_killed"));
                        data.setCreatedAt(rs.getLong("created_at"));
                        if (!rs.getString("username").equals(username)) {
                            data.setUsername(username);
                        }
                        return data;
                    }
                }
            }
            final PlayerData fresh = new PlayerData(uuid, username);
            save(fresh);
            return fresh;
            } catch (java.sql.SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public @NotNull CompletableFuture<Void> save(@NotNull PlayerData data) {
        return db.query(conn -> {
            try {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO " + table + " (uuid, username, lucky_uses, last_lucky, mutants_killed, created_at) "
                            + "VALUES (?,?,?,?,?,?) ON DUPLICATE KEY UPDATE username=?, lucky_uses=?, last_lucky=?, mutants_killed=?")) {
                ps.setString(1, data.getUuid().toString());
                ps.setString(2, data.getUsername());
                ps.setInt(3, data.getLuckyUses());
                ps.setLong(4, data.getLastLucky());
                ps.setInt(5, data.getMutantsKilled());
                ps.setLong(6, data.getCreatedAt());
                ps.setString(7, data.getUsername());
                ps.setInt(8, data.getLuckyUses());
                ps.setLong(9, data.getLastLucky());
                ps.setInt(10, data.getMutantsKilled());
                ps.executeUpdate();
            }
            return null;
            } catch (java.sql.SQLException ex) {
                throw new RuntimeException(ex);
            }
        }).thenApply(v -> null);
    }
}
