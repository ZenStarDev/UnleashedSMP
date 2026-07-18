package dev.unleashed.smp.events;

import dev.unleashed.smp.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class SkeletonArmyEvent extends AbstractEvent {
    private int taskId = -1;
    public SkeletonArmyEvent() { super("skeleton_army"); }
    @Override public @NotNull String getDescription() { return "Swarms of skeletons."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int perWave = cfgInt(ctx, "settings.skeletons-per-wave", 8);
        final long interval = (long) ctx.config().events().getDouble("events.skeleton_army.settings.wave-interval", 80);
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < perWave; i++) {
                    final Location loc = MathUtils.randomNearby(p, 10);
                    p.getWorld().spawnEntity(loc, EntityType.SKELETON);
                }
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
