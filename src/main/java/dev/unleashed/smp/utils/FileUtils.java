package dev.unleashed.smp.utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * File utilities for resource extraction and safe IO.
 */
public final class FileUtils {

    private FileUtils() {
    }

    /**
     * Copies a bundled resource into the plugin data folder if it does not already exist.
     *
     * @param plugin   the plugin
     * @param resource the resource path inside the jar
     * @param fileName the destination file name in the data folder
     */
    public static void saveResourceIfAbsent(@org.jetbrains.annotations.NotNull JavaPlugin plugin,
                                            @org.jetbrains.annotations.NotNull String resource,
                                            @org.jetbrains.annotations.NotNull String fileName) {
        final File file = new File(plugin.getDataFolder(), fileName);
        if (file.exists()) {
            return;
        }
        final File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (InputStream in = plugin.getResource(resource)) {
            if (in == null) {
                plugin.getLogger().warning("Resource not found in jar: " + resource);
                return;
            }
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            plugin.getLogger().severe("Could not save resource " + fileName + ": " + ex.getMessage());
        }
    }

    /**
     * @return the plugin data folder, creating it if necessary
     */
    public static @org.jetbrains.annotations.NotNull File ensureDataFolder(@org.jetbrains.annotations.NotNull JavaPlugin plugin) {
        final File folder = plugin.getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }
}
