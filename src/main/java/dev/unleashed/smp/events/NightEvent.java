package dev.unleashed.smp.events;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class NightEvent extends AbstractEvent {
    public NightEvent() { super("night"); }
    @Override public @NotNull String getDescription() { return "Forces night time."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final long time = (long) ctx.config().events().getDouble("events.night.settings.set-time", 13000);
        for (World w : Bukkit.getWorlds()) w.setTime(time);
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        for (World w : Bukkit.getWorlds()) w.setTime(6000);
    }
}
