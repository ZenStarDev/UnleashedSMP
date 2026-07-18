package dev.unleashed.smp.events;

import dev.unleashed.smp.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class LightningStormEvent extends AbstractEvent {
    private int taskId = -1;
    public LightningStormEvent() { super("lightning_storm"); }
    @Override public @NotNull String getDescription() { return "Lightning strikes randomly."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final long interval = (long) ctx.config().events().getDouble("events.lightning_storm.settings.interval", 30);
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                final Location loc = MathUtils.randomNearby(p, 12);
                p.getWorld().strikeLightning(loc);
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
