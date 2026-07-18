package dev.unleashed.smp.placeholders;

import dev.unleashed.smp.events.EventManager;
import dev.unleashed.smp.lucky.LuckyManager;
import dev.unleashed.smp.mutant.MutantManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PlaceholderAPI expansion exposing plugin state.
 *
 * <ul>
 *   <li>%unleashed_event% - currently running events</li>
 *   <li>%unleashed_lucky% - whether lucky is on cooldown (yes/no)</li>
 *   <li>%unleashed_mutant% - number of mutant definitions</li>
 *   <li>%unleashed_players% - online player count</li>
 * </ul>
 */
public final class UnleashedPlaceholderExpansion extends PlaceholderExpansion {

    private final JavaPlugin plugin;
    private final EventManager eventManager;
    private final LuckyManager luckyManager;
    private final MutantManager mutantManager;

    public UnleashedPlaceholderExpansion(@NotNull JavaPlugin plugin, @NotNull EventManager eventManager,
                                         @NotNull LuckyManager luckyManager, @NotNull MutantManager mutantManager) {
        this.plugin = plugin;
        this.eventManager = eventManager;
        this.luckyManager = luckyManager;
        this.mutantManager = mutantManager;
    }

    @Override
    public @NotNull String getIdentifier() { return "unleashed"; }

    @Override
    public @NotNull String getAuthor() { return "UnleashedSMP"; }

    @Override
    public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }

    @Override
    public boolean persist() { return true; }

    @Override
    public @Nullable String onRequest(@Nullable OfflinePlayer player, @NotNull String params) {
        return switch (params.toLowerCase()) {
            case "event" -> eventManager.getRunningEvents().stream()
                    .map(e -> e.getId()).collect(Collectors.joining(", "));
            case "lucky" -> player != null && player.getPlayer() != null && luckyManager.onCooldown(player.getPlayer())
                    ? "cooldown" : "ready";
            case "mutant" -> String.valueOf(mutantManager.getDefinitions().size());
            case "players" -> String.valueOf(org.bukkit.Bukkit.getOnlinePlayers().size());
            default -> null;
        };
    }
}
