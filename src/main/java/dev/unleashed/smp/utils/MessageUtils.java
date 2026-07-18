package dev.unleashed.smp.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Utilities for parsing and sending MiniMessage-formatted text and resolving
 * PlaceholderAPI placeholders where available.
 */
public final class MessageUtils {

    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final Pattern HEX_PATTERN = Pattern.compile("(?i)&#([0-9a-f]{6})");

    private MessageUtils() {
    }

    public static @NotNull MiniMessage miniMessage() {
        return MINI;
    }

    /**
     * Parses a MiniMessage string into a Component.
     *
     * @param message the raw message
     * @return the parsed component
     */
    public static @NotNull Component parse(@org.jetbrains.annotations.NotNull String message) {
        return MINI.deserialize(message == null ? "" : message);
    }

    /**
     * Identity helper so a Component can be passed where a parse(String) call is already present.
     *
     * @param message the component
     * @return the same component
     */
    public static @NotNull Component parse(@org.jetbrains.annotations.NotNull Component message) {
        return message;
    }

    /**
     * Parses a MiniMessage string and resolves PlaceholderAPI placeholders for a player.
     *
     * @param player  the target player (may be null)
     * @param message the raw message
     * @return the parsed component
     */
    public static @NotNull Component parse(@org.jetbrains.annotations.Nullable Player player,
                                           @org.jetbrains.annotations.NotNull String message) {
        String resolved = message;
        if (player != null && isPlaceholderApiPresent()) {
            resolved = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, message);
        }
        return parse(resolved);
    }

    /**
     * Sends a Component to a CommandSender using the Adventure overload available on Paper/Spigot.
     *
     * @param sender  the sender
     * @param message the component
     */
    public static void send(@org.jetbrains.annotations.NotNull org.bukkit.command.CommandSender sender,
                             @org.jetbrains.annotations.NotNull Component message) {
        sender.sendMessage(net.kyori.adventure.identity.Identity.nil(), message,
                net.kyori.adventure.audience.MessageType.CHAT);
    }

    /**
     * Converts an ampersand/legacy color string to a Component.
     *
     * @param message the legacy message
     * @return the component
     */
    public static @NotNull Component fromLegacy(@org.jetbrains.annotations.NotNull String message) {
        return LegacyComponentSerializer.legacySection().deserialize(message.replace('&', '§'));
    }

    public static boolean isPlaceholderApiPresent() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
}
