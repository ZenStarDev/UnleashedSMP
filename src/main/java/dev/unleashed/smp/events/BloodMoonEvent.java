package dev.unleashed.smp.events;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class BloodMoonEvent extends AbstractEvent {
    private int taskId = -1;
    public BloodMoonEvent() { super("blood_moon"); }
    @Override public @NotNull String getDescription() { return "Red moon with stronger mobs."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final double multiplier = ctx.config().events().getDouble("events.blood_moon.settings.mob-multiplier", 2.0);
        final int strAmp = (int) Math.max(0, multiplier - 1);
        final int spdAmp = (int) Math.max(0, multiplier - 1);
        for (World w : Bukkit.getWorlds()) w.setTime(14000);
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (org.bukkit.entity.Entity e : Bukkit.getWorlds().stream().flatMap(w -> w.getEntities().stream()).toList()) {
                if (e instanceof org.bukkit.entity.LivingEntity le && !(e instanceof Player)) {
                    le.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.STRENGTH, 60, strAmp, true, false));
                    le.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, 60, spdAmp, true, false));
                }
            }
        }, 20L, 60L);
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        if (taskId != -1) {
            ctx.scheduler().cancelTask(taskId);
            taskId = -1;
        }
        for (World w : Bukkit.getWorlds()) w.setTime(6000);
    }
}
