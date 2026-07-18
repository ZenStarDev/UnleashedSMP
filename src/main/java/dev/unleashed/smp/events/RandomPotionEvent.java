package dev.unleashed.smp.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Set;

public final class RandomPotionEvent extends AbstractEvent {
    private int taskId = -1;
    public RandomPotionEvent() { super("random_potion"); }
    @Override public @NotNull String getDescription() { return "Random potion effects applied."; }
    @Override public void onStart(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        final List<String> effects = ctx.config().events().getStringList("events.random_potion.settings.effects");
        taskId = ctx.scheduler().runSyncTimer(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (effects.isEmpty()) continue;
                final String name = effects.get(dev.unleashed.smp.utils.MathUtils.randomInt(0, effects.size() - 1));
                try {
                    final PotionEffectType type = PotionEffectType.getByName(name);
                    if (type != null) p.addPotionEffect(new PotionEffect(type, 200, 1, true, false));
                } catch (RuntimeException ignored) { }
            }
        }, 20L, 100L);
    }
    @Override public void onStop(@NotNull EventContext ctx, @NotNull Set<Player> players) {
        if (taskId != -1) {
            ctx.scheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
}
