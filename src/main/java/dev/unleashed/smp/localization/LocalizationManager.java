package dev.unleashed.smp.localization;

import dev.unleashed.smp.UnleashedPlugin;
import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.logging.PluginLogger;
import dev.unleashed.smp.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Resolves localized, MiniMessage-formatted message keys. Supports runtime language switching and
 * PlaceholderAPI placeholders for players.
 */
public final class LocalizationManager {

    private final JavaPlugin plugin;
    private final PluginLogger logger;
    private final ConfigurationManager configManager;

    private final Map<String, YamlConfiguration> locales = new HashMap<>();
    private volatile String defaultLanguage;

    public LocalizationManager(@NotNull JavaPlugin plugin, @NotNull PluginLogger logger,
                               @NotNull ConfigurationManager configManager) {
        this.plugin = plugin;
        this.logger = logger;
        this.configManager = configManager;
    }

    public void load() {
        locales.clear();
        defaultLanguage = configManager.config().getString("general.default-language", "en_US");
        loadLocale("en_US");
        loadLocale("th_TH");
        logger.info("Loaded %d locales (default: %s)", locales.size(), defaultLanguage);
    }

    public void reload() {
        load();
    }

    private void loadLocale(@NotNull String lang) {
        final String resource = "lang/" + lang + ".yml";
        try (InputStream in = plugin.getResource(resource)) {
            if (in == null) {
                return;
            }
            final YamlConfiguration cfg = new YamlConfiguration();
            cfg.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            locales.put(lang, cfg);
        } catch (Exception ex) {
            logger.warn("Failed to load locale %s: %s", lang, ex.getMessage());
        }
    }

    /**
     * Resolves a key for the default language, parsed as MiniMessage.
     */
    public @NotNull Component get(@NotNull String key) {
        return MessageUtils.parse(resolveRaw(key, defaultLanguage));
    }

    /**
     * Resolves a key for a player (their locale if known, else default), with PAPI placeholders.
     */
    public @NotNull Component get(@NotNull Player player, @NotNull String key, Object... placeholders) {
        String raw = resolveRaw(key, defaultLanguage);
        for (int i = 0; i + 1 < placeholders.length; i += 2) {
            raw = raw.replace(String.valueOf(placeholders[i]), String.valueOf(placeholders[i + 1]));
        }
        return MessageUtils.parse(player, raw);
    }

    /**
     * Resolves a key with simple %placeholder% substitution for a player.
     */
    public @NotNull Component get(@NotNull String key, Object... placeholders) {
        String raw = resolveRaw(key, defaultLanguage);
        for (int i = 0; i + 1 < placeholders.length; i += 2) {
            raw = raw.replace(String.valueOf(placeholders[i]), String.valueOf(placeholders[i + 1]));
        }
        return MessageUtils.parse(raw);
    }

    private @NotNull String resolveRaw(@NotNull String key, @NotNull String lang) {
        final YamlConfiguration cfg = locales.get(lang);
        if (cfg != null && cfg.contains(key)) {
            return cfg.getString(key, key);
        }
        final YamlConfiguration def = locales.get(defaultLanguage);
        if (def != null && def.contains(key)) {
            return def.getString(key, key);
        }
        return configManager.messages().getString(key, key);
    }

    public void setDefaultLanguage(@NotNull String lang) {
        this.defaultLanguage = lang;
        configManager.config().set("general.default-language", lang);
        configManager.config().save();
    }

    public @NotNull String getDefaultLanguage() {
        return defaultLanguage;
    }
}
