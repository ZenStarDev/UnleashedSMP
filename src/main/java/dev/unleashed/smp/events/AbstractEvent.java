package dev.unleashed.smp.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Set;

public abstract class AbstractEvent implements GameEvent {
    private final String id;
    protected AbstractEvent(@NotNull String id) { this.id = id; }
    @Override public @NotNull String getId() { return id; }
    protected @NotNull String cfgPath() { return "events." + id; }
    protected boolean cfgBool(@NotNull EventContext ctx, @NotNull String key, boolean def) {
        return ctx.config().events().getBoolean(cfgPath() + "." + key, def);
    }
    protected int cfgInt(@NotNull EventContext ctx, @NotNull String key, int def) {
        return ctx.config().events().getInt(cfgPath() + "." + key, def);
    }
    protected double cfgDouble(@NotNull EventContext ctx, @NotNull String key, double def) {
        return ctx.config().events().getDouble(cfgPath() + "." + key, def);
    }
    protected @Nullable String cfgString(@NotNull EventContext ctx, @NotNull String key, @Nullable String def) {
        return ctx.config().events().getString(cfgPath() + "." + key, def);
    }
    protected boolean isEnabled(@NotNull EventContext ctx) {
        return ctx.config().events().getBoolean(cfgPath() + ".enabled", isEnabledByDefault());
    }
    @Override public void onTick(@NotNull EventContext context, @NotNull Set<Player> players) { }
}
