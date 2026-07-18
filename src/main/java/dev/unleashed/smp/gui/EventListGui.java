package dev.unleashed.smp.gui;

import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.events.EventManager;
import dev.unleashed.smp.events.GameEvent;
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
 * Lists all registered events; clicking one starts it.
 */
public final class EventListGui {

    private final JavaPlugin plugin;
    private final ConfigurationManager configManager;
    private final LocalizationManager localizationManager;
    private final EventManager eventManager;

    public EventListGui(@NotNull JavaPlugin plugin, @NotNull ConfigurationManager configManager,
                        @NotNull LocalizationManager localizationManager, @NotNull EventManager eventManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.localizationManager = localizationManager;
        this.eventManager = eventManager;
    }

    public void open(@NotNull Player player) {
        EventListActions.clear();
        final List<GameEvent> events = new ArrayList<>(eventManager.getEvents());
        final int size = Math.max(9, ((events.size() / 9) + 2) * 9);
        final Inventory inv = Bukkit.createInventory(null, size, MessageUtils.parse(configManager.gui().getString("events-title", "<gold>Event Menu</gold>")));
        int slot = 0;
        for (GameEvent e : events) {
            final ItemStack item = new ItemStack(Material.PAPER);
            final ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text(e.getId()));
                meta.lore(List.of(Component.text(e.getDescription())));
                item.setItemMeta(meta);
            }
            inv.setItem(slot++, item);
            EventListActions.register(slot, p -> eventManager.start(e.getId()));
        }
        player.openInventory(inv);
    }
}
