package dev.unleashed.smp.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Persisted per-player statistics and state.
 */
public final class PlayerData {

    private final UUID uuid;
    private String username;
    private int luckyUses;
    private long lastLucky;
    private int mutantsKilled;
    private long createdAt;

    public PlayerData(@NotNull UUID uuid, @NotNull String username) {
        this.uuid = uuid;
        this.username = username;
        this.createdAt = System.currentTimeMillis();
    }

    public @NotNull UUID getUuid() { return uuid; }
    public @NotNull String getUsername() { return username; }
    public void setUsername(@NotNull String username) { this.username = username; }
    public int getLuckyUses() { return luckyUses; }
    public void setLuckyUses(int luckyUses) { this.luckyUses = luckyUses; }
    public long getLastLucky() { return lastLucky; }
    public void setLastLucky(long lastLucky) { this.lastLucky = lastLucky; }
    public int getMutantsKilled() { return mutantsKilled; }
    public void setMutantsKilled(int mutantsKilled) { this.mutantsKilled = mutantsKilled; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
