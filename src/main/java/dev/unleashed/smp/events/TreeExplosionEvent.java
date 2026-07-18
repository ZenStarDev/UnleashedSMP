package dev.unleashed.smp.events;

import dev.unleashed.smp.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class TreeExplosionEvent extends AbstractEvent {
    private int taskId = -1;
    public TreeExplosionEvent() { super("tree_explosion"); }
    @Override public @NotNull String getDescription() { return "Trees explode into wood."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int radius = ctx.config().events().getInt("events.tree_explosion.settings.radius", 5);
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                final Block b = p.getLocation().add(MathUtils.randomInt(-radius, radius), 0, MathUtils.randomInt(-radius, radius)).getBlock();
                if (b.getType() == Material.OAK_LOG || b.getType().name().endsWith("_LOG")) {
                    b.getWorld().dropItemNaturally(b.getLocation(), new org.bukkit.inventory.ItemStack(Material.OAK_LOG, 3));
                    b.setType(Material.AIR);
                }
            }
        }, 20L, 40L);
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        if (taskId != -1) {
            ctx.scheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
}
