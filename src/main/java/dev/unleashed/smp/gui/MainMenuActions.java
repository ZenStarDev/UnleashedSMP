package dev.unleashed.smp.gui;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Holds click actions for the main menu slots keyed by inventory slot.
 */
final class MainMenuActions {
    private static final Map<Integer, Consumer<Player>> ACTIONS = new ConcurrentHashMap<>();

    private MainMenuActions() { }

    static void register(int slot, @NotNull Consumer<Player> action) {
        ACTIONS.put(slot, action);
    }

    static void run(int slot, @NotNull Player player) {
        final Consumer<Player> action = ACTIONS.get(slot);
        if (action != null) action.accept(player);
    }

    static void clear() { ACTIONS.clear(); }
}
