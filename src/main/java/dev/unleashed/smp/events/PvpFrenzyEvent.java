package dev.unleashed.smp.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class PvpFrenzyEvent extends AbstractEvent {
    public PvpFrenzyEvent() { super("pvp_frenzy"); }
    @Override public @NotNull String getDescription() { return "PvP enabled everywhere with buffs."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int str = (int) ctx.config().events().getDouble("events.pvp_frenzy.settings.strength", 1);
        Bukkit.getWorlds().forEach(w -> w.setPVP(true));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 100000, str, true, false));
        }
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        Bukkit.getWorlds().forEach(w -> w.setPVP(false));
        for (Player p : Bukkit.getOnlinePlayers()) p.removePotionEffect(PotionEffectType.STRENGTH);
    }
}
