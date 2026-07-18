package dev.unleashed.smp.events;

import dev.unleashed.smp.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class TntRainEvent extends AbstractEvent {
    private int taskId = -1;
    public TntRainEvent() { super("tnt_rain"); }
    @Override public @NotNull String getDescription() { return "Rain of TNT from the sky."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int perTick = cfgInt(ctx, "settings.tnt-per-tick", 2);
        final int radius = cfgInt(ctx, "settings.world-spawn-radius", 30);
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < perTick; i++) {
                    final Location loc = MathUtils.randomNearby(p, radius);
                    if (loc.getWorld() != null) loc.setY(loc.getWorld().getMaxHeight() - 5);
                    p.getWorld().spawnEntity(loc, EntityType.TNT);
                }
            }
        }, 20L, 20L);
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        if (taskId != -1) {
            ctx.scheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
}
