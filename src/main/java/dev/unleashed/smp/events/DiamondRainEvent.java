package dev.unleashed.smp.events;

import dev.unleashed.smp.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class DiamondRainEvent extends AbstractEvent {
    private int taskId = -1;
    public DiamondRainEvent() { super("diamond_rain"); }
    @Override public @NotNull String getDescription() { return "Diamonds rain from the sky."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int perTick = cfgInt(ctx, "settings.diamonds-per-tick", 3);
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < perTick; i++) {
                    final Location loc = MathUtils.randomNearby(p, 20);
                    loc.setY(70);
                    final Item item = p.getWorld().dropItemNaturally(loc, new ItemStack(Material.DIAMOND));
                    item.setPickupDelay(20);
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
