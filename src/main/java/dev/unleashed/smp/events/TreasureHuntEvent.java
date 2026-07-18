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

public final class TreasureHuntEvent extends AbstractEvent {
    public TreasureHuntEvent() { super("treasure_hunt"); }
    @Override public @NotNull String getDescription() { return "Hidden treasures spawn."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int count = ctx.config().events().getInt("events.treasure_hunt.settings.treasure-count", 10);
        final Material[] loot = {Material.DIAMOND, Material.GOLD_INGOT, Material.EMERALD, Material.NETHERITE_INGOT};
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < count; i++) {
                final Location loc = MathUtils.randomNearby(p, 50);
                final Material m = loot[MathUtils.randomInt(0, loot.length - 1)];
                final Item item = p.getWorld().dropItem(loc, new ItemStack(m, 1));
                final net.kyori.adventure.text.Component name = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<gold>Treasure</gold>");
                item.setCustomName(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(name));
                item.setCustomNameVisible(true);
                item.setUnlimitedLifetime(true);
            }
        }
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) { }
}
