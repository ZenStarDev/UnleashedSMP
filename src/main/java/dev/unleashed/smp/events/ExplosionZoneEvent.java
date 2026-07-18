package dev.unleashed.smp.events;

import dev.unleashed.smp.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class ExplosionZoneEvent extends AbstractEvent {
    private int taskId = -1;
    public ExplosionZoneEvent() { super("explosion_zone"); }
    @Override public @NotNull String getDescription() { return "Random explosions around players."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final long interval = (long) ctx.config().events().getDouble("events.explosion_zone.settings.interval", 50);
        final float power = (float) ctx.config().events().getDouble("events.explosion_zone.settings.power", 2);
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                final Location loc = MathUtils.randomNearby(p, 6);
                p.getWorld().createExplosion(loc, power, false, true);
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
