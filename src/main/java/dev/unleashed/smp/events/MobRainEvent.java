package dev.unleashed.smp.events;

import dev.unleashed.smp.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Set;

public final class MobRainEvent extends AbstractEvent {
    private int taskId = -1;
    public MobRainEvent() { super("mob_rain"); }
    @Override public @NotNull String getDescription() { return "Hostile mobs rain from the sky."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int perTick = cfgInt(ctx, "settings.mobs-per-tick", 3);
        final List<String> types = ctx.config().events().getStringList("events.mob_rain.settings.mob-types");
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < perTick; i++) {
                    final EntityType type = pickType(types);
                    if (type == null) continue;
                    final Location loc = MathUtils.randomNearby(p, 30);
                    loc.setY(80);
                    p.getWorld().spawnEntity(loc, type);
                }
            }
        }, 20L, 20L);
    }
    private static EntityType pickType(List<String> types) {
        if (types.isEmpty()) return null;
        try { return EntityType.valueOf(types.get(MathUtils.randomInt(0, types.size() - 1))); }
        catch (IllegalArgumentException ex) { return null; }
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        if (taskId != -1) {
            ctx.scheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
}
