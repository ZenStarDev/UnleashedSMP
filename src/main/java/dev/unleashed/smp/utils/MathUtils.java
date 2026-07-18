package dev.unleashed.smp.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Assorted math/collection helpers used across the plugin.
 */
public final class MathUtils {

    private static final Random RANDOM = new Random();

    private MathUtils() {
    }

    public static @NotNull Random random() {
        return RANDOM;
    }

    public static int randomInt(int min, int max) {
        if (max <= min) {
            return min;
        }
        return min + RANDOM.nextInt(max - min + 1);
    }

    public static double randomDouble(double min, double max) {
        return min + (max - min) * RANDOM.nextDouble();
    }

    public static boolean chance(double probability) {
        return probability > 0.0 && RANDOM.nextDouble() < probability;
    }

    /**
     * Picks a weighted index from the supplied weights.
     *
     * @param weights non-null, non-negative weights
     * @return the chosen index, or -1 if all weights are zero
     */
    public static int weightedPick(@NotNull double[] weights) {
        double total = 0.0;
        for (double w : weights) {
            total += Math.max(0.0, w);
        }
        if (total <= 0.0) {
            return -1;
        }
        double roll = RANDOM.nextDouble() * total;
        double cumulative = 0.0;
        for (int i = 0; i < weights.length; i++) {
            cumulative += Math.max(0.0, weights[i]);
            if (roll < cumulative) {
                return i;
            }
        }
        return weights.length - 1;
    }

    /**
     * Returns a random safe location near a player within a horizontal radius.
     *
     * @param player the player
     * @param radius the radius
     * @return a location at or above the surface
     */
    public static @NotNull Location randomNearby(@NotNull Player player, int radius) {
        final Location base = player.getLocation();
        final World world = base.getWorld();
        if (world == null) {
            return base.clone();
        }
        final double angle = RANDOM.nextDouble() * Math.PI * 2;
        final double dist = RANDOM.nextDouble() * radius;
        final int x = base.getBlockX() + (int) (Math.cos(angle) * dist);
        final int z = base.getBlockZ() + (int) (Math.sin(angle) * dist);
        final int y = world.getHighestBlockYAt(x, z) + 1;
        return new Location(world, x + 0.5, y, z + 0.5, base.getYaw(), base.getPitch());
    }

    public static @NotNull Location randomSpawnNear(@NotNull Player player, int radius) {
        return randomNearby(player, radius);
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
