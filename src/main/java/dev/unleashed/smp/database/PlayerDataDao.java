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
                    "SELECT username, lucky_uses, last_lucky, mutants_killed, luck_level, created_at FROM " + table + " WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        final PlayerData data = new PlayerData(uuid, rs.getString("username"));
                        data.setLuckyUses(rs.getInt("lucky_uses"));
                        data.setLastLucky(rs.getLong("last_lucky"));
                        data.setMutantsKilled(rs.getInt("mutants_killed"));
                        data.setLuckLevel(rs.getInt("luck_level"));
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
                    "INSERT INTO " + table + " (uuid, username, lucky_uses, last_lucky, mutants_killed, luck_level, created_at) "
                            + "VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE username=?, lucky_uses=?, last_lucky=?, mutants_killed=?, luck_level=?")) {
                ps.setString(1, data.getUuid().toString());
                ps.setString(2, data.getUsername());
                ps.setInt(3, data.getLuckyUses());
                ps.setLong(4, data.getLastLucky());
                ps.setInt(5, data.getMutantsKilled());
                ps.setInt(6, data.getLuckLevel());
                ps.setLong(7, data.getCreatedAt());
                ps.setString(8, data.getUsername());
                ps.setInt(9, data.getLuckyUses());
                ps.setLong(10, data.getLastLucky());
                ps.setInt(11, data.getMutantsKilled());
                ps.setInt(12, data.getLuckLevel());
                ps.executeUpdate();
            }
            return null;
            } catch (java.sql.SQLException ex) {
                throw new RuntimeException(ex);
            }
        }).thenApply(v -> null);
    }
}
