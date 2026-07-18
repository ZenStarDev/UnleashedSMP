package dev.unleashed.smp.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class XpStormEvent extends AbstractEvent {
    private int taskId = -1;
    public XpStormEvent() { super("xp_storm"); }
    @Override public @NotNull String getDescription() { return "Players gain XP rapidly."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int perTick = cfgInt(ctx, "settings.xp-per-tick", 5);
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) p.giveExp(perTick);
        }, 20L, 20L);
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        if (taskId != -1) {
            ctx.scheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
}
