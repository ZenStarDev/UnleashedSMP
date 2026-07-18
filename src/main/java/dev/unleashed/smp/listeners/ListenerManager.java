package dev.unleashed.smp.listeners;

import dev.unleashed.smp.events.EventManager;
import dev.unleashed.smp.lucky.LuckyManager;
import dev.unleashed.smp.mutant.MutantManager;
import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.logging.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers and unregisters all plugin listeners.
 */
public final class ListenerManager {

    private final JavaPlugin plugin;
    private final PluginLogger logger;
    private final EventManager eventManager;
    private final LuckyManager luckyManager;
    private final MutantManager mutantManager;
    private final ConfigurationManager configManager;
    private final List<Listener> registered = new java.util.ArrayList<>();

    public ListenerManager(@NotNull JavaPlugin plugin, @NotNull PluginLogger logger,
                           @NotNull EventManager eventManager, @NotNull LuckyManager luckyManager,
                           @NotNull MutantManager mutantManager, @NotNull ConfigurationManager configManager) {
        this.plugin = plugin;
        this.logger = logger;
        this.eventManager = eventManager;
        this.luckyManager = luckyManager;
        this.mutantManager = mutantManager;
        this.configManager = configManager;
    }

    public void register() {
        // MutantManager and LuckyDropEvent/DoubleDropsEvent register themselves; add a session join listener.
        final PlayerJoinListener join = new PlayerJoinListener(eventManager, luckyManager);
        Bukkit.getPluginManager().registerEvents(join, plugin);
        registered.add(join);
        logger.info("Registered %d listeners", registered.size());
    }

    public void unregister() {
        for (Listener l : registered) {
            org.bukkit.event.HandlerList.unregisterAll(l);
        }
        registered.clear();
    }
}
