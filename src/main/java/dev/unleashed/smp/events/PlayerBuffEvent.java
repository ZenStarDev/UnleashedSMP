package dev.unleashed.smp.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Set;

public final class PlayerBuffEvent extends AbstractEvent {
    public PlayerBuffEvent() { super("player_buff"); }
    @Override public @NotNull String getDescription() { return "All players gain buffs."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final List<String> effects = ctx.config().events().getStringList("events.player_buff.settings.effects");
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (String name : effects) {
                final PotionEffectType t = PotionEffectType.getByName(name);
                if (t != null) p.addPotionEffect(new PotionEffect(t, 100000, 1, true, false));
            }
        }
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final List<String> effects = ctx.config().events().getStringList("events.player_buff.settings.effects");
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (String name : effects) {
                final PotionEffectType t = PotionEffectType.getByName(name);
                if (t != null) p.removePotionEffect(t);
            }
        }
    }
}
