package dev.unleashed.smp.listeners;

import dev.unleashed.smp.events.EventManager;
import dev.unleashed.smp.lucky.LuckyManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Applies currently-running events to players as they join so they are not excluded mid-event.
 */
public final class PlayerJoinListener implements Listener {

    private final EventManager eventManager;
    private final LuckyManager luckyManager;

    public PlayerJoinListener(@NotNull EventManager eventManager, @NotNull LuckyManager luckyManager) {
        this.eventManager = eventManager;
        this.luckyManager = luckyManager;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        for (var running : eventManager.getRunningEvents()) {
            running.addPlayer(e.getPlayer());
        }
    }
}
