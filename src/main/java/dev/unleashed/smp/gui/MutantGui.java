package dev.unleashed.smp.gui;

import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.mutant.MutantDefinition;
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

import java.util.List;

/**
 * Displays mutant definitions and their stats.
 */
public final class MutantGui {

    private final JavaPlugin plugin;
    private final ConfigurationManager configManager;
    private final LocalizationManager localizationManager;
    private final MutantManager mutantManager;

    public MutantGui(@NotNull JavaPlugin plugin, @NotNull ConfigurationManager configManager,
                     @NotNull LocalizationManager localizationManager, @NotNull MutantManager mutantManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.localizationManager = localizationManager;
        this.mutantManager = mutantManager;
    }

    public void open(@NotNull Player player) {
        final List<MutantDefinition> defs = mutantManager.getDefinitions();
        final int size = Math.max(9, ((defs.size() / 9) + 2) * 9);
        final Inventory inv = Bukkit.createInventory(null, size, MessageUtils.parse("<red>Mutants</red>"));
        int slot = 0;
        for (MutantDefinition d : defs) {
            final ItemStack item = new ItemStack(Material.ZOMBIE_HEAD);
            final ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text(d.getId()));
                meta.lore(List.of(
                        Component.text("HP x" + d.getHpMultiplier()),
                        Component.text("DMG x" + d.getDamageMultiplier()),
                        Component.text("Speed x" + d.getSpeedMultiplier())));
                item.setItemMeta(meta);
            }
            inv.setItem(slot++, item);
        }
        player.openInventory(inv);
    }
}
