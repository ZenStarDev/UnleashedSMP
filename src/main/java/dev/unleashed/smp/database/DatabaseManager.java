package dev.unleashed.smp.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.unleashed.smp.UnleashedPlugin;
import dev.unleashed.smp.config.ConfigurationManager;
import dev.unleashed.smp.logging.PluginLogger;
import dev.unleashed.smp.managers.SchedulerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;

/**
 * Owns the connection pool (HikariCP) and exposes async query helpers.
 *
 * <p>All JDBC access goes through {@link #getConnection()} and is executed off the main thread via
 * the {@link SchedulerManager}. The plugin degrades to an in-memory fallback if the configured
 * backend cannot be reached, so the server never hard-crashes.</p>
 */
public final class DatabaseManager {

    private final UnleashedPlugin plugin;
    private final PluginLogger logger;
    private final ConfigurationManager configManager;
    private final SchedulerManager scheduler;

    private DatabaseType type;
    private HikariDataSource source;
    private PlayerDataDao playerDataDao;
    private EventLogDao eventLogDao;
    private boolean available;

    public DatabaseManager(@NotNull UnleashedPlugin plugin, @NotNull PluginLogger logger,
                           @NotNull ConfigurationManager configManager,
                           @NotNull SchedulerManager scheduler) {
        this.plugin = plugin;
        this.logger = logger;
        this.configManager = configManager;
        this.scheduler = scheduler;
    }

    public void connect() {
        final String raw = configManager.database().getString("type", "sqlite");
        type = DatabaseType.fromString(raw);
        try {
            switch (type) {
                case SQLITE -> initSqlite();
                case MYSQL -> initPool("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC",
                        configManager.database().getSection("mysql"));
                case MARIADB -> initPool("jdbc:mariadb://%s:%d/%s?useSSL=false",
                        configManager.database().getSection("mariadb"));
            }
            available = true;
            logger.database("Connected to %s database", type);
        } catch (RuntimeException ex) {
            logger.error("Database connection failed, using memory fallback: " + ex.getMessage());
            available = false;
        }
    }

    private void initSqlite() {
        final String file = configManager.database().getString("sqlite.file", "plugins/UnleashedSMP/data.db");
        final File dbFile = new File(plugin.getDataFolder(), file.replace("plugins/UnleashedSMP/", ""));
        final File parent = dbFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        final HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        cfg.setPoolName("UnleashedSMP-SQLite");
        cfg.setMaximumPoolSize(2);
        source = new HikariDataSource(cfg);
    }

    private void initPool(@NotNull String urlTemplate,
                          @org.jetbrains.annotations.Nullable org.bukkit.configuration.ConfigurationSection section) {
        final String host = section == null ? "localhost" : section.getString("host", "localhost");
        final int port = section == null ? 3306 : section.getInt("port", 3306);
        final String db = section == null ? "unleashedsmp" : section.getString("database", "unleashedsmp");
        final String user = section == null ? "root" : section.getString("username", "root");
        final String pass = section == null ? "" : section.getString("password", "");
        final HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(String.format(urlTemplate, host, port, db));
        cfg.setUsername(user);
        cfg.setPassword(pass);
        final org.bukkit.configuration.ConfigurationSection pool = section == null ? null : section.getConfigurationSection("pool");
        cfg.setMaximumPoolSize(pool == null ? 10 : pool.getInt("maximum-pool-size", 10));
        cfg.setMinimumIdle(pool == null ? 2 : pool.getInt("minimum-idle", 2));
        cfg.setConnectionTimeout(pool == null ? 30000 : pool.getLong("connection-timeout", 30000));
        cfg.setIdleTimeout(pool == null ? 600000 : pool.getLong("idle-timeout", 600000));
        cfg.setMaxLifetime(pool == null ? 1800000 : pool.getLong("max-lifetime", 1800000));
        cfg.setPoolName("UnleashedSMP-" + type);
        source = new HikariDataSource(cfg);
    }

    /**
     * Creates the schema and applies migrations.
     */
    public void migrate() {
        if (!available) {
            return;
        }
        scheduler.runAsync(() -> {
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {
                final String prefix = configManager.database().getString("table-prefix", "unleashed_");
                stmt.execute("CREATE TABLE IF NOT EXISTS " + prefix + "player_data ("
                        + "uuid VARCHAR(36) PRIMARY KEY, username VARCHAR(64), lucky_uses INT DEFAULT 0, "
                        + "last_lucky BIGINT DEFAULT 0, mutants_killed INT DEFAULT 0, created_at BIGINT)");
                stmt.execute("CREATE TABLE IF NOT EXISTS " + prefix + "event_log ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, event VARCHAR(64), started_at BIGINT, "
                        + "ended_at BIGINT, world VARCHAR(64))".replace("AUTOINCREMENT", type == DatabaseType.SQLITE ? "AUTOINCREMENT" : "AUTO_INCREMENT"));
                playerDataDao = new PlayerDataDao(this, prefix);
                eventLogDao = new EventLogDao(this, prefix);
                logger.database("Schema migrated");
            } catch (SQLException ex) {
                logger.error("Migration failed: " + ex.getMessage());
                available = false;
            }
        });
    }

    public @Nullable Connection getConnection() throws SQLException {
        if (source == null) {
            throw new SQLException("No data source");
        }
        return source.getConnection();
    }

    public @NotNull CompletableFuture<Integer> executeUpdate(@NotNull String sql, Object... params) {
        final CompletableFuture<Integer> future = new CompletableFuture<>();
        scheduler.runAsync(() -> {
            try (Connection conn = getConnection();
                 java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                bind(ps, params);
                future.complete(ps.executeUpdate());
            } catch (SQLException ex) {
                logger.error("Execute update failed: " + ex.getMessage());
                future.completeExceptionally(ex);
            }
        });
        return future;
    }

    public <T> @NotNull CompletableFuture<T> query(@NotNull java.util.function.Function<Connection, T> fn) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        scheduler.runAsync(() -> {
            try (Connection conn = getConnection()) {
                future.complete(fn.apply(conn));
            } catch (SQLException ex) {
                logger.error("Query failed: " + ex.getMessage());
                future.completeExceptionally(ex);
            }
        });
        return future;
    }

    private static void bind(@NotNull java.sql.PreparedStatement ps, @NotNull Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    public @NotNull PlayerDataDao getPlayerDataDao() {
        return playerDataDao;
    }

    public @NotNull EventLogDao getEventLogDao() {
        return eventLogDao;
    }

    public boolean isAvailable() {
        return available;
    }

    public @NotNull DatabaseType getType() {
        return type;
    }

    public void reload() {
        disconnect();
        connect();
        migrate();
    }

    public void disconnect() {
        if (source != null && !source.isClosed()) {
            source.close();
        }
        source = null;
        available = false;
    }
}
