package dev.unleashed.smp.events;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Set;

public interface GameEvent {
    @NotNull String getId();
    @NotNull String getDescription();
    void onStart(@NotNull EventContext context, @NotNull Set<Player> players);
    void onTick(@NotNull EventContext context, @NotNull Set<Player> players);
    void onStop(@NotNull EventContext context, @NotNull Set<Player> players);
    default int getWeight() { return 10; }
    default int getDuration() { return 600; }
    default int getCooldown() { return 600; }
    default @Nullable String getRequiredPermission() { return null; }
    default boolean isEnabledByDefault() { return true; }
}
