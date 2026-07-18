package dev.unleashed.smp.database;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

/**
 * Data Access Object for the event log table.
 */
public final class EventLogDao {

    private final DatabaseManager db;
    private final String table;

    EventLogDao(@NotNull DatabaseManager db, @NotNull String prefix) {
        this.db = db;
        this.table = prefix + "event_log";
    }

    public @NotNull CompletableFuture<Void> logStart(@NotNull String event, long startedAt, @NotNull String world) {
        return db.query(conn -> {
            try {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO " + table + " (event, started_at, ended_at, world) VALUES (?,?,0,?)")) {
                ps.setString(1, event);
                ps.setLong(2, startedAt);
                ps.setString(3, world);
                ps.executeUpdate();
            }
            return null;
            } catch (java.sql.SQLException ex) {
                throw new RuntimeException(ex);
            }
        }).thenApply(v -> null);
    }

    public @NotNull CompletableFuture<Void> logEnd(@NotNull String event, long endedAt) {
        return db.query(conn -> {
            try {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE " + table + " SET ended_at = ? WHERE event = ? AND ended_at = 0 ORDER BY id DESC LIMIT 1")) {
                ps.setLong(1, endedAt);
                ps.setString(2, event);
                ps.executeUpdate();
            }
            return null;
            } catch (java.sql.SQLException ex) {
                throw new RuntimeException(ex);
            }
        }).thenApply(v -> null);
    }
}
