package dev.unleashed.smp.events;

import dev.unleashed.smp.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class MobBuffEvent extends AbstractEvent {
    private int taskId = -1;
    public MobBuffEvent() { super("mob_buff"); }
    @Override public @NotNull String getDescription() { return "All mobs gain buffs."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final int str = (int) ctx.config().events().getDouble("events.mob_buff.settings.strength", 2);
        final int spd = (int) ctx.config().events().getDouble("events.mob_buff.settings.speed", 1);
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (org.bukkit.entity.Entity e : Bukkit.getWorlds().stream().flatMap(w -> w.getEntities().stream()).toList()) {
                if (e instanceof LivingEntity && !(e instanceof Player)) {
                    final LivingEntity le = (LivingEntity) e;
                    le.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 60, str, true, false));
                    le.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, spd, true, false));
                }
            }
        }, 20L, 60L);
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        if (taskId != -1) {
            ctx.scheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
}
