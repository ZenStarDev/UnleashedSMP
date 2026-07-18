package dev.unleashed.smp.events;

import dev.unleashed.smp.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class RandomTeleportEvent extends AbstractEvent {
    private int taskId = -1;
    public RandomTeleportEvent() { super("random_teleport"); }
    @Override public @NotNull String getDescription() { return "Players are randomly teleported."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int radius = cfgInt(ctx, "settings.teleport-radius", 100);
        final long interval = (long) ctx.config().events().getDouble("events.random_teleport.settings.interval", 100);
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                final Location loc = MathUtils.randomNearby(p, radius);
                p.teleport(loc);
            }
        }, 20L, interval);
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        if (taskId != -1) {
            ctx.scheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
}
