package dev.unleashed.smp.mutant;

import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.integrations.IntegrationManager;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.logging.PluginLogger;
import dev.unleashed.smp.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spawns and manages mutant mobs with scaling stats, boss bars, and custom loot.
 */
public final class MutantManager implements Listener {

    private final JavaPlugin plugin;
    private final PluginLogger logger;
    private final ConfigurationManager configManager;
    private final LocalizationManager localizationManager;
    private final IntegrationManager integrationManager;

    private final Map<String, MutantDefinition> definitions = new ConcurrentHashMap<>();
    private final Map<UUID, MutantDefinition> activeMutants = new ConcurrentHashMap<>();
    private final Map<UUID, BossBar> bossBars = new ConcurrentHashMap<>();

    public MutantManager(@NotNull JavaPlugin plugin, @NotNull PluginLogger logger,
                         @NotNull ConfigurationManager configManager,
                         @NotNull LocalizationManager localizationManager,
                         @NotNull IntegrationManager integrationManager) {
        this.plugin = plugin;
        this.logger = logger;
        this.configManager = configManager;
        this.localizationManager = localizationManager;
        this.integrationManager = integrationManager;
        load();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void load() {
        definitions.clear();
        final var section = configManager.mutants().getSection("mutants");
        if (section == null) return;
        for (String id : section.getKeys(false)) {
            final String path = "mutants." + id;
            if (!configManager.mutants().getBoolean(path + ".enabled", true)) continue;
            final String baseName = configManager.mutants().getString(path + ".base-mob", "ZOMBIE");
            final EntityType base;
            try { base = EntityType.valueOf(baseName); }
            catch (IllegalArgumentException ex) { continue; }
            final String name = ChatColor.translateAlternateColorCodes('&',
                    configManager.mutants().getString(path + ".name", id));
            final double hp = configManager.mutants().getDouble(path + ".hp-multiplier", 3);
            final double dmg = configManager.mutants().getDouble(path + ".damage-multiplier", 2);
            final int armor = configManager.mutants().getInt(path + ".armor", 5);
            final double spd = configManager.mutants().getDouble(path + ".speed-multiplier", 1.3);
            final List<String> abilities = configManager.mutants().getStringList(path + ".abilities");
            final double weight = configManager.mutants().getDouble(path + ".weight", 10);
            final List<MutantDefinition.LootEntry> loot = new ArrayList<>();
            for (String entry : configManager.mutants().getStringList(path + ".loot")) {
                // loot entries are structured objects; read individually
            }
            final var lootSection = configManager.mutants().getSection(path + ".loot");
            if (lootSection != null) {
                for (String lk : lootSection.getKeys(false)) {
                    final String lp = path + ".loot." + lk;
                    final Material item = Material.matchMaterial(configManager.mutants().getString(lp + ".item", "AIR"));
                    if (item == null) continue;
                    loot.add(new MutantDefinition.LootEntry(item,
                            configManager.mutants().getInt(lp + ".amount", 1),
                            configManager.mutants().getDouble(lp + ".chance", 1)));
                }
            }
            definitions.put(id, new MutantDefinition(id, base, name, hp, dmg, armor, spd, abilities, weight, loot));
        }
        logger.info("Loaded %d mutant definitions", definitions.size());
    }

    public void reload() { load(); activeMutants.clear(); }

    public boolean isEnabled() { return configManager.mutants().getBoolean("enabled", true); }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (!isEnabled()) return;
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;
        if (!(e.getEntity() instanceof LivingEntity)) return;
        if (!MathUtils.chance(configManager.mutants().getDouble("spawn-chance", 0.02))) return;
        final MutantDefinition def = pick();
        if (def == null) return;
        transform(e.getEntity(), def);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        final UUID id = e.getEntity().getUniqueId();
        final MutantDefinition def = activeMutants.remove(id);
        final BossBar bar = bossBars.remove(id);
        if (bar != null) bar.removeAll();
        if (def == null) return;
        for (MutantDefinition.LootEntry entry : def.getLoot()) {
            if (MathUtils.chance(entry.getChance())) {
                e.getDrops().add(new ItemStack(entry.getItem(), entry.getAmount()));
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(localizationManager.get(p, "mutant-killed", "%mutant%", def.getName()));
        }
    }

    private void transform(@NotNull LivingEntity entity, @NotNull MutantDefinition def) {
        final net.kyori.adventure.text.Component name = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(def.getName());
        entity.setCustomName(net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(name));
        entity.setCustomNameVisible(true);
        if (configManager.mutants().getBoolean("glow", true)) entity.setGlowing(true);
        final double baseHp = entity.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
        entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(baseHp * def.getHpMultiplier());
        entity.setHealth(entity.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        entity.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(
                entity.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue() * def.getSpeedMultiplier());
        if (def.getArmor() > 0 && entity.getAttribute(Attribute.ARMOR) != null) {
            entity.getAttribute(Attribute.ARMOR).setBaseValue(def.getArmor());
        }
        activeMutants.put(entity.getUniqueId(), def);
        final net.kyori.adventure.text.Component parsed = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(def.getName());
        final String legacyName = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(parsed);
        final BossBar bar = Bukkit.createBossBar(legacyName,
                BarColor.valueOf(configManager.mutants().getString("bossbar-color", "RED")), BarStyle.SOLID);
        for (Player p : Bukkit.getOnlinePlayers()) {
            bar.addPlayer(p);
        }
        bossBars.put(entity.getUniqueId(), bar);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(localizationManager.get(p, "mutant-spawned", "%mutant%", def.getName()));
        }
    }

    private @org.jetbrains.annotations.Nullable MutantDefinition pick() {
        if (definitions.isEmpty()) return null;
        final double[] weights = new double[definitions.size()];
        int i = 0;
        for (MutantDefinition d : definitions.values()) weights[i++] = d.getWeight();
        final int idx = MathUtils.weightedPick(weights);
        if (idx < 0) return null;
        return new ArrayList<>(definitions.values()).get(idx);
    }

    public void shutdown() {
        HandlerList.unregisterAll(this);
        activeMutants.clear();
        for (BossBar bar : bossBars.values()) bar.removeAll();
        bossBars.clear();
    }

    public @NotNull List<MutantDefinition> getDefinitions() { return new ArrayList<>(definitions.values()); }
}
