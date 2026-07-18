package dev.unleashed.smp.managers;

import dev.unleashed.smp.UnleashedPlugin;
import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.logging.PluginLogger;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Time-based expiring cache used to avoid repeated expensive lookups. Thread-safe.
 */
public final class CacheManager {

    private final UnleashedPlugin plugin;
    private final PluginLogger logger;
    private final ConfigurationManager configManager;
    private final Map<String, Entry<?>> cache = new ConcurrentHashMap<>();
    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();
    private volatile long ttlTicks;

    public CacheManager(@NotNull UnleashedPlugin plugin, @NotNull PluginLogger logger,
                        @NotNull ConfigurationManager configManager) {
        this.plugin = plugin;
        this.logger = logger;
        this.configManager = configManager;
        this.ttlTicks = configManager.performance().getLong("cache-ttl", 1200);
    }

    public void reload() {
        this.ttlTicks = configManager.performance().getLong("cache-ttl", 1200);
        cache.clear();
    }

    public @Nullable <T> T get(@NotNull String key, @NotNull Class<T> type) {
        final Entry<?> entry = cache.get(key);
        if (entry == null) {
            misses.incrementAndGet();
            return null;
        }
        if (entry.isExpired(Bukkit.getCurrentTick())) {
            cache.remove(key);
            misses.incrementAndGet();
            return null;
        }
        hits.incrementAndGet();
        return type.cast(entry.value);
    }

    public void put(@NotNull String key, @NotNull Object value) {
        cache.put(key, new Entry<>(value, Bukkit.getCurrentTick() + ttlTicks));
    }

    public void invalidate(@NotNull String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    public double hitRatio() {
        final long total = hits.get() + misses.get();
        return total == 0 ? 0.0 : (double) hits.get() / total;
    }

    public int size() {
        return cache.size();
    }

    private record Entry<T>(@NotNull T value, long expireTick) {
        boolean isExpired(long currentTick) {
            return currentTick >= expireTick;
        }
    }
}
