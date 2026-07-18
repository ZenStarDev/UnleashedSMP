package dev.unleashed.smp.events;

import dev.unleashed.smp.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class EarthquakeEvent extends AbstractEvent {
    private int taskId = -1;
    public EarthquakeEvent() { super("earthquake"); }
    @Override public @NotNull String getDescription() { return "Ground shakes, players knocked."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final long interval = (long) ctx.config().events().getDouble("events.earthquake.settings.interval", 40);
        final double power = ctx.config().events().getDouble("events.earthquake.settings.power", 1.5);
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.setVelocity(new Vector(MathUtils.randomDouble(-power, power), 0.3, MathUtils.randomDouble(-power, power)));
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
