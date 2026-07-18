package dev.unleashed.smp.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Set;

public final class EventBuilder {
    private String id = "custom";
    private String description = "Custom event";
    private int weight = 10, duration = 600, cooldown = 600;
    private Runnable onStart = () -> {}, onTick = () -> {}, onStop = () -> {};

    public @NotNull EventBuilder id(@NotNull String id) { this.id = id; return this; }
    public @NotNull EventBuilder description(@NotNull String d) { this.description = d; return this; }
    public @NotNull EventBuilder weight(int w) { this.weight = w; return this; }
    public @NotNull EventBuilder duration(int d) { this.duration = d; return this; }
    public @NotNull EventBuilder cooldown(int c) { this.cooldown = c; return this; }
    public @NotNull EventBuilder onStart(@NotNull Runnable r) { this.onStart = r; return this; }
    public @NotNull EventBuilder onTick(@NotNull Runnable r) { this.onTick = r; return this; }
    public @NotNull EventBuilder onStop(@NotNull Runnable r) { this.onStop = r; return this; }

    public @NotNull GameEvent build() {
        final String id = this.id, description = this.description;
        final int weight = this.weight, duration = this.duration, cooldown = this.cooldown;
        final Runnable s = onStart, t = onTick, e = onStop;
        return new GameEvent() {
            @Override public @NotNull String getId() { return id; }
            @Override public @NotNull String getDescription() { return description; }
            @Override public int getWeight() { return weight; }
            @Override public int getDuration() { return duration; }
            @Override public int getCooldown() { return cooldown; }
            @Override public void onStart(@NotNull EventContext c, @NotNull Set<Player> p) { s.run(); }
            @Override public void onTick(@NotNull EventContext c, @NotNull Set<Player> p) { t.run(); }
            @Override public void onStop(@NotNull EventContext c, @NotNull Set<Player> p) { e.run(); }
        };
    }
}
