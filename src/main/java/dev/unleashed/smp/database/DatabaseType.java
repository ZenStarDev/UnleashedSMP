package dev.unleashed.smp.database;

import org.jetbrains.annotations.NotNull;

/**
 * Supported database backends.
 */
public enum DatabaseType {
    SQLITE,
    MYSQL,
    MARIADB;

    public static @NotNull DatabaseType fromString(@NotNull String value) {
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return SQLITE;
        }
    }
}
