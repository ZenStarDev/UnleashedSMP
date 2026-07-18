package dev.unleashed.smp.gui;

import dev.unleashed.smp.UnleashedPlugin;
import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.events.EventManager;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.logging.PluginLogger;
import dev.unleashed.smp.lucky.LuckyManager;
import dev.unleashed.smp.mutant.MutantManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Central GUI registry. Builds the main menu and sub-menus (events, lucky, mutants, admin) and
 * dispatches inventory click handling through a single listener.
 */
public final class GuiManager {

    private final JavaPlugin plugin;
    private final PluginLogger logger;
    private final ConfigurationManager configManager;
    private final LocalizationManager localizationManager;
    private final EventManager eventManager;
    private final LuckyManager luckyManager;
    private final MutantManager mutantManager;
    private final GuiListener listener;

    public GuiManager(@NotNull JavaPlugin plugin, @NotNull PluginLogger logger,
                      @NotNull ConfigurationManager configManager,
                      @NotNull LocalizationManager localizationManager,
                      @NotNull EventManager eventManager,
                      @NotNull LuckyManager luckyManager,
                      @NotNull MutantManager mutantManager) {
        this.plugin = plugin;
        this.logger = logger;
        this.configManager = configManager;
        this.localizationManager = localizationManager;
        this.eventManager = eventManager;
        this.luckyManager = luckyManager;
        this.mutantManager = mutantManager;
        this.listener = new GuiListener(this, eventManager, luckyManager);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void openMain(@NotNull Player player) {
        new MainMenuGui(plugin, configManager, localizationManager, eventManager, luckyManager, mutantManager)
                .open(player);
    }

    public void openEvents(@NotNull Player player) {
        new EventListGui(plugin, configManager, localizationManager, eventManager).open(player);
    }

    public void openLucky(@NotNull Player player) {
        new LuckyGui(plugin, configManager, localizationManager, luckyManager).open(player);
    }

    public void openMutants(@NotNull Player player) {
        new MutantGui(plugin, configManager, localizationManager, mutantManager).open(player);
    }

    public GuiListener getListener() { return listener; }

    public void reload() { }
    public void shutdown() {
        org.bukkit.event.HandlerList.unregisterAll(listener);
    }
}
