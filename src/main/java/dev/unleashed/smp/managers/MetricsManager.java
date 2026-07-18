package dev.unleashed.smp.managers;

import dev.unleashed.smp.UnleashedPlugin;
import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.logging.PluginLogger;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Lightweight internal metrics collector. Flushed periodically; respects the metrics-enabled flag.
 */
public final class MetricsManager {

    private final UnleashedPlugin plugin;
    private final PluginLogger logger;
    private final ConfigurationManager configManager;

    private int eventsStarted;
    private int luckyRolls;
    private int mutantsSpawned;
    private int commandsExecuted;

    public MetricsManager(@NotNull UnleashedPlugin plugin, @NotNull PluginLogger logger) {
        this.plugin = plugin;
        this.logger = logger;
        this.configManager = plugin.getBootstrap().getConfigurationManager();
    }

    public void start() {
        if (!configManager.config().getBoolean("general.metrics-enabled", true)) {
            return;
        }
        final long interval = configManager.scheduler().getLong("metrics-interval", 12000);
        Bukkit.getScheduler().runTaskTimer(plugin, this::flush, interval, interval);
    }

    public void shutdown() {
    }

    private void flush() {
        logger.performance("Metrics | events=%d lucky=%d mutants=%d cmds=%d cacheHit=%.2f",
                eventsStarted, luckyRolls, mutantsSpawned, commandsExecuted,
                plugin.getBootstrap().getCacheManager().hitRatio());
    }

    public void recordEventStart() { eventsStarted++; }
    public void recordLuckyRoll() { luckyRolls++; }
    public void recordMutantSpawn() { mutantsSpawned++; }
    public void recordCommand() { commandsExecuted++; }

    public int getEventsStarted() { return eventsStarted; }
    public int getLuckyRolls() { return luckyRolls; }
    public int getMutantsSpawned() { return mutantsSpawned; }
    public int getCommandsExecuted() { return commandsExecuted; }
}
