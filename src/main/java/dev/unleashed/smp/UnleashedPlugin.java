package dev.unleashed.smp;

import dev.unleashed.smp.bootstrap.Bootstrap;
import dev.unleashed.smp.logging.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main entry point for the UnleashedSMP plugin.
 *
 * <p>This class is intentionally thin. All real work is delegated to the {@link Bootstrap},
 * keeping the plugin compatible with dependency-injection-friendly construction and isolating
 * Bukkit lifecycle callbacks from the application logic.</p>
 */
public final class UnleashedPlugin extends JavaPlugin {

    private Bootstrap bootstrap;

    @Override
    public void onLoad() {
        // Reserved for early setup (e.g. registering custom registries) if needed in the future.
    }

    @Override
    public void onEnable() {
        final PluginLogger logger = new PluginLogger(this);
        this.bootstrap = new Bootstrap(this, logger);
        bootstrap.enable();
    }

    @Override
    public void onDisable() {
        if (bootstrap != null) {
            bootstrap.disable();
            bootstrap = null;
        }
    }

    /**
     * Exposes the bootstrap so modules can resolve shared services through it.
     *
     * @return the active bootstrap, or {@code null} if the plugin is disabled
     */
    public Bootstrap getBootstrap() {
        return bootstrap;
    }
}
