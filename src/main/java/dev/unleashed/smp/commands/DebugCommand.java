package dev.unleashed.smp.commands;

import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.logging.PluginLogger;
import dev.unleashed.smp.managers.PermissionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class DebugCommand implements org.bukkit.command.CommandExecutor {
    private final PluginLogger logger;
    private final ConfigurationManager configManager;
    private final LocalizationManager localizationManager;
    private final PermissionManager permissionManager;

    public DebugCommand(@NotNull PluginLogger logger, @NotNull ConfigurationManager configManager,
                        @NotNull LocalizationManager localizationManager, @NotNull PermissionManager permissionManager) {
        this.logger = logger;
        this.configManager = configManager;
        this.localizationManager = localizationManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!permissionManager.has(sender, "unleashed.command.debug")) { noPerm(sender); return true; }
        final boolean next = !logger.isDebugEnabled();
        logger.setDebugEnabled(next);
        configManager.debug().set("enabled", next);
        configManager.debug().save();
        sender.sendMessage(localizationManager.get(next ? "debug-enabled" : "debug-disabled").toString());
        return true;
    }

    private void noPerm(@NotNull CommandSender sender) { sender.sendMessage(localizationManager.get("no-permission").toString()); }
}
