package dev.unleashed.smp.events;

import dev.unleashed.smp.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class ChickenRainEvent extends AbstractEvent {
    private int taskId = -1;
    public ChickenRainEvent() { super("chicken_rain"); }
    @Override public @NotNull String getDescription() { return "Chickens rain from the sky."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int perTick = cfgInt(ctx, "settings.chickens-per-tick", 4);
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < perTick; i++) {
                    final Location loc = MathUtils.randomNearby(p, 20);
                    loc.setY(70);
                    p.getWorld().spawnEntity(loc, EntityType.CHICKEN);
                }
            }
        }, 15L, 15L);
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        if (taskId != -1) {
            ctx.scheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
}
