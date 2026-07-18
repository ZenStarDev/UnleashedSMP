package dev.unleashed.smp.commands;

import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.managers.PermissionManager;
import dev.unleashed.smp.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class HelpCommand implements org.bukkit.command.CommandExecutor {
    private final LocalizationManager localizationManager;
    private final PermissionManager permissionManager;

    private static final Map<String, String> COMMANDS = Map.of(
            "/unleashed", "Main command / open GUI",
            "/event <name>", "Start an event",
            "/eventall <name>", "Start an event for everyone",
            "/lucky", "Roll the lucky system",
            "/reload unleashed", "Reload the plugin",
            "/debug", "Toggle debug mode",
            "/help", "Show this help"
    );

    public HelpCommand(@NotNull LocalizationManager localizationManager, @NotNull PermissionManager permissionManager) {
        this.localizationManager = localizationManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("help-header")));
        for (var entry : COMMANDS.entrySet()) {
            MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("help-line",
                    "%command%", entry.getKey(), "%description%", entry.getValue())));
        }
        MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("help-footer")));
        return true;
    }
}

