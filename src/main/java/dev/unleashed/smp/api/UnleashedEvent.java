package dev.unleashed.smp.api;

import dev.unleashed.smp.events.AbstractEvent;
import dev.unleashed.smp.events.EventContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Convenience base class for developers creating custom events via the API.
 */
public abstract class UnleashedEvent extends AbstractEvent {
    protected UnleashedEvent(@NotNull String id) {
        super(id);
    }

    @Override
    public abstract void onStart(@NotNull EventContext context, @NotNull Set<Player> players);

    @Override
    public abstract void onStop(@NotNull EventContext context, @NotNull Set<Player> players);
}
