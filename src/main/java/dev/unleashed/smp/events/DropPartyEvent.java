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

public final class DropPartyEvent extends AbstractEvent {
    private int taskId = -1;
    public DropPartyEvent() { super("drop_party"); }
    @Override public @NotNull String getDescription() { return "Items drop from the sky."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int perTick = ctx.config().events().getInt("events.drop_party.settings.items-per-tick", 4);
        final Material[] items = Material.values();
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < perTick; i++) {
                    final Location loc = MathUtils.randomNearby(p, 15);
                    loc.setY(70);
                    final Material m = items[MathUtils.randomInt(0, items.length - 1)];
                    if (m.isItem()) {
                        final Item item = p.getWorld().dropItemNaturally(loc, new ItemStack(m, 1));
                        item.setPickupDelay(20);
                    }
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
