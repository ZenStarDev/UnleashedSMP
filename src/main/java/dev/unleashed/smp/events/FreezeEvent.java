package dev.unleashed.smp.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class FreezeEvent extends AbstractEvent {
    public FreezeEvent() { super("freeze"); }
    @Override public @NotNull String getDescription() { return "Players are slowed/frozen."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int amp = cfgInt(ctx, "settings.amplifier", 4);
        for (Player p : Bukkit.getOnlinePlayers()) p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100000, amp, true, false));
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        for (Player p : Bukkit.getOnlinePlayers()) p.removePotionEffect(PotionEffectType.SLOWNESS);
    }
}
