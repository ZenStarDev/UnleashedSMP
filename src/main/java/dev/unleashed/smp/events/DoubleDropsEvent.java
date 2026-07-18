package dev.unleashed.smp.events;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class DoubleDropsEvent extends AbstractEvent implements Listener {
    private EventContext ctx;
    public DoubleDropsEvent() { super("double_drops"); }
    @Override public @NotNull String getDescription() { return "Double block/item drops."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        this.ctx = ctx;
        Bukkit.getPluginManager().registerEvents(this, ctx.plugin());
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        final Block b = e.getBlock();
        final double multiplier = cfgDouble(ctx, "settings.multiplier", 2.0);
        for (org.bukkit.inventory.ItemStack drop : b.getDrops()) {
            final org.bukkit.inventory.ItemStack d = drop.clone();
            d.setAmount((int) (d.getAmount() * multiplier));
            b.getWorld().dropItemNaturally(b.getLocation(), d);
        }
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        HandlerList.unregisterAll(this);
    }
}
