package dev.unleashed.smp.managers;

import dev.unleashed.smp.bootstrap.Bootstrap;
import dev.unleashed.smp.logging.PluginLogger;
import dev.unleashed.smp.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Coordinates a full plugin reload. Configuration first, then dependent subsystems, sequentially.
 */
public final class ReloadManager {

    private final org.bukkit.plugin.java.JavaPlugin plugin;
    private final PluginLogger logger;
    private final Bootstrap bootstrap;

    public ReloadManager(@NotNull org.bukkit.plugin.java.JavaPlugin plugin,
                          @NotNull PluginLogger logger, @NotNull Bootstrap bootstrap) {
        this.plugin = plugin;
        this.logger = logger;
        this.bootstrap = bootstrap;
    }

    public void register() {
        // ReloadManager has no event handlers; registration is a no-op.
    }


    public void reload(@NotNull CommandSender sender) {
        final long start = System.currentTimeMillis();
        try {
            bootstrap.reload();
            if (sender != null) {
                MessageUtils.send(sender, MessageUtils.parse(
                        bootstrap.getConfigurationManager().messages().getString("reload-success", "<green>Reloaded.</green>")));
            }
            logger.info("Reload completed in %dms", System.currentTimeMillis() - start);
        } catch (RuntimeException ex) {
            logger.error("Reload failed", ex);
            if (sender != null) {
                final String msg = bootstrap.getConfigurationManager().messages()
                        .getString("reload-failed", "<red>Reload failed.</red>")
                        .replace("%reason%", ex.getMessage() == null ? "unknown" : ex.getMessage());
                MessageUtils.send(sender, MessageUtils.parse(msg));
            }
        }
    }
}

