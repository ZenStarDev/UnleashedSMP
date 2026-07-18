package dev.unleashed.smp.mutant;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Static configuration for a mutant mob type loaded from mutants.yml.
 */
public final class MutantDefinition {
    private final String id;
    private final EntityType baseMob;
    private final String name;
    private final double hpMultiplier;
    private final double damageMultiplier;
    private final int armor;
    private final double speedMultiplier;
    private final List<String> abilities;
    private final double weight;
    private final List<LootEntry> loot;

    public MutantDefinition(@NotNull String id, @NotNull EntityType baseMob, @NotNull String name,
                            double hpMultiplier, double damageMultiplier, int armor, double speedMultiplier,
                            @NotNull List<String> abilities, double weight, @NotNull List<LootEntry> loot) {
        this.id = id; this.baseMob = baseMob; this.name = name; this.hpMultiplier = hpMultiplier;
        this.damageMultiplier = damageMultiplier; this.armor = armor; this.speedMultiplier = speedMultiplier;
        this.abilities = abilities; this.weight = weight; this.loot = loot;
    }

    public @NotNull String getId() { return id; }
    public @NotNull EntityType getBaseMob() { return baseMob; }
    public @NotNull String getName() { return name; }
    public double getHpMultiplier() { return hpMultiplier; }
    public double getDamageMultiplier() { return damageMultiplier; }
    public int getArmor() { return armor; }
    public double getSpeedMultiplier() { return speedMultiplier; }
    public @NotNull List<String> getAbilities() { return abilities; }
    public double getWeight() { return weight; }
    public @NotNull List<LootEntry> getLoot() { return loot; }

    public static final class LootEntry {
        private final Material item;
        private final int amount;
        private final double chance;
        public LootEntry(@NotNull Material item, int amount, double chance) {
            this.item = item; this.amount = amount; this.chance = chance;
        }
        public @NotNull Material getItem() { return item; }
        public int getAmount() { return amount; }
        public double getChance() { return chance; }
    }
}
