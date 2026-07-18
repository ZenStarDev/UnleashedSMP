package dev.unleashed.smp.events;

import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.integrations.IntegrationManager;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.logging.PluginLogger;
import dev.unleashed.smp.managers.CacheManager;
import dev.unleashed.smp.managers.SchedulerManager;
import dev.unleashed.smp.database.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class EventContext {
    private final JavaPlugin plugin;
    private final PluginLogger logger;
    private final ConfigurationManager configManager;
    private final LocalizationManager localizationManager;
    private final SchedulerManager schedulerManager;
    private final DatabaseManager databaseManager;
    private final IntegrationManager integrationManager;
    private final CacheManager cacheManager;

    public EventContext(@NotNull JavaPlugin plugin, @NotNull PluginLogger logger,
                        @NotNull ConfigurationManager configManager,
                        @NotNull LocalizationManager localizationManager,
                        @NotNull SchedulerManager schedulerManager,
                        @NotNull DatabaseManager databaseManager,
                        @NotNull IntegrationManager integrationManager,
                        @NotNull CacheManager cacheManager) {
        this.plugin = plugin; this.logger = logger; this.configManager = configManager;
        this.localizationManager = localizationManager; this.schedulerManager = schedulerManager;
        this.databaseManager = databaseManager; this.integrationManager = integrationManager;
        this.cacheManager = cacheManager;
    }
    public @NotNull JavaPlugin plugin() { return plugin; }
    public @NotNull PluginLogger logger() { return logger; }
    public @NotNull ConfigurationManager config() { return configManager; }
    public @NotNull LocalizationManager locale() { return localizationManager; }
    public @NotNull SchedulerManager scheduler() { return schedulerManager; }
    public @NotNull DatabaseManager database() { return databaseManager; }
    public @NotNull IntegrationManager integrations() { return integrationManager; }
    public @NotNull CacheManager cache() { return cacheManager; }
}
