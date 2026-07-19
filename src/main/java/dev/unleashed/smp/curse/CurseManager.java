package dev.unleashed.smp.curse;

import dev.unleashed.smp.database.DatabaseManager;
import dev.unleashed.smp.database.PlayerDataDao;
import dev.unleashed.smp.logging.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CurseManager implements Listener {
    private final Map<UUID, Integer> luckLevels = new ConcurrentHashMap<>();
    private final PluginLogger logger;
    private final DatabaseManager databaseManager;
    private final PlayerDataDao playerDataDao;

    public CurseManager(@NotNull PluginLogger logger, @NotNull DatabaseManager databaseManager) {
        this.logger = logger;
        this.databaseManager = databaseManager;
        this.playerDataDao = databaseManager.getPlayerDataDao();
        Bukkit.getPluginManager().registerEvents(this, ((dev.unleashed.smp.UnleashedPlugin) Bukkit.getPluginManager().getPlugin("UnleashedSMP")));
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

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        if (playerDataDao == null || !databaseManager.isAvailable()) return;
        playerDataDao.loadOrCreate(e.getPlayer().getUniqueId(), e.getPlayer().getName()).thenAccept(data -> {
            luckLevels.put(e.getPlayer().getUniqueId(), data.getLuckLevel());
        });
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        final int luck = getLuck(e.getPlayer().getUniqueId());
        if (playerDataDao == null || !databaseManager.isAvailable()) return;
        playerDataDao.loadOrCreate(e.getPlayer().getUniqueId(), e.getPlayer().getName()).thenAccept(data -> {
            data.setLuckLevel(luck);
            playerDataDao.save(data);
        });
        luckLevels.remove(e.getPlayer().getUniqueId());
    }
}
