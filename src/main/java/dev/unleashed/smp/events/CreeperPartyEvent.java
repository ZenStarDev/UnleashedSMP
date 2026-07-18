package dev.unleashed.smp.events;

import dev.unleashed.smp.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class CreeperPartyEvent extends AbstractEvent {
    private int taskId = -1;
    public CreeperPartyEvent() { super("creeper_party"); }
    @Override public @NotNull String getDescription() { return "Charged creepers spawn around players."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int perWave = cfgInt(ctx, "settings.creepers-per-wave", 3);
        final long interval = (long) ctx.config().events().getDouble("events.creeper_party.settings.wave-interval", 60);
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < perWave; i++) {
                    final Location loc = MathUtils.randomNearby(p, 8);
                    final Creeper c = p.getWorld().spawn(loc, Creeper.class);
                    c.setPowered(true);
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
