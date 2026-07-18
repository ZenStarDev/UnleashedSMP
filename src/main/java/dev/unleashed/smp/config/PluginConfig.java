package dev.unleashed.smp.config;

import dev.unleashed.smp.exceptions.ConfigurationException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Thin wrapper around a Bukkit {@link YamlConfiguration} that guarantees the backing file exists
 * and provides convenient accessors with sane defaults.
 *
 * <p>Instances are reloadable via {@link #reload()}.</p>
 */
public class PluginConfig {

    private final JavaPlugin plugin;
    private final String fileName;
    private final File file;
    private YamlConfiguration yaml;

    public PluginConfig(@NotNull JavaPlugin plugin, @NotNull String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = plugin == null ? null : new File(plugin.getDataFolder(), fileName);
        if (plugin != null) {
            load();
        }
    }

    /**
     * Loads (or reloads) the configuration from disk, extracting the bundled default if missing.
     */
    public void load() {
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
        yaml = new YamlConfiguration();
        try {
            yaml.load(file);
        } catch (InvalidConfigurationException | IOException ex) {
            throw new ConfigurationException("Failed to load " + fileName, ex);
        }
    }

    public void reload() {
        load();
    }

    public void save() {
        try {
            yaml.save(file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save " + fileName, ex);
        }
    }

    public @NotNull YamlConfiguration getYaml() {
        return yaml;
    }

    public @NotNull File getFile() {
        return file;
    }

    public @NotNull String getFileName() {
        return fileName;
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        return getYaml().getBoolean(path, def);
    }

    public int getInt(@NotNull String path, int def) {
        return getYaml().getInt(path, def);
    }

    public long getLong(@NotNull String path, long def) {
        return getYaml().getLong(path, def);
    }

    public double getDouble(@NotNull String path, double def) {
        return getYaml().getDouble(path, def);
    }

    public @Nullable String getString(@NotNull String path) {
        return getYaml().getString(path);
    }

    public @NotNull String getString(@NotNull String path, @NotNull String def) {
        return getYaml().getString(path, def);
    }

    public @NotNull List<String> getStringList(@NotNull String path) {
        return getYaml().getStringList(path);
    }

    public @Nullable ConfigurationSection getSection(@NotNull String path) {
        return getYaml().getConfigurationSection(path);
    }

    public @NotNull Set<String> getKeys(@NotNull String path) {
        final ConfigurationSection section = getYaml().getConfigurationSection(path);
        return section == null ? Set.of() : section.getKeys(false);
    }

    public boolean contains(@NotNull String path) {
        return getYaml().contains(path);
    }

    public void set(@NotNull String path, @Nullable Object value) {
        getYaml().set(path, value);
    }
}
