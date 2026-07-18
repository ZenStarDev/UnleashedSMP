package dev.unleashed.smp.bootstrap;

import dev.unleashed.smp.UnleashedPlugin;
import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.database.DatabaseManager;
import dev.unleashed.smp.events.EventManager;
import dev.unleashed.smp.gui.GuiManager;
import dev.unleashed.smp.integrations.IntegrationManager;
import dev.unleashed.smp.listeners.ListenerManager;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.logging.PluginLogger;
import dev.unleashed.smp.lucky.LuckyManager;
import dev.unleashed.smp.managers.CacheManager;
import dev.unleashed.smp.commands.CommandManager;
import dev.unleashed.smp.managers.MetricsManager;
import dev.unleashed.smp.managers.PermissionManager;
import dev.unleashed.smp.managers.ReloadManager;
import dev.unleashed.smp.managers.SchedulerManager;
import dev.unleashed.smp.mutant.MutantManager;
import dev.unleashed.smp.placeholders.UnleashedPlaceholderExpansion;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Lightweight service locator and lifecycle orchestrator.
 *
 * <p>Every long-lived service in the plugin is created and wired here. This keeps construction
 * centralized and makes the system dependency-injection friendly without pulling in a heavy
 * framework.</p>
 */
public final class Bootstrap {

    private final UnleashedPlugin plugin;
    private final PluginLogger logger;

    private ConfigurationManager configurationManager;
    private LocalizationManager localizationManager;
    private IntegrationManager integrationManager;
    private DatabaseManager databaseManager;
    private CacheManager cacheManager;
    private SchedulerManager schedulerManager;
    private PermissionManager permissionManager;
    private dev.unleashed.smp.curse.CurseManager curseManager;
    private EventManager eventManager;
    private LuckyManager luckyManager;
    private MutantManager mutantManager;
    private GuiManager guiManager;
    private CommandManager commandManager;
    private ListenerManager listenerManager;
    private MetricsManager metricsManager;
    private ReloadManager reloadManager;
    private UnleashedPlaceholderExpansion placeholderExpansion;

    public Bootstrap(@NotNull UnleashedPlugin plugin, @NotNull PluginLogger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    public void enable() {
        final long start = System.currentTimeMillis();

        configurationManager = new ConfigurationManager(plugin, logger);
        configurationManager.loadAll();

        localizationManager = new LocalizationManager(plugin, logger, configurationManager);
        localizationManager.load();

        schedulerManager = new SchedulerManager(plugin);
        cacheManager = new CacheManager(plugin, logger, configurationManager);

        integrationManager = new IntegrationManager(plugin, logger);
        integrationManager.detect();

        databaseManager = new DatabaseManager(plugin, logger, configurationManager, schedulerManager);
        databaseManager.connect();
        databaseManager.migrate();

        permissionManager = new PermissionManager(plugin, integrationManager);

        curseManager = new dev.unleashed.smp.curse.CurseManager(logger);

        eventManager = new EventManager(plugin, logger, configurationManager, localizationManager,
                schedulerManager, databaseManager, integrationManager, cacheManager, curseManager);
        eventManager.registerDefaults();
        eventManager.startScheduler();

        luckyManager = new LuckyManager(plugin, logger, configurationManager, localizationManager,
                schedulerManager, integrationManager, curseManager);

        mutantManager = new MutantManager(plugin, logger, configurationManager, localizationManager,
                integrationManager);

        guiManager = new GuiManager(plugin, logger, configurationManager, localizationManager,
                eventManager, luckyManager, mutantManager);

        commandManager = new CommandManager(plugin, logger, configurationManager, localizationManager,
                eventManager, luckyManager, mutantManager, guiManager, permissionManager,
                integrationManager, schedulerManager, curseManager);
        commandManager.registerCommands();

        listenerManager = new ListenerManager(plugin, logger, eventManager, luckyManager,
                mutantManager, configurationManager);
        listenerManager.register();

        if (integrationManager.isPlaceholderApiPresent()) {
            placeholderExpansion = new UnleashedPlaceholderExpansion(plugin, eventManager,
                    luckyManager, mutantManager);
            placeholderExpansion.register();
            logger.integration("PlaceholderAPI expansion registered");
        }

        metricsManager = new MetricsManager(plugin, logger);
        metricsManager.start();

        reloadManager = new ReloadManager(plugin, logger, this);
        reloadManager.register();

        logger.info("UnleashedSMP enabled in %dms", System.currentTimeMillis() - start);
    }

    public void disable() {
        if (eventManager != null) {
            eventManager.shutdown();
        }
        if (luckyManager != null) {
            luckyManager.shutdown();
        }
        if (mutantManager != null) {
            mutantManager.shutdown();
        }
        if (guiManager != null) {
            guiManager.shutdown();
        }
        if (listenerManager != null) {
            listenerManager.unregister();
        }
        if (placeholderExpansion != null) {
            placeholderExpansion.unregister();
        }
        if (commandManager != null) {
            commandManager.unregister();
        }
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        if (schedulerManager != null) {
            schedulerManager.shutdown();
        }
        if (metricsManager != null) {
            metricsManager.shutdown();
        }
        logger.info("UnleashedSMP disabled");
    }

    public void reload() {
        configurationManager.loadAll();
        localizationManager.reload();
        databaseManager.reload();
        cacheManager.reload();
        eventManager.reload();
        luckyManager.reload();
        mutantManager.reload();
        guiManager.reload();
        logger.info("UnleashedSMP reloaded");
    }

    @NotNull
    public UnleashedPlugin getPlugin() {
        return plugin;
    }

    @NotNull
    public PluginLogger getLogger() {
        return logger;
    }

    @NotNull
    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    @NotNull
    public LocalizationManager getLocalizationManager() {
        return localizationManager;
    }

    @NotNull
    public IntegrationManager getIntegrationManager() {
        return integrationManager;
    }

    @NotNull
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @NotNull
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    @NotNull
    public SchedulerManager getSchedulerManager() {
        return schedulerManager;
    }

    @NotNull
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    @NotNull
    public dev.unleashed.smp.curse.CurseManager getCurseManager() {
        return curseManager;
    }

    @NotNull
    public EventManager getEventManager() {
        return eventManager;
    }

    @NotNull
    public LuckyManager getLuckyManager() {
        return luckyManager;
    }

    @NotNull
    public MutantManager getMutantManager() {
        return mutantManager;
    }

    @NotNull
    public GuiManager getGuiManager() {
        return guiManager;
    }

    @NotNull
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @NotNull
    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    @NotNull
    public MetricsManager getMetricsManager() {
        return metricsManager;
    }

    @NotNull
    public ReloadManager getReloadManager() {
        return reloadManager;
    }
}
