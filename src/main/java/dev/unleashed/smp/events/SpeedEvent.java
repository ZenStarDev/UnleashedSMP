package dev.unleashed.smp.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class SpeedEvent extends AbstractEvent {
    public SpeedEvent() { super("speed"); }
    @Override public @NotNull String getDescription() { return "Players gain speed."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int amp = cfgInt(ctx, "settings.amplifier", 2);
        for (Player p : Bukkit.getOnlinePlayers()) p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, amp, true, false));
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        for (Player p : Bukkit.getOnlinePlayers()) p.removePotionEffect(PotionEffectType.SPEED);
    }
}
