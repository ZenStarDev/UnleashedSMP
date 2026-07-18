package dev.unleashed.smp.commands;

import dev.unleashed.smp.bootstrap.Bootstrap;
import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.curse.CurseManager;
import dev.unleashed.smp.events.EventManager;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.logging.PluginLogger;
import dev.unleashed.smp.lucky.LuckyManager;
import dev.unleashed.smp.managers.PermissionManager;
import dev.unleashed.smp.managers.SchedulerManager;
import dev.unleashed.smp.mutant.MutantManager;
import dev.unleashed.smp.integrations.IntegrationManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Registers all plugin commands and wires them to their handlers.
 */
public final class CommandManager {

    private final JavaPlugin plugin;
    private final PluginLogger logger;
    private final ConfigurationManager configManager;
    private final LocalizationManager localizationManager;
    private final EventManager eventManager;
    private final LuckyManager luckyManager;
    private final MutantManager mutantManager;
    private final dev.unleashed.smp.gui.GuiManager guiManager;
    private final PermissionManager permissionManager;
    private final IntegrationManager integrationManager;
    private final SchedulerManager schedulerManager;
    private final CurseManager curseManager;

    public CommandManager(@NotNull JavaPlugin plugin, @NotNull PluginLogger logger,
                          @NotNull ConfigurationManager configManager,
                          @NotNull LocalizationManager localizationManager,
                          @NotNull EventManager eventManager, @NotNull LuckyManager luckyManager,
                          @NotNull MutantManager mutantManager, @NotNull dev.unleashed.smp.gui.GuiManager guiManager,
                          @NotNull PermissionManager permissionManager,
                          @NotNull IntegrationManager integrationManager,
                          @NotNull SchedulerManager schedulerManager,
                          @NotNull CurseManager curseManager) {
        this.plugin = plugin;
        this.logger = logger;
        this.configManager = configManager;
        this.localizationManager = localizationManager;
        this.eventManager = eventManager;
        this.luckyManager = luckyManager;
        this.mutantManager = mutantManager;
        this.guiManager = guiManager;
        this.permissionManager = permissionManager;
        this.integrationManager = integrationManager;
        this.schedulerManager = schedulerManager;
        this.curseManager = curseManager;
    }

    public void registerCommands() {
        plugin.getCommand("unleashed").setExecutor(new UnleashedCommand(plugin, logger, configManager,
                localizationManager, eventManager, luckyManager, mutantManager, guiManager,
                permissionManager, integrationManager, schedulerManager));
        plugin.getCommand("event").setExecutor(new EventCommand(eventManager, localizationManager, permissionManager));
        plugin.getCommand("eventall").setExecutor(new EventAllCommand(eventManager, localizationManager, permissionManager));
        plugin.getCommand("lucky").setExecutor(new LuckyCommand(luckyManager, localizationManager, permissionManager));
        plugin.getCommand("reload").setExecutor(new ReloadCommand(((dev.unleashed.smp.UnleashedPlugin) plugin).getBootstrap().getReloadManager(), localizationManager, permissionManager));
        plugin.getCommand("debug").setExecutor(new DebugCommand(logger, configManager, localizationManager, permissionManager));
        plugin.getCommand("help").setExecutor(new HelpCommand(localizationManager, permissionManager));
        plugin.getCommand("admin").setExecutor(new AdminCommand(plugin, logger, configManager, localizationManager,
                eventManager, luckyManager, mutantManager, guiManager, permissionManager, curseManager));
    }

    public void unregister() {
        // Executors are cleared on plugin disable automatically.
    }
}
