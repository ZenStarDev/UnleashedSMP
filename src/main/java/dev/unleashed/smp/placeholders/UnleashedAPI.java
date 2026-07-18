package dev.unleashed.smp.placeholders;

import dev.unleashed.smp.UnleashedPlugin;
import dev.unleashed.smp.bootstrap.Bootstrap;
import dev.unleashed.smp.events.EventManager;
import dev.unleashed.smp.events.GameEvent;
import dev.unleashed.smp.lucky.LuckyManager;
import dev.unleashed.smp.mutant.MutantManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Public developer API for UnleashedSMP. Obtain an instance via {@link #get(JavaPlugin)}.
 */
public final class UnleashedAPI {

    private final Bootstrap bootstrap;

    private UnleashedAPI(@NotNull Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    /**
     * Resolves the API from a plugin instance. Returns null if UnleashedSMP is not loaded or the
     * caller lacks the unleashed.api permission.
     */
    public static @Nullable UnleashedAPI get(@NotNull JavaPlugin plugin) {
        if (!plugin.getServer().getPluginManager().isPluginEnabled("UnleashedSMP")) return null;
        final UnleashedPlugin unleashed = (UnleashedPlugin) plugin.getServer().getPluginManager().getPlugin("UnleashedSMP");
        if (unleashed == null || unleashed.getBootstrap() == null) return null;
        return new UnleashedAPI(unleashed.getBootstrap());
    }

    public @NotNull EventManager getEventManager() { return bootstrap.getEventManager(); }
    public @NotNull LuckyManager getLuckyManager() { return bootstrap.getLuckyManager(); }
    public @NotNull MutantManager getMutantManager() { return bootstrap.getMutantManager(); }

    public void registerEvent(@NotNull GameEvent event) { bootstrap.getEventManager().register(event); }
    public void unregisterEvent(@NotNull String id) { bootstrap.getEventManager().unregister(id); }
    public @Nullable GameEvent getEvent(@NotNull String id) { return bootstrap.getEventManager().getEvent(id); }
    public @NotNull Collection<GameEvent> getEvents() { return bootstrap.getEventManager().getEvents(); }

    public boolean startEvent(@NotNull String id) { return bootstrap.getEventManager().start(id) != null; }
    public boolean startEventFor(@NotNull String id, @NotNull Collection<? extends Player> players) {
        return bootstrap.getEventManager().start(id, players) != null;
    }
    public void stopEvent(@NotNull String id) { bootstrap.getEventManager().stop(id); }
    public void pauseEvent(@NotNull String id) { bootstrap.getEventManager().pause(id); }
    public void resumeEvent(@NotNull String id) { bootstrap.getEventManager().resume(id); }

    public boolean rollLucky(@NotNull Player player) { return bootstrap.getLuckyManager().roll(player); }
}
