package dev.unleashed.smp.commands;

import dev.unleashed.smp.lucky.LuckyManager;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.managers.PermissionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class LuckyCommand implements org.bukkit.command.CommandExecutor {
    private final LuckyManager luckyManager;
    private final LocalizationManager localizationManager;
    private final PermissionManager permissionManager;

    public LuckyCommand(@NotNull LuckyManager luckyManager, @NotNull LocalizationManager localizationManager,
                        @NotNull PermissionManager permissionManager) {
        this.luckyManager = luckyManager;
        this.localizationManager = localizationManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!permissionManager.has(sender, "unleashed.command.lucky")) { noPerm(sender); return true; }
        if (!(sender instanceof Player player)) { sender.sendMessage(localizationManager.get("player-only").toString()); return true; }
        luckyManager.roll(player);
        return true;
    }

    private void noPerm(@NotNull CommandSender sender) { sender.sendMessage(localizationManager.get("no-permission").toString()); }
}
