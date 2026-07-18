package dev.unleashed.smp.gui;

import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.lucky.LuckyManager;
import dev.unleashed.smp.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Shows lucky outcomes and a button to roll.
 */
public final class LuckyGui {

    private final JavaPlugin plugin;
    private final ConfigurationManager configManager;
    private final LocalizationManager localizationManager;
    private final LuckyManager luckyManager;

    public LuckyGui(@NotNull JavaPlugin plugin, @NotNull ConfigurationManager configManager,
                    @NotNull LocalizationManager localizationManager, @NotNull LuckyManager luckyManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.localizationManager = localizationManager;
        this.luckyManager = luckyManager;
    }

    public void open(@NotNull Player player) {
        final int size = 27;
        final Inventory inv = Bukkit.createInventory(null, size, MessageUtils.parse(configManager.gui().getString("lucky-title", "<gold>Lucky Menu</gold>")));
        int slot = 0;
        for (dev.unleashed.smp.lucky.LuckyOutcome o : luckyManager.getOutcomes()) {
            final Material m = o.getRewardItem() == null ? Material.PAPER : o.getRewardItem();
            final ItemStack item = new ItemStack(m);
            final ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text(o.getId() + " [" + o.getType() + "]"));
                if (o.getEffect() != null) meta.lore(List.of(Component.text(o.getEffect())));
                item.setItemMeta(meta);
            }
            inv.setItem(slot++, item);
        }
        final ItemStack roll = new ItemStack(Material.WHEAT);
        final ItemMeta rm = roll.getItemMeta();
        if (rm != null) { rm.displayName(Component.text("Roll!")); roll.setItemMeta(rm); }
        inv.setItem(size - 1, roll);
        player.openInventory(inv);
    }
}
