package dev.unleashed.smp.logging;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Structured, level-aware logger for the plugin.
 *
 * <p>Supports a debug mode that can be toggled at runtime and provides dedicated methods for
 * integration, database, and performance related logging so that output stays consistent and
 * console spam can be controlled.</p>
 */
public final class PluginLogger {

    private final JavaPlugin plugin;
    private volatile boolean debugEnabled;

    public PluginLogger(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.debugEnabled = false;
    }

    public void info(@NotNull String message, Object... args) {
        plugin.getLogger().info(format(message, args));
    }

    public void warn(@NotNull String message, Object... args) {
        plugin.getLogger().warning(format(message, args));
    }

    public void error(@NotNull String message, Object... args) {
        plugin.getLogger().severe(format(message, args));
    }

    public void error(@NotNull String message, @NotNull Throwable throwable) {
        plugin.getLogger().severe(message);
        throwable.printStackTrace();
    }

    public void debug(@NotNull String message, Object... args) {
        if (debugEnabled) {
            plugin.getLogger().info("[DEBUG] " + format(message, args));
        }
    }

    public void integration(@NotNull String message, Object... args) {
        debug("[INTEGRATION] " + message, args);
    }

    public void database(@NotNull String message, Object... args) {
        debug("[DATABASE] " + message, args);
    }

    public void performance(@NotNull String message, Object... args) {
        debug("[PERFORMANCE] " + message, args);
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    private static String format(@NotNull String message, Object... args) {
        if (args.length == 0) {
            return message;
        }
        try {
            return String.format(message, args);
        } catch (RuntimeException ex) {
            return message + " " + java.util.Arrays.toString(args);
        }
    }
}
