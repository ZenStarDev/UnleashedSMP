package dev.unleashed.smp.managers;

import dev.unleashed.smp.integrations.IntegrationManager;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeSet;

/**
 * Central permission resolution. Uses Bukkit permissions directly; LuckPerms is consulted through
 * the integration layer only when present. Predictable and dependency-free otherwise.
 */
public final class PermissionManager {

    private final IntegrationManager integrations;

    public PermissionManager(@NotNull org.bukkit.plugin.java.JavaPlugin plugin,
                             @NotNull IntegrationManager integrations) {
        this.integrations = integrations;
    }

    public boolean has(@NotNull org.bukkit.command.CommandSender sender, @NotNull String permission) {
        if (!(sender instanceof Player player)) {
            return sender.isOp() || sender.hasPermission(permission);
        }
        return player.hasPermission(permission);
    }

    public boolean hasBypass(@NotNull Player player, @NotNull String node) {
        return player.hasPermission("unleashed.bypass.*") || player.hasPermission(node);
    }

    public @NotNull Set<String> getPermissions(@NotNull Player player, @NotNull String prefix) {
        final Set<String> result = new TreeSet<>();
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            final String name = info.getPermission();
            if (info.getValue() && (name.equals(prefix) || name.startsWith(prefix + ".") || name.equals("*"))) {
                result.add(name);
            }
        }
        return result;
    }
}
