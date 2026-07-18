package dev.unleashed.smp.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class LuckyDropEvent extends AbstractEvent implements Listener {
    private EventContext ctx;
    public LuckyDropEvent() { super("lucky_drop"); }
    @Override public @NotNull String getDescription() { return "Increased chance of rare drops."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        this.ctx = ctx;
        Bukkit.getPluginManager().registerEvents(this, ctx.plugin());
    }
    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        final double multiplier = cfgDouble(ctx, "settings.multiplier", 3.0);
        if (Math.random() < 0.3) e.getDrops().forEach(d -> d.setAmount((int) (d.getAmount() * multiplier)));
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        HandlerList.unregisterAll(this);
    }
}
