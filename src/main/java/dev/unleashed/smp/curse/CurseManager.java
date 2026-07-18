package dev.unleashed.smp.curse;

import dev.unleashed.smp.logging.PluginLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CurseManager {
    private final Map<UUID, Integer> luckLevels = new ConcurrentHashMap<>();
    private final PluginLogger logger;

    public CurseManager(@NotNull PluginLogger logger) {
        this.logger = logger;
    }

    public int getLuck(@NotNull UUID playerId) {
        return luckLevels.getOrDefault(playerId, 0);
    }

    public void setLuck(@NotNull UUID playerId, int level) {
        luckLevels.put(playerId, Math.max(-20, Math.min(20, level)));
    }

    public void addLuck(@NotNull UUID playerId, int amount) {
        setLuck(playerId, getLuck(playerId) + amount);
    }

    public boolean isCursed(@NotNull UUID playerId) {
        return getLuck(playerId) < 0;
    }

    public double getCurseChance(@NotNull UUID playerId) {
        int luck = getLuck(playerId);
        if (luck >= 5) return 0.0;
        if (luck <= -5) return 1.0;
        return Math.max(0.0, Math.min(1.0, 0.5 - (luck * 0.1)));
    }

    public void remove(@NotNull UUID playerId) {
        luckLevels.remove(playerId);
    }

    public void clear() {
        luckLevels.clear();
    }
}
