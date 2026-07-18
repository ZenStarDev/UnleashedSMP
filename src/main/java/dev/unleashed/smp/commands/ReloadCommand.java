package dev.unleashed.smp.commands;

import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.managers.PermissionManager;
import dev.unleashed.smp.managers.ReloadManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class ReloadCommand implements org.bukkit.command.CommandExecutor {
    private final ReloadManager reloadManager;
    private final LocalizationManager localizationManager;
    private final PermissionManager permissionManager;

    public ReloadCommand(@NotNull ReloadManager reloadManager, @NotNull LocalizationManager localizationManager,
                         @NotNull PermissionManager permissionManager) {
        this.reloadManager = reloadManager;
        this.localizationManager = localizationManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!permissionManager.has(sender, "unleashed.command.reload")) { noPerm(sender); return true; }
        if (args.length > 0 && args[0].equalsIgnoreCase("unleashed")) {
            reloadManager.reload(sender);
            return true;
        }
        sender.sendMessage(localizationManager.get("invalid-usage", "%usage%", "/reload unleashed").toString());
        return true;
    }

    private void noPerm(@NotNull CommandSender sender) { sender.sendMessage(localizationManager.get("no-permission").toString()); }
}

