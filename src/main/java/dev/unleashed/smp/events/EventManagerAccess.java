package dev.unleashed.smp.events;

import dev.unleashed.smp.managers.SchedulerManager;
import org.jetbrains.annotations.NotNull;

final class EventManagerAccess {
    private EventManagerAccess() { }
    static @NotNull SchedulerManager scheduler(@NotNull EventManager manager) { return manager.getSchedulerManager(); }
    static @NotNull EventContext context(@NotNull EventManager manager) { return manager.buildContext(); }
}
