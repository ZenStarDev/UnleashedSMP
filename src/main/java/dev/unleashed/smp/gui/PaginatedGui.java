package dev.unleashed.smp.gui;

import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.localization.LocalizationManager;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for paginated inventory menus with next/previous navigation and a close button.
 */
public abstract class PaginatedGui {

    protected final JavaPlugin plugin;
    protected final ConfigurationManager configManager;
    protected final LocalizationManager localizationManager;
    protected final int rows;
    protected int page = 0;

    protected PaginatedGui(@NotNull JavaPlugin plugin, @NotNull ConfigurationManager configManager,
                           @NotNull LocalizationManager localizationManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.localizationManager = localizationManager;
        this.rows = Math.max(1, Math.min(6, configManager.gui().getInt("main-rows", 54) / 9));
    }

    protected abstract @NotNull List<ItemStack> buildItems();

    protected abstract @NotNull Component title();

    protected abstract void onClick(@NotNull Player player, int slot, @NotNull ItemStack item);

    public void open(@NotNull Player player) {
        final int size = rows * 9;
        final Inventory inv = Bukkit.createInventory(null, size, title());
        render(inv);
        player.openInventory(inv);
    }

    protected void render(@NotNull Inventory inv) {
        inv.clear();
        final List<ItemStack> items = buildItems();
        final int perPage = configManager.gui().getInt("pagination.items-per-page", 45);
        final int start = page * perPage;
        int slot = 0;
        for (int i = start; i < items.size() && slot < perPage; i++, slot++) {
            inv.setItem(slot, items.get(i));
        }
        if (page > 0) {
            inv.setItem(configManager.gui().getInt("pagination.previous-slot", 45), navItem(Material.ARROW, "previous"));
        }
        if (start + perPage < items.size()) {
            inv.setItem(configManager.gui().getInt("pagination.next-slot", 53), navItem(Material.ARROW, "next"));
        }
        inv.setItem(configManager.gui().getInt("main-menu.close-slot", 49), closeItem());
    }

    protected @NotNull ItemStack navItem(@NotNull Material material, @NotNull String direction) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            final String key = direction.equals("next") ? "gui-next" : "gui-previous";
            meta.displayName(MessageUtils.parse(localizationManager.get(key)));
            item.setItemMeta(meta);
        }
        return item;
    }

    protected @NotNull ItemStack closeItem() {
        final ItemStack item = new ItemStack(Material.BARRIER);
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(MessageUtils.parse(localizationManager.get("gui-close")));
            item.setItemMeta(meta);
        }
        return item;
    }

    protected boolean isNav(@NotNull ItemStack item) {
        return item.getType() == Material.ARROW || item.getType() == Material.BARRIER;
    }

    public void handleClick(@NotNull Player player, int slot, @NotNull ItemStack item) {
        if (item.getType() == Material.BARRIER) { player.closeInventory(); return; }
        if (item.getType() == Material.ARROW) {
            final List<ItemStack> items = buildItems();
            final int perPage = configManager.gui().getInt("pagination.items-per-page", 45);
            if (slot == configManager.gui().getInt("pagination.next-slot", 53) && (page + 1) * perPage < items.size()) {
                page++;
            } else if (slot == configManager.gui().getInt("pagination.previous-slot", 45) && page > 0) {
                page--;
            } else {
                return;
            }
            open(player);
            return;
        }
        onClick(player, slot, item);
    }
}
