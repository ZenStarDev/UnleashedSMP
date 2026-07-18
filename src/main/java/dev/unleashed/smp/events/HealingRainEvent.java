package dev.unleashed.smp.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class HealingRainEvent extends AbstractEvent {
    private int taskId = -1;
    public HealingRainEvent() { super("healing_rain"); }
    @Override public @NotNull String getDescription() { return "Players regenerate health."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int heal = ctx.config().events().getInt("events.healing_rain.settings.heal-per-tick", 1);
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getHealth() < p.getMaxHealth()) p.setHealth(Math.min(p.getMaxHealth(), p.getHealth() + heal));
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
