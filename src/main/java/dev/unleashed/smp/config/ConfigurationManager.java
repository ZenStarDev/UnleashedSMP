package dev.unleashed.smp.config;

import dev.unleashed.smp.logging.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

/**
 * Owns every {@link PluginConfig} instance and exposes them through a typed enum.
 */
public final class ConfigurationManager {

    public enum ConfigType {
        CONFIG("config.yml"),
        MESSAGES("messages.yml"),
        PERMISSIONS("permissions.yml"),
        EVENTS("events.yml"),
        LUCKY("lucky.yml"),
        MUTANTS("mutants.yml"),
        GUI("gui.yml"),
        DATABASE("database.yml"),
        PERFORMANCE("performance.yml"),
        INTEGRATION("integration.yml"),
        DEBUG("debug.yml"),
        SCHEDULER("scheduler.yml");

        private final String fileName;

        ConfigType(@NotNull String fileName) {
            this.fileName = fileName;
        }

        public @NotNull String fileName() {
            return fileName;
        }
    }

    private final JavaPlugin plugin;
    private final PluginLogger logger;
    private final Map<ConfigType, PluginConfig> configs = new EnumMap<>(ConfigType.class);

    public ConfigurationManager(@NotNull JavaPlugin plugin, @NotNull PluginLogger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    public void loadAll() {
        for (ConfigType type : ConfigType.values()) {
            reload(type);
        }
        logger.info("Loaded %d configuration files", configs.size());
    }

    public void reload(@NotNull ConfigType type) {
        configs.put(type, new PluginConfig(plugin, type.fileName()));
    }

    public @NotNull PluginConfig get(@NotNull ConfigType type) {
        final PluginConfig config = configs.get(type);
        if (config == null) {
            throw new IllegalStateException("Config not loaded: " + type);
        }
        return config;
    }

    public @NotNull PluginConfig config() { return get(ConfigType.CONFIG); }
    public @NotNull PluginConfig messages() { return get(ConfigType.MESSAGES); }
    public @NotNull PluginConfig permissions() { return get(ConfigType.PERMISSIONS); }
    public @NotNull PluginConfig events() { return get(ConfigType.EVENTS); }
    public @NotNull PluginConfig lucky() { return get(ConfigType.LUCKY); }
    public @NotNull PluginConfig mutants() { return get(ConfigType.MUTANTS); }
    public @NotNull PluginConfig gui() { return get(ConfigType.GUI); }
    public @NotNull PluginConfig database() { return get(ConfigType.DATABASE); }
    public @NotNull PluginConfig performance() { return get(ConfigType.PERFORMANCE); }
    public @NotNull PluginConfig integration() { return get(ConfigType.INTEGRATION); }
    public @NotNull PluginConfig debug() { return get(ConfigType.DEBUG); }
    public @NotNull PluginConfig scheduler() { return get(ConfigType.SCHEDULER); }
}
