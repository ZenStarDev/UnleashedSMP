package dev.unleashed.smp.gui;

import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.events.EventManager;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.lucky.LuckyManager;
import dev.unleashed.smp.mutant.MutantManager;
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
 * The root navigation menu exposing Events, Lucky, Mutants, Stats, and Admin entries.
 */
public final class MainMenuGui {

    private final JavaPlugin plugin;
    private final ConfigurationManager configManager;
    private final LocalizationManager localizationManager;
    private final EventManager eventManager;
    private final LuckyManager luckyManager;
    private final MutantManager mutantManager;

    public MainMenuGui(@NotNull JavaPlugin plugin, @NotNull ConfigurationManager configManager,
                       @NotNull LocalizationManager localizationManager,
                       @NotNull EventManager eventManager, @NotNull LuckyManager luckyManager,
                       @NotNull MutantManager mutantManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.localizationManager = localizationManager;
        this.eventManager = eventManager;
        this.luckyManager = luckyManager;
        this.mutantManager = mutantManager;
    }

    public void open(@NotNull Player player) {
        MainMenuActions.clear();
        final int size = Math.max(9, Math.min(54, configManager.gui().getInt("main-rows", 54)));
        final Inventory inv = Bukkit.createInventory(null, size, MessageUtils.parse(configManager.gui().getString("main-title", "<gold>UnleashedSMP</gold>")));
        put(inv, configManager.gui().getInt("main-menu.events-slot", 19), Material.ENDER_PEARL,
                localizationManager.get("gui-events-title"), player1 -> new EventListGui(plugin, configManager, localizationManager, eventManager).open(player1));
        put(inv, configManager.gui().getInt("main-menu.lucky-slot", 21), Material.WHEAT,
                localizationManager.get("gui-lucky-title"), player1 -> new LuckyGui(plugin, configManager, localizationManager, luckyManager).open(player1));
        put(inv, configManager.gui().getInt("main-menu.mutant-slot", 23), Material.ZOMBIE_HEAD,
                localizationManager.get("gui-mutant-title", "%mutant%", "Mutants"), player1 -> new MutantGui(plugin, configManager, localizationManager, mutantManager).open(player1));
        player.openInventory(inv);
    }

    private void put(@NotNull Inventory inv, int slot, @NotNull Material material, @NotNull Component name, @NotNull java.util.function.Consumer<Player> action) {
        if (slot < 0 || slot >= inv.getSize()) return;
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) { meta.displayName(name); item.setItemMeta(meta); }
        inv.setItem(slot, item);
        MainMenuActions.register(slot, action);
    }
}
