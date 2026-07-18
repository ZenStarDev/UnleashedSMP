package dev.unleashed.smp.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class GravityEvent extends AbstractEvent {
    public GravityEvent() { super("gravity"); }
    @Override public @NotNull String getDescription() { return "Inverted gravity effect."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        for (Player p : Bukkit.getOnlinePlayers()) { p.setAllowFlight(true); p.setFlying(true); }
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
                p.setFlying(false); p.setAllowFlight(false);
            }
        }
    }
}
