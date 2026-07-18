package dev.unleashed.smp.events;

import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.logging.PluginLogger;
import dev.unleashed.smp.managers.SchedulerManager;
import dev.unleashed.smp.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class EventScheduler {
    private final EventManager manager;
    private final LocalizationManager localizationManager;
    private final ConfigurationManager configManager;
    private final PluginLogger logger;
    private volatile int autoRollTask = -1;

    EventScheduler(@NotNull EventManager manager, @NotNull LocalizationManager localizationManager,
                   @NotNull ConfigurationManager configManager, @NotNull PluginLogger logger) {
        this.manager = manager; this.localizationManager = localizationManager;
        this.configManager = configManager; this.logger = logger;
    }

    void schedule(@NotNull RunningEvent runningEvent, @NotNull GameEvent event) {
        final int duration = runningEvent.getRemainingTicks();
        final SchedulerManager scheduler = EventManagerAccess.scheduler(manager);
        final EventContext ctx = EventManagerAccess.context(manager);
        final int id = scheduler.runSyncTimer(() -> {
            if (!manager.isRunning(runningEvent.getId())) { runningEvent.setTaskId(-1); return; }
            final Set<Player> players = runningEvent.resolvePlayers();
            try { event.onTick(ctx, players); }
            catch (RuntimeException ex) { logger.error("Error ticking event " + runningEvent.getId(), ex); }
            if (duration > 0) {
                int rem = runningEvent.getRemainingTicks() - 20;
                runningEvent.setRemainingTicks(Math.max(0, rem));
                if (rem <= 0) manager.stop(runningEvent.getId());
            }
        }, 20L, 20L);
        runningEvent.setTaskId(id);
    }

    void startAutoRoll() {
        if (!configManager.config().getBoolean("events.enabled", true)) return;
        final long interval = configManager.scheduler().getLong("event-roll-interval",
                configManager.config().getLong("events.auto-roll-interval", 1200));
        final double chance = configManager.config().getDouble("events.auto-roll-chance", 0.35);
        final int maxConcurrent = configManager.config().getInt("events.max-concurrent-events", 3);
        autoRollTask = EventManagerAccess.scheduler(manager).runSyncTimer(() -> {
            if (!configManager.config().getBoolean("events.enabled", true)) return;
            if (manager.getRunningEvents().size() >= maxConcurrent) return;
            final double rollChance = getLuckModifiedChance(chance);
            if (!MathUtils.chance(rollChance)) return;
            final GameEvent pick = pickEvent();
            if (pick == null) return;
            final java.util.Set<Player> targets = pickCursedTargets();
            if (targets.isEmpty()) return;
            manager.start(pick, targets);
        }, interval, interval);
    }

    private double getLuckModifiedChance(double baseChance) {
        final double luckBonus = configManager.config().getDouble("events.curse.luck-bonus-chance", 0.15);
        final java.util.Set<Player> cursed = new java.util.LinkedHashSet<>();
        for (org.bukkit.entity.Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (manager.getCurseManager().isCursed(p.getUniqueId())) cursed.add(p);
        }
        if (cursed.isEmpty()) return baseChance;
        return Math.min(1.0, baseChance + luckBonus);
    }

    private @org.jetbrains.annotations.NotNull java.util.Set<Player> pickCursedTargets() {
        final java.util.Set<Player> cursed = new java.util.LinkedHashSet<>();
        for (org.bukkit.entity.Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (manager.getCurseManager().isCursed(p.getUniqueId())) cursed.add(p);
        }
        return cursed.isEmpty() ? new java.util.LinkedHashSet<>(org.bukkit.Bukkit.getOnlinePlayers()) : cursed;
    }

    private @org.jetbrains.annotations.Nullable GameEvent pickEvent() {
        final List<GameEvent> pool = new ArrayList<>();
        final double[] weights = new double[manager.getEvents().size()];
        int i = 0;
        for (GameEvent e : manager.getEvents()) {
            if (!manager.isEnabled(e) || manager.isRunning(e.getId())) { weights[i] = 0; i++; continue; }
            final int w = configManager.events().getInt("events." + e.getId() + ".weight", e.getWeight());
            weights[i] = Math.max(0, w); pool.add(e); i++;
        }
        if (pool.isEmpty()) return null;
        final int idx = MathUtils.weightedPick(weights);
        return idx >= 0 ? pool.get(idx) : null;
    }

    void stopAutoRoll() {
        if (autoRollTask != -1) { EventManagerAccess.scheduler(manager).cancelTask(autoRollTask); autoRollTask = -1; }
    }
}
