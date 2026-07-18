package dev.unleashed.smp.managers;

import dev.unleashed.smp.UnleashedPlugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Centralized scheduler abstraction that wraps the Bukkit scheduler.
 *
 * <p>All async work must go through {@link #runAsync(Runnable)} or {@link #supplyAsync(Supplier)}
 * to avoid blocking the main thread, while tasks that touch the Bukkit API must run on the main
 * thread via {@link #runSync(Runnable)}.</p>
 */
public final class SchedulerManager {

    private final JavaPlugin plugin;

    public SchedulerManager(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs a task on the main server thread.
     *
     * @param task the task to execute
     */
    public void runSync(@NotNull Runnable task) {
        if (plugin.getServer().isPrimaryThread()) {
            task.run();
        } else {
            plugin.getServer().getScheduler().runTask(plugin, task);
        }
    }

    /**
     * Runs a task on the main server thread later.
     *
     * @param task  the task to execute
     * @param delay delay in ticks
     */
    public void runSyncLater(@NotNull Runnable task, long delay) {
        plugin.getServer().getScheduler().runTaskLater(plugin, task, delay);
    }

    /**
     * Runs a repeating task on the main server thread.
     *
     * @param task   the task to execute
     * @param delay  initial delay in ticks
     * @param period period in ticks
     * @return the task id
     */
    public int runSyncTimer(@NotNull Runnable task, long delay, long period) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, task, delay, period).getTaskId();
    }

    /**
     * Runs a task asynchronously.
     *
     * @param task the task to execute
     */
    public void runAsync(@NotNull Runnable task) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    /**
     * Runs a supplier asynchronously and returns a future resolving on the async thread.
     *
     * @param supplier the supplier to execute
     * @param <T>      result type
     * @return a completable future
     */
    public <T> CompletableFuture<T> supplyAsync(@NotNull Supplier<T> supplier) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        runAsync(() -> {
            try {
                future.complete(supplier.get());
            } catch (RuntimeException ex) {
                future.completeExceptionally(ex);
            }
        });
        return future;
    }

    /**
     * Schedules an async task that later hops back to the main thread with its result.
     *
     * @param supplier the async supplier
     * @param then     the callback executed on the main thread
     * @param <T>      result type
     */
    public <T> void supplyAsyncThenSync(@NotNull Supplier<T> supplier, @NotNull java.util.function.Consumer<T> then) {
        supplyAsync(supplier).thenAccept(result -> runSync(() -> then.accept(result)));
    }

    /**
     * @return an executor that submits work to the async scheduler
     */
    public @NotNull Executor asyncExecutor() {
        return this::runAsync;
    }

    /**
     * Schedules an async task with a delay using the system scheduler.
     *
     * @param task  the task
     * @param delay delay value
     * @param unit  time unit
     */
    public void runAsyncLater(@NotNull Runnable task, long delay, @NotNull TimeUnit unit) {
        plugin.getServer().getAsyncScheduler().runDelayed(plugin, t -> task.run(), delay, unit);
    }

    public void cancelTask(int taskId) {
        plugin.getServer().getScheduler().cancelTask(taskId);
    }

    public void shutdown() {
        plugin.getServer().getScheduler().cancelTasks(plugin);
    }
}
