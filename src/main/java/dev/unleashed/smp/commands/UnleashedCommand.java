package dev.unleashed.smp.commands;

import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.events.EventManager;
import dev.unleashed.smp.gui.GuiManager;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.logging.PluginLogger;
import dev.unleashed.smp.lucky.LuckyManager;
import dev.unleashed.smp.managers.PermissionManager;
import dev.unleashed.smp.managers.SchedulerManager;
import dev.unleashed.smp.mutant.MutantManager;
import dev.unleashed.smp.integrations.IntegrationManager;
import dev.unleashed.smp.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public final class UnleashedCommand implements org.bukkit.command.CommandExecutor, org.bukkit.command.TabCompleter {
    private final JavaPlugin plugin;
    private final PluginLogger logger;
    private final ConfigurationManager configManager;
    private final LocalizationManager localizationManager;
    private final EventManager eventManager;
    private final LuckyManager luckyManager;
    private final MutantManager mutantManager;
    private final GuiManager guiManager;
    private final PermissionManager permissionManager;
    private final IntegrationManager integrationManager;
    private final SchedulerManager schedulerManager;

    public UnleashedCommand(@NotNull JavaPlugin plugin, @NotNull PluginLogger logger,
                            @NotNull ConfigurationManager configManager,
                            @NotNull LocalizationManager localizationManager,
                            @NotNull EventManager eventManager, @NotNull LuckyManager luckyManager,
                            @NotNull MutantManager mutantManager, @NotNull GuiManager guiManager,
                            @NotNull PermissionManager permissionManager,
                            @NotNull IntegrationManager integrationManager,
                            @NotNull SchedulerManager schedulerManager) {
        this.plugin = plugin; this.logger = logger; this.configManager = configManager;
        this.localizationManager = localizationManager; this.eventManager = eventManager;
        this.luckyManager = luckyManager; this.mutantManager = mutantManager; this.guiManager = guiManager;
        this.permissionManager = permissionManager; this.integrationManager = integrationManager;
        this.schedulerManager = schedulerManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player p && permissionManager.has(p, "unleashed.gui.main")) {
                guiManager.openMain(p);
            } else {
                MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("help-header")));
            }
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!permissionManager.has(sender, "unleashed.command.reload")) {
                    noPerm(sender); return true;
                }
                ((dev.unleashed.smp.UnleashedPlugin) plugin).getBootstrap().getReloadManager().reload(sender);
            }
            case "events" -> {
                if (sender instanceof Player p) guiManager.openEvents(p);
                else listEvents(sender);
            }
            case "help" -> MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("help-header")));
            case "debug" -> {
                if (!permissionManager.has(sender, "unleashed.command.debug")) { noPerm(sender); return true; }
                boolean next = !logger.isDebugEnabled();
                logger.setDebugEnabled(next);
                MessageUtils.send(sender, MessageUtils.parse(next ? localizationManager.get("debug-enabled") : localizationManager.get("debug-disabled")));
            }
            default -> MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("unknown-command")));
        }
        return true;
    }

    private void listEvents(@NotNull CommandSender sender) {
        MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("event-list-header")));
        for (var e : eventManager.getEvents()) {
            MessageUtils.send(sender, MessageUtils.parse("<gray>-</gray> <aqua>" + e.getId() + "</aqua>"));
        }
    }

    private void noPerm(@NotNull CommandSender sender) {
        MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("no-permission")));
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                               @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("reload", "events", "help", "debug");
        }
        return Collections.emptyList();
    }
}
