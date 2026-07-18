package dev.unleashed.smp.lucky;

import dev.unleashed.smp.UnleashedPlugin;
import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.curse.CurseManager;
import dev.unleashed.smp.integrations.IntegrationManager;
import dev.unleashed.smp.localization.LocalizationManager;
import dev.unleashed.smp.logging.PluginLogger;
import dev.unleashed.smp.managers.CacheManager;
import dev.unleashed.smp.managers.SchedulerManager;
import dev.unleashed.smp.utils.MathUtils;
import dev.unleashed.smp.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the /lucky command: weighted outcome selection, cooldowns, rewards, and punishments.
 */
public final class LuckyManager {

    private final JavaPlugin plugin;
    private final PluginLogger logger;
    private final ConfigurationManager configManager;
    private final LocalizationManager localizationManager;
    private final SchedulerManager schedulerManager;
    private final IntegrationManager integrationManager;
    private final CurseManager curseManager;
    private final List<LuckyOutcome> outcomes = new ArrayList<>();
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();

    public LuckyManager(@NotNull JavaPlugin plugin, @NotNull PluginLogger logger,
                        @NotNull ConfigurationManager configManager,
                        @NotNull LocalizationManager localizationManager,
                        @NotNull SchedulerManager schedulerManager,
                        @NotNull IntegrationManager integrationManager,
                        @NotNull CurseManager curseManager) {
        this.plugin = plugin;
        this.logger = logger;
        this.configManager = configManager;
        this.localizationManager = localizationManager;
        this.schedulerManager = schedulerManager;
        this.integrationManager = integrationManager;
        this.curseManager = curseManager;
        load();
    }

    private void load() {
        outcomes.clear();
        final var section = configManager.lucky().getSection("outcomes");
        if (section == null) return;
        for (String id : section.getKeys(false)) {
            final String typeName = configManager.lucky().getString("outcomes." + id + ".type", "NEUTRAL");
            final LuckyOutcome.Type type;
            try { type = LuckyOutcome.Type.valueOf(typeName.toUpperCase()); }
            catch (IllegalArgumentException ex) { continue; }
            final double weight = configManager.lucky().getDouble("outcomes." + id + ".weight", 1);
            final String effect = configManager.lucky().getString("outcomes." + id + ".effect", "");
            final String itemName = configManager.lucky().getString("outcomes." + id + ".reward.item", null);
            final Material item = itemName == null ? null : Material.matchMaterial(itemName);
            final int amount = configManager.lucky().getInt("outcomes." + id + ".reward.amount", 1);
            final double economy = configManager.lucky().getDouble("outcomes." + id + ".economy", 0);
            final String command = configManager.lucky().getString("outcomes." + id + ".command", null);
            final String sound = configManager.lucky().getString("outcomes." + id + ".sound", null);
            outcomes.add(new LuckyOutcome(id, type, weight, effect, item, amount, economy, command, sound));
        }
        logger.info("Loaded %d lucky outcomes", outcomes.size());
    }

    public void reload() { load(); cooldowns.clear(); }

    public boolean isEnabled() {
        return configManager.lucky().getBoolean("enabled", true);
    }

    private int cooldownTicks() {
        return configManager.lucky().getInt("cooldown", 600);
    }

    public boolean onCooldown(@NotNull Player player) {
        final Long until = cooldowns.get(player.getUniqueId());
        if (until == null) return false;
        final long now = System.currentTimeMillis();
        if (now >= until) { cooldowns.remove(player.getUniqueId()); return false; }
        return true;
    }

    public long cooldownRemaining(@NotNull Player player) {
        final Long until = cooldowns.get(player.getUniqueId());
        if (until == null) return 0;
        return Math.max(0, (until - System.currentTimeMillis()) / 1000);
    }

    /**
     * Rolls the lucky system for a player. Returns true if an outcome was applied.
     */
    public boolean roll(@NotNull Player player) {
        if (!isEnabled()) return false;
        if (player.hasPermission("unleashed.bypass.cooldown")) {
            // allowed
        } else if (onCooldown(player)) {
            player.sendMessage(localizationManager.get(player, "lucky-cooldown", "%time%", String.valueOf(cooldownRemaining(player))));
            return false;
        }
        if (outcomes.isEmpty()) {
            player.sendMessage(localizationManager.get(player, "lucky-none"));
            return false;
        }
        final double[] weights = new double[outcomes.size()];
        for (int i = 0; i < outcomes.size(); i++) weights[i] = outcomes.get(i).getWeight();
        final int idx = MathUtils.weightedPick(weights);
        if (idx < 0) return false;
        final LuckyOutcome outcome = outcomes.get(idx);
        apply(player, outcome);
        if (!player.hasPermission("unleashed.bypass.cooldown")) {
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + cooldownTicks() * 50L);
        }
        ((dev.unleashed.smp.UnleashedPlugin) plugin).getBootstrap().getMetricsManager().recordLuckyRoll();
        return true;
    }

    private void apply(@NotNull Player player, @NotNull LuckyOutcome outcome) {
        if (outcome.getEffect() != null && !outcome.getEffect().isEmpty()) {
            final String key = switch (outcome.getType()) {
                case GOOD -> "lucky-good";
                case BAD -> "lucky-bad";
                default -> "lucky-neutral";
            };
            player.sendMessage(localizationManager.get(player, key, "%effect%", outcome.getEffect()));
        }
        if (outcome.getRewardItem() != null && outcome.getRewardAmount() > 0) {
            player.getInventory().addItem(new ItemStack(outcome.getRewardItem(), outcome.getRewardAmount()));
        }
        if (outcome.getEconomy() != 0) {
            applyEconomy(player, outcome.getEconomy());
        }
        if (outcome.getCommand() != null && !outcome.getCommand().isEmpty()) {
            final String cmd = outcome.getCommand().replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
        if (outcome.getSound() != null) {
            try {
                player.playSound(player.getLocation(), org.bukkit.Sound.valueOf(outcome.getSound()), 1f, 1f);
            } catch (RuntimeException ignored) { }
        }
        curseManager.addLuck(player.getUniqueId(), switch (outcome.getType()) {
            case GOOD -> 2;
            case BAD -> -2;
            default -> 0;
        });
    }

    private void applyEconomy(@NotNull Player player, double amount) {
        if (integrationManager.isVaultPresent()) {
            final org.bukkit.plugin.RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp =
                    Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (rsp != null) {
                final net.milkbowl.vault.economy.Economy econ = rsp.getProvider();
                if (amount > 0) econ.depositPlayer(player, amount);
                else econ.withdrawPlayer(player, -amount);
            }
        }
        if (integrationManager.isPlayerPointsPresent()) {
            final org.black_ixx.playerpoints.PlayerPointsAPI api =
                    org.black_ixx.playerpoints.PlayerPoints.getInstance().getAPI();
            if (amount > 0) api.give(player.getUniqueId(), (int) amount);
            else api.take(player.getUniqueId(), (int) -amount);
        }
    }

    public void shutdown() {
        cooldowns.clear();
    }

    public @NotNull List<LuckyOutcome> getOutcomes() { return new ArrayList<>(outcomes); }
}
