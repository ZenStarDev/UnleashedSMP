package dev.unleashed.smp.gui;

import dev.unleashed.smp.events.EventManager;
import dev.unleashed.smp.lucky.LuckyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

/**
 * Routes inventory clicks to the appropriate GUI handler based on the clicked inventory title.
 */
public final class GuiListener implements Listener {

    private final GuiManager manager;
    private final EventManager eventManager;
    private final LuckyManager luckyManager;

    public GuiListener(@NotNull GuiManager manager, @NotNull EventManager eventManager,
                       @NotNull LuckyManager luckyManager) {
        this.manager = manager;
        this.eventManager = eventManager;
        this.luckyManager = luckyManager;
    }

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getCurrentItem() == null || e.getCurrentItem().getType().isAir()) return;
        final ItemStack item = e.getCurrentItem();
        final String title = e.getView().title() == null ? "" : e.getView().title().toString();
        e.setCancelled(true);
        if (title.contains("UnleashedSMP") || title.contains("Unleashed SMP")) {
            MainMenuActions.run(e.getSlot(), player);
        } else if (title.contains("Event Menu")) {
            EventListActions.run(e.getSlot(), player);
        } else if (title.contains("Lucky")) {
            if (item.getType() == Material.WHEAT) luckyManager.roll(player);
        }
    }
}
