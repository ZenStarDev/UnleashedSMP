package dev.unleashed.smp.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class BossSpawnEvent extends AbstractEvent {
    public BossSpawnEvent() { super("boss_spawn"); }
    @Override public @NotNull String getDescription() { return "Spawns a powerful boss."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final String typeName = ctx.config().events().getString("events.boss_spawn.settings.boss-type", "WITHER");
        final double health = ctx.config().events().getDouble("events.boss_spawn.settings.health", 500);
        final EntityType type;
        try { type = EntityType.valueOf(typeName); } catch (IllegalArgumentException ex) { return; }
        if (!org.bukkit.entity.LivingEntity.class.isAssignableFrom(type.getEntityClass())) return;
        for (Player p : Bukkit.getOnlinePlayers()) {
            final Location loc = p.getLocation().add(0, 3, 5);
            final org.bukkit.entity.LivingEntity boss = (org.bukkit.entity.LivingEntity) p.getWorld().spawnEntity(loc, type);
            final net.kyori.adventure.text.Component name = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<red>Boss</red>");
            boss.setCustomName(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(name));
            boss.setCustomNameVisible(true);
            boss.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).setBaseValue(health);
            boss.setHealth(health);
        }
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) { }
}
