package dev.unleashed.smp.events;

import dev.unleashed.smp.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class MeteorShowerEvent extends AbstractEvent {
    private int taskId = -1;
    public MeteorShowerEvent() { super("meteor_shower"); }
    @Override public @NotNull String getDescription() { return "Falling meteors that explode on impact."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int perTick = cfgInt(ctx, "settings.meteors-per-tick", 1);
        final float power = (float) cfgDouble(ctx, "settings.explosion-power", 3);
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < perTick; i++) {
                    final Location loc = MathUtils.randomNearby(p, 25);
                    loc.setY(120);
                    final SmallFireball fb = p.getWorld().spawn(loc, SmallFireball.class);
                    fb.setVelocity(new Vector(0, -1, 0));
                    fb.setYield(power);
                    fb.setIsIncendiary(true);
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
