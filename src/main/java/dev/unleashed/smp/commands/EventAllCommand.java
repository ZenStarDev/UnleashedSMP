package dev.unleashed.smp.commands;

import dev.unleashed.smp.events.EventManager;
import dev.unleashed.smp.events.GameEvent;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.managers.PermissionManager;
import dev.unleashed.smp.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EventAllCommand implements org.bukkit.command.CommandExecutor, org.bukkit.command.TabCompleter {
    private final EventManager eventManager;
    private final LocalizationManager localizationManager;
    private final PermissionManager permissionManager;

    public EventAllCommand(@NotNull EventManager eventManager, @NotNull LocalizationManager localizationManager,
                           @NotNull PermissionManager permissionManager) {
        this.eventManager = eventManager;
        this.localizationManager = localizationManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!permissionManager.has(sender, "unleashed.command.eventall")) { noPerm(sender); return true; }
        if (args.length == 0) { MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("invalid-usage", "%usage%", "/eventall <name>"))); return true; }
        final String name = args[0].toLowerCase();
        if (eventManager.getEvent(name) == null) {
            MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("event-not-found", "%event%", name)));
            return true;
        }
        final GameEvent event = eventManager.getEvent(name);
        if (event.getRequiredPermission() != null && !sender.hasPermission(event.getRequiredPermission())) {
            noPerm(sender);
            return true;
        }
        eventManager.start(name, Bukkit.getOnlinePlayers());
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                               @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            final List<String> ids = new ArrayList<>();
            for (var e : eventManager.getEvents()) ids.add(e.getId());
            return ids;
        }
        return Collections.emptyList();
    }

    private void noPerm(@NotNull CommandSender sender) { MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("no-permission"))); }
}

