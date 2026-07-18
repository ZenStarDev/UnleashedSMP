package dev.unleashed.smp.exceptions;

/**
 * Thrown when a configuration file is missing, malformed, or contains invalid values.
 */
public class ConfigurationException extends RuntimeException {

    public ConfigurationException(@org.jetbrains.annotations.NotNull String message) {
        super(message);
    }

    public ConfigurationException(@org.jetbrains.annotations.NotNull String message,
                                  @org.jetbrains.annotations.Nullable Throwable cause) {
        super(message, cause);
    }
}
