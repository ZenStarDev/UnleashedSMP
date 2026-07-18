package dev.unleashed.smp.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class RunningEvent {
    private final String id;
    private final GameEvent event;
    private final Set<UUID> playerIds = new HashSet<>();
    private volatile int remainingTicks;
    private volatile int taskId = -1;
    private final long startedAt = System.currentTimeMillis();

    public RunningEvent(@NotNull GameEvent event, int durationTicks) {
        this.id = event.getId(); this.event = event; this.remainingTicks = durationTicks;
    }
    public @NotNull String getId() { return id; }
    public @NotNull GameEvent getEvent() { return event; }
    public int getRemainingTicks() { return remainingTicks; }
    public void setRemainingTicks(int ticks) { this.remainingTicks = ticks; }
    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    public long getStartedAt() { return startedAt; }
    public void addPlayer(@NotNull Player player) { playerIds.add(player.getUniqueId()); }
    public void removePlayer(@NotNull Player player) { playerIds.remove(player.getUniqueId()); }
    public boolean hasPlayer(@NotNull Player player) { return playerIds.contains(player.getUniqueId()); }
    public @NotNull Set<UUID> getPlayerIds() { return new HashSet<>(playerIds); }
    public @NotNull Set<Player> resolvePlayers() {
        final Set<Player> players = new HashSet<>();
        for (UUID pid : playerIds) { final Player p = Bukkit.getPlayer(pid); if (p != null) players.add(p); }
        return players;
    }
}
