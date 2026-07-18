package dev.unleashed.smp.events;

import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.curse.CurseManager;
import dev.unleashed.smp.database.DatabaseManager;
import dev.unleashed.smp.integrations.IntegrationManager;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.logging.PluginLogger;
import dev.unleashed.smp.managers.CacheManager;
import dev.unleashed.smp.managers.SchedulerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class EventManager implements Listener {
    private final org.bukkit.plugin.java.JavaPlugin plugin;
    private final PluginLogger logger;
    private final ConfigurationManager configManager;
    private final LocalizationManager localizationManager;
    private final SchedulerManager schedulerManager;
    private final DatabaseManager databaseManager;
    private final IntegrationManager integrationManager;
    private final CacheManager cacheManager;

    private final Map<String, GameEvent> registry = new LinkedHashMap<>();
    private final Map<String, RunningEvent> running = new ConcurrentHashMap<>();
    private final Map<String, PendingEvent> pending = new ConcurrentHashMap<>();
    private final Map<String, Long> cooldowns = new ConcurrentHashMap<>();
    private final EventScheduler eventScheduler;
    private final List<String> recentlyEnded = new ArrayList<>();
    private volatile long globalCooldownUntil = 0;
    private final CurseManager curseManager;

    public EventManager(@NotNull org.bukkit.plugin.java.JavaPlugin plugin, @NotNull PluginLogger logger,
                        @NotNull ConfigurationManager configManager,
                        @NotNull LocalizationManager localizationManager,
                        @NotNull SchedulerManager schedulerManager,
                        @NotNull DatabaseManager databaseManager,
                        @NotNull IntegrationManager integrationManager,
                        @NotNull CacheManager cacheManager,
                        @NotNull CurseManager curseManager) {
        this.plugin = plugin; this.logger = logger; this.configManager = configManager;
        this.localizationManager = localizationManager; this.schedulerManager = schedulerManager;
        this.databaseManager = databaseManager; this.integrationManager = integrationManager;
        this.cacheManager = cacheManager;
        this.curseManager = curseManager;
        this.eventScheduler = new EventScheduler(this, localizationManager, configManager, logger);
    }

    public void registerDefaults() {
        register(new TntRainEvent());
        register(new MeteorShowerEvent());
        register(new MobRainEvent());
        register(new CatRainEvent());
        register(new ChickenRainEvent());
        register(new CreeperPartyEvent());
        register(new LuckyDropEvent());
        register(new DiamondRainEvent());
        register(new XpStormEvent());
        register(new RandomTeleportEvent());
        register(new GravityEvent());
        register(new SpeedEvent());
        register(new FreezeEvent());
        register(new BlindnessEvent());
        register(new NightEvent());
        register(new BloodMoonEvent());
        register(new BossSpawnEvent());
        register(new ZombieHordeEvent());
        register(new SkeletonArmyEvent());
        register(new RandomPotionEvent());
        register(new ExplosionZoneEvent());
        register(new MobBuffEvent());
        register(new PlayerBuffEvent());
        register(new HealingRainEvent());
        register(new DoubleDropsEvent());
        register(new TreeExplosionEvent());
        register(new EarthquakeEvent());
        register(new LightningStormEvent());
        register(new TreasureHuntEvent());
        register(new DropPartyEvent());
        register(new PvpFrenzyEvent());
        logger.info("Registered %d events", registry.size());
    }

    public void register(@NotNull GameEvent event) { registry.put(event.getId().toLowerCase(), event); }
    public void unregister(@NotNull String id) { registry.remove(id.toLowerCase()); }
    public @Nullable GameEvent getEvent(@NotNull String id) { return registry.get(id.toLowerCase()); }
    public @NotNull Collection<GameEvent> getEvents() { return new ArrayList<>(registry.values()); }
    public @Nullable RunningEvent getRunning(@NotNull String id) { return running.get(id.toLowerCase()); }
    public @NotNull Collection<RunningEvent> getRunningEvents() { return new ArrayList<>(running.values()); }
    public int getActiveEventCount() { return running.size() + pending.size(); }
    public boolean isRunning(@NotNull String id) { return running.containsKey(id.toLowerCase()); }
    public @NotNull CurseManager getCurseManager() { return curseManager; }

    public boolean isEnabled(@NotNull GameEvent event) {
        if (!configManager.events().getBoolean("events." + event.getId() + ".enabled", true)) return false;
        final String perm = event.getRequiredPermission();
        if (perm != null && !Bukkit.getOnlinePlayers().isEmpty()) {
            return Bukkit.getOnlinePlayers().stream().anyMatch(p -> p.hasPermission(perm));
        }
        return true;
    }

    EventContext buildContext() {
        return new EventContext(plugin, logger, configManager, localizationManager,
                schedulerManager, databaseManager, integrationManager, cacheManager);
    }

    SchedulerManager getSchedulerManager() { return schedulerManager; }

    public @Nullable RunningEvent start(@NotNull String id) {
        final GameEvent event = getEvent(id);
        return event == null ? null : start(event, Bukkit.getOnlinePlayers());
    }

    public @Nullable RunningEvent start(@NotNull String id, @NotNull Collection<? extends Player> players) {
        final GameEvent event = getEvent(id);
        return event == null ? null : start(event, players);
    }


    public @Nullable RunningEvent start(@NotNull GameEvent event, @NotNull Collection<? extends Player> players) {
        final String key = event.getId().toLowerCase();
        if (running.containsKey(key) || pending.containsKey(key) || !isEnabled(event)) return null;
        final long now = System.currentTimeMillis();
        final Long cd = cooldowns.get(key);
        if (cd != null && now < cd) return null;
        if (now < globalCooldownUntil) return null;

        final int countdownTicks = getCountdownTicks(event);
        if (countdownTicks > 0 && !hasBypassCountdown(players)) {
            return startCountdown(event, players, countdownTicks);
        }
        return doStart(event, players);
    }

    private @Nullable RunningEvent doStart(@NotNull GameEvent event, @NotNull Collection<? extends Player> players) {
        final String key = event.getId().toLowerCase();
        final EventContext ctx = buildContext();
        final Set<Player> playerSet = new java.util.HashSet<>(players);
        final RunningEvent runningEvent = new RunningEvent(event, event.getDuration());
        for (Player p : playerSet) runningEvent.addPlayer(p);
        running.put(key, runningEvent);

        try { event.onStart(ctx, playerSet); }
        catch (RuntimeException ex) { logger.error("Error starting event " + key, ex); }

        eventScheduler.schedule(runningEvent, event);
        final var dao = databaseManager.getEventLogDao();
        if (dao != null) dao.logStart(event.getId(), System.currentTimeMillis(), worldName(playerSet));
        ((dev.unleashed.smp.UnleashedPlugin) plugin).getBootstrap().getMetricsManager().recordEventStart();

        final boolean broadcast = configManager.events().getBoolean("events." + event.getId() + ".broadcast", true)
                && configManager.config().getBoolean("events.announce", true);
        if (broadcast) Bukkit.broadcast(localizationManager.get("event-started", "%event%", event.getId()));
        return runningEvent;
    }

    private @Nullable RunningEvent startCountdown(@NotNull GameEvent event, @NotNull Collection<? extends Player> players, int countdownTicks) {
        final String key = event.getId().toLowerCase();
        final Set<Player> playerSet = new java.util.HashSet<>(players);
        final PendingEvent pendingEvent = new PendingEvent(event, playerSet, countdownTicks);
        pending.put(key, pendingEvent);

        final SchedulerManager scheduler = EventManagerAccess.scheduler(this);
        final EventContext ctx = EventManagerAccess.context(this);
        final int id = scheduler.runSyncTimer(() -> {
            int rem = pendingEvent.getRemainingTicks() - 20;
            pendingEvent.setRemainingTicks(Math.max(0, rem));

            if (rem <= 0) {
                pending.remove(key);
                if (!isRunning(key)) doStart(event, pendingEvent.getTargetPlayers());
                return;
            }

            final int interval = configManager.config().getInt("events.countdown.broadcast-interval", 100);
            if (rem > 0 && rem % interval == 0) {
                final boolean broadcast = configManager.events().getBoolean("events." + event.getId() + ".broadcast", true)
                        && configManager.config().getBoolean("events.announce", true);
                if (broadcast) {
                    final String timeStr = formatTime(rem);
                    Bukkit.broadcast(localizationManager.get("event-countdown-broadcast", "%event%", event.getId(), "%time%", timeStr));
                }
            }
        }, 20L, 20L);
        pendingEvent.setTaskId(id);

        final boolean broadcast = configManager.events().getBoolean("events." + event.getId() + ".broadcast", true)
                && configManager.config().getBoolean("events.announce", true);
        if (broadcast) {
            final String timeStr = formatTime(countdownTicks);
            Bukkit.broadcast(localizationManager.get("event-countdown-start", "%event%", event.getId(), "%time%", timeStr));
        }
        return null;
    }

    private boolean hasBypassCountdown(@NotNull Collection<? extends Player> players) {
        final String perm = configManager.config().getString("events.countdown.bypass-permission", "unleashed.bypass.countdown");
        for (Player p : players) {
            if (p != null && p.hasPermission(perm)) return true;
        }
        return false;
    }

    private int getCountdownTicks(@NotNull GameEvent event) {
        if (!configManager.config().getBoolean("events.countdown.enabled", true)) return 0;
        final int perEvent = configManager.events().getInt("events." + event.getId() + ".countdown", -1);
        if (perEvent >= 0) return perEvent;
        return configManager.config().getInt("events.countdown.default-ticks", 6000);
    }

    private static @NotNull String formatTime(int ticks) {
        final int totalSeconds = ticks / 20;
        final int minutes = totalSeconds / 60;
        final int seconds = totalSeconds % 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    public @NotNull Collection<PendingEvent> getPendingEvents() {
        return new ArrayList<>(pending.values());
    }

    public boolean hasPending(@NotNull String id) {
        return pending.containsKey(id.toLowerCase());
    }

    public void cancelCountdown(@NotNull String id) {
        final String key = id.toLowerCase();
        final PendingEvent pe = pending.remove(key);
        if (pe != null && pe.getTaskId() != -1) schedulerManager.cancelTask(pe.getTaskId());
    }

    public void stop(@NotNull String id) {
        final String key = id.toLowerCase();
        final RunningEvent runningEvent = running.remove(key);
        if (runningEvent == null) return;
        if (runningEvent.getTaskId() != -1) schedulerManager.cancelTask(runningEvent.getTaskId());
        try { runningEvent.getEvent().onStop(buildContext(), runningEvent.resolvePlayers()); }
        catch (RuntimeException ex) { logger.error("Error stopping event " + key, ex); }
        final long now = System.currentTimeMillis();
        cooldowns.put(key, now + runningEvent.getEvent().getCooldown());
        globalCooldownUntil = now + configManager.config().getLong("events.global-cooldown", 200) * 50L;
        recentlyEnded.add(key);
        if (recentlyEnded.size() > 16) recentlyEnded.remove(0);
        final var dao = databaseManager.getEventLogDao();
        if (dao != null) dao.logEnd(runningEvent.getEvent().getId(), now);
    }

    public void pause(@NotNull String id) {
        final RunningEvent re = running.get(id.toLowerCase());
        if (re != null && re.getTaskId() != -1) { schedulerManager.cancelTask(re.getTaskId()); re.setTaskId(-1); }
    }

    public void resume(@NotNull String id) {
        final RunningEvent re = running.get(id.toLowerCase());
        if (re != null) eventScheduler.schedule(re, re.getEvent());
    }

    private static @NotNull String worldName(@NotNull Set<Player> players) {
        for (Player p : players) if (p.getWorld() != null) return p.getWorld().getName();
        return "unknown";
    }

    public void startScheduler() { eventScheduler.stopAutoRoll(); eventScheduler.startAutoRoll(); }
    public void reload() {
        eventScheduler.stopAutoRoll();
        for (PendingEvent e : new ArrayList<>(pending.values())) cancelCountdown(e.getEvent().getId());
        for (RunningEvent e : new ArrayList<>(running.values())) stop(e.getId());
        running.clear(); pending.clear(); cooldowns.clear(); recentlyEnded.clear();
        eventScheduler.startAutoRoll();
    }
    public void shutdown() {
        for (PendingEvent e : new ArrayList<>(pending.values())) cancelCountdown(e.getEvent().getId());
        for (RunningEvent e : new ArrayList<>(running.values())) {
            try { stop(e.getId()); } catch (RuntimeException ex) { logger.error("Shutdown stop error " + e.getId(), ex); }
        }
    }
    public static final class PendingEvent {
        private final GameEvent event;
        private final Set<Player> targetPlayers;
        private volatile int remainingTicks;
        private volatile int taskId = -1;

        public PendingEvent(@NotNull GameEvent event, @NotNull Set<Player> targetPlayers, int countdownTicks) {
            this.event = event;
            this.targetPlayers = targetPlayers;
            this.remainingTicks = countdownTicks;
        }
        public @NotNull GameEvent getEvent() { return event; }
        public @NotNull Set<Player> getTargetPlayers() { return targetPlayers; }
        public int getRemainingTicks() { return remainingTicks; }
        public void setRemainingTicks(int ticks) { this.remainingTicks = ticks; }
        public int getTaskId() { return taskId; }
        public void setTaskId(int taskId) { this.taskId = taskId; }
    }

    public @NotNull Map<String, GameEvent> getRegistry() { return registry; }
}
