package dev.unleashed.smp.commands;

import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.curse.CurseManager;
import dev.unleashed.smp.events.EventManager;
import dev.unleashed.smp.gui.GuiManager;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.logging.PluginLogger;
import dev.unleashed.smp.lucky.LuckyManager;
import dev.unleashed.smp.managers.PermissionManager;
import dev.unleashed.smp.mutant.MutantManager;
import dev.unleashed.smp.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AdminCommand implements org.bukkit.command.CommandExecutor, org.bukkit.command.TabCompleter {
    private final JavaPlugin plugin;
    private final PluginLogger logger;
    private final ConfigurationManager configManager;
    private final LocalizationManager localizationManager;
    private final EventManager eventManager;
    private final LuckyManager luckyManager;
    private final MutantManager mutantManager;
    private final GuiManager guiManager;
    private final PermissionManager permissionManager;
    private final CurseManager curseManager;

    public AdminCommand(@NotNull JavaPlugin plugin, @NotNull PluginLogger logger,
                        @NotNull ConfigurationManager configManager,
                        @NotNull LocalizationManager localizationManager,
                        @NotNull EventManager eventManager, @NotNull LuckyManager luckyManager,
                        @NotNull MutantManager mutantManager, @NotNull GuiManager guiManager,
                        @NotNull PermissionManager permissionManager,
                        @NotNull CurseManager curseManager) {
        this.plugin = plugin; this.logger = logger; this.configManager = configManager;
        this.localizationManager = localizationManager; this.eventManager = eventManager;
        this.luckyManager = luckyManager; this.mutantManager = mutantManager; this.guiManager = guiManager;
        this.permissionManager = permissionManager; this.curseManager = curseManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!permissionManager.has(sender, "unleashed.command.admin")) { noPerm(sender); return true; }
        if (args.length == 0) {
            if (sender instanceof Player p) guiManager.openMain(p);
            else MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("help-header")));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "stop" -> {
                if (args.length > 1) eventManager.stop(args[1]);
                else for (var e : eventManager.getRunningEvents()) eventManager.stop(e.getId());
            }
            case "pause" -> { if (args.length > 1) eventManager.pause(args[1]); }
            case "resume" -> { if (args.length > 1) eventManager.resume(args[1]); }
            case "cancelcountdown", "cc" -> {
                if (args.length > 1) {
                    eventManager.cancelCountdown(args[1]);
                    MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("event-countdown-cancelled", "%event%", args[1])));
                }
            }
            case "pending" -> {
                MessageUtils.send(sender, MessageUtils.parse("<gold>Pending Countdowns:</gold>"));
                for (var p : eventManager.getPendingEvents()) {
                    MessageUtils.send(sender, MessageUtils.parse(" <gray>-</gray> <aqua>" + p.getEvent().getId() + "</aqua> <gray>(" + (p.getRemainingTicks() / 20) + "s)</gray>"));
                }
            }
            case "luck" -> {
                if (args.length < 3) { MessageUtils.send(sender, MessageUtils.parse("<yellow>Usage: /admin luck <player> <level></yellow>")); return true; }
                final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { MessageUtils.send(sender, MessageUtils.parse("<red>Player not found.</red>")); return true; }
                final int level;
                try { level = Integer.parseInt(args[2]); }
                catch (NumberFormatException ex) { MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("not-number", "%input%", args[2]))); return true; }
                curseManager.setLuck(target.getUniqueId(), level);
                MessageUtils.send(sender, MessageUtils.parse("<green>Set luck of " + target.getName() + " to " + level + "</green>"));
            }
            case "list" -> {
                MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("event-list-header")));
                for (var e : eventManager.getRunningEvents()) {
                    MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("event-list-entry",
                            "%event%", e.getId(), "%remaining%", String.valueOf(e.getRemainingTicks() / 20))));
                }
            }
            default -> MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("unknown-command")));
        }
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                               @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return List.of("stop", "pause", "resume", "cancelcountdown", "cc", "pending", "luck", "list");
        if (args.length == 2 && (args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("pause") || args[0].equalsIgnoreCase("resume") || args[0].equalsIgnoreCase("cancelcountdown") || args[0].equalsIgnoreCase("cc"))) {
            final List<String> ids = new java.util.ArrayList<>();
            for (var e : eventManager.getEvents()) ids.add(e.getId());
            return ids;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("luck")) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return Collections.emptyList();
    }

    private void noPerm(@NotNull CommandSender sender) { MessageUtils.send(sender, MessageUtils.parse(localizationManager.get("no-permission"))); }
}

