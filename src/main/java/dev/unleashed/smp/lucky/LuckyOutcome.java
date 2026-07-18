package dev.unleashed.smp.lucky;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A single lucky outcome definition (good/bad/neutral) with optional rewards and effects.
 */
public final class LuckyOutcome {
    public enum Type { GOOD, BAD, NEUTRAL }

    private final String id;
    private final Type type;
    private final double weight;
    private final String effect;
    private final Material rewardItem;
    private final int rewardAmount;
    private final double economy;
    private final String command;
    private final String sound;

    public LuckyOutcome(@NotNull String id, @NotNull Type type, double weight, @Nullable String effect,
                        @Nullable Material rewardItem, int rewardAmount, double economy, @Nullable String command,
                        @Nullable String sound) {
        this.id = id; this.type = type; this.weight = weight; this.effect = effect;
        this.rewardItem = rewardItem; this.rewardAmount = rewardAmount; this.economy = economy;
        this.command = command; this.sound = sound;
    }

    public @NotNull String getId() { return id; }
    public @NotNull Type getType() { return type; }
    public double getWeight() { return weight; }
    public @Nullable String getEffect() { return effect; }
    public @Nullable Material getRewardItem() { return rewardItem; }
    public int getRewardAmount() { return rewardAmount; }
    public double getEconomy() { return economy; }
    public @Nullable String getCommand() { return command; }
    public @Nullable String getSound() { return sound; }
}
