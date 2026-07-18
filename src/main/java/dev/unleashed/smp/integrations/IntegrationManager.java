package dev.unleashed.smp.integrations;

import dev.unleashed.smp.UnleashedPlugin;
import dev.unleashed.smp.logging.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * Detects and tracks optional third-party plugins (soft depends).
 *
 * <p>Every integration is resolved lazily and stored as a {@link IntegrationState}. Nothing here
 * requires the integrated plugin to be present; absence is a graceful, supported state.</p>
 */
public final class IntegrationManager {

    public enum Hook {
        VAULT, PLAYERPOINTS, LUCKPERMS, PLACEHOLDERAPI, WORLDGUARD, WORLDEDIT,
        CITIZENS, MYTHICMOBS, GRIEFPREVENTION, LANDS, RESIDENCE, ITEMSADDER,
        ORAXEN, MMOITEMS, ELITEMOBS, MODELENGINE, MYPET, MMOCORE, AURELIUMSKILLS,
        ESSENTIALSX, VENTURECHAT, DISCORDSRV, COREPROTECT, GRIMAC, VULCAN, MATRIX
    }

    private final UnleashedPlugin plugin;
    private final PluginLogger logger;
    private final Map<Hook, IntegrationState> states = new EnumMap<>(Hook.class);

    public IntegrationManager(@NotNull UnleashedPlugin plugin, @NotNull PluginLogger logger) {
        this.plugin = plugin;
        this.logger = logger;
        for (Hook hook : Hook.values()) {
            states.put(hook, new IntegrationState(hook));
        }
    }

    /**
     * Runs detection for every hook against the running server's plugin list.
     */
    public void detect() {
        for (Hook hook : Hook.values()) {
            final IntegrationState state = states.get(hook);
            final String pluginName = hookPluginName(hook);
            final Plugin p = Bukkit.getPluginManager().getPlugin(pluginName);
            state.setPresent(p != null && p.isEnabled());
            if (state.isPresent()) {
                logger.integration("Detected integration: %s", hook);
            }
        }
    }

    private static @NotNull String hookPluginName(@NotNull Hook hook) {
        return switch (hook) {
            case VAULT -> "Vault";
            case PLAYERPOINTS -> "PlayerPoints";
            case LUCKPERMS -> "LuckPerms";
            case PLACEHOLDERAPI -> "PlaceholderAPI";
            case WORLDGUARD -> "WorldGuard";
            case WORLDEDIT -> "WorldEdit";
            case CITIZENS -> "Citizens";
            case MYTHICMOBS -> "MythicMobs";
            case GRIEFPREVENTION -> "GriefPrevention";
            case LANDS -> "Lands";
            case RESIDENCE -> "Residence";
            case ITEMSADDER -> "ItemsAdder";
            case ORAXEN -> "Oraxen";
            case MMOITEMS -> "MMOItems";
            case ELITEMOBS -> "EliteMobs";
            case MODELENGINE -> "ModelEngine";
            case MYPET -> "MyPet";
            case MMOCORE -> "MMOCore";
            case AURELIUMSKILLS -> "AureliumSkills";
            case ESSENTIALSX -> "Essentials";
            case VENTURECHAT -> "VentureChat";
            case DISCORDSRV -> "DiscordSRV";
            case COREPROTECT -> "CoreProtect";
            case GRIMAC -> "GrimAC";
            case VULCAN -> "Vulcan";
            case MATRIX -> "Matrix";
        };
    }

    public boolean isPresent(@NotNull Hook hook) {
        final IntegrationState state = states.get(hook);
        return state != null && state.isPresent();
    }

    public @Nullable Plugin getPlugin(@NotNull Hook hook) {
        final String name = hookPluginName(hook);
        return Bukkit.getPluginManager().getPlugin(name);
    }

    public @NotNull IntegrationState getState(@NotNull Hook hook) {
        return states.get(hook);
    }

    /**
     * @return true if Vault (economy/permission bridge) is available
     */
    public boolean isVaultPresent() { return isPresent(Hook.VAULT); }
    public boolean isPlaceholderApiPresent() { return isPresent(Hook.PLACEHOLDERAPI); }
    public boolean isLuckPermsPresent() { return isPresent(Hook.LUCKPERMS); }
    public boolean isPlayerPointsPresent() { return isPresent(Hook.PLAYERPOINTS); }
    public boolean isWorldGuardPresent() { return isPresent(Hook.WORLDGUARD); }
    public boolean isCitizensPresent() { return isPresent(Hook.CITIZENS); }
    public boolean isMythicMobsPresent() { return isPresent(Hook.MYTHICMOBS); }

    /**
     * Represents the resolved presence of a single integration hook.
     */
    public static final class IntegrationState {
        private final Hook hook;
        private volatile boolean present;

        IntegrationState(@NotNull Hook hook) {
            this.hook = hook;
        }

        public @NotNull Hook getHook() { return hook; }
        public boolean isPresent() { return present; }
        void setPresent(boolean present) { this.present = present; }
    }
}
