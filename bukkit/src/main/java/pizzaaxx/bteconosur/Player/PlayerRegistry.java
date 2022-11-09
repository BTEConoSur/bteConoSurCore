package pizzaaxx.bteconosur.Player;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRegistry { // REGISTRO CIVIL XD

    private final BTEConoSur plugin;
    private final Map<UUID, ServerPlayer> cache = new HashMap<>();
    private final Map<UUID, Long> deletionCache = new HashMap<>();

    public PlayerRegistry(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public boolean hasPlayedBefore(UUID uuid) {
        if (cache.containsKey(uuid)) {
            return true;
        }

        try {
            return plugin.getSqlManager().select(
                    "players",
                    new SQLColumnSet(
                            "uuid"
                    ),
                    new SQLConditionSet(
                            new SQLOperatorCondition(
                                    "uuid", "=", uuid
                            )
                    )
            ).retrieve().next();
        } catch (SQLException e) {
            plugin.warn("SQL error. Query: " + e.getMessage());
            return false;
        }
    }

    private void load(UUID uuid) {
        if (!cache.containsKey(uuid)) {
            try {
                cache.put(uuid, new ServerPlayer(plugin, uuid));
            } catch (SQLException e) {
                plugin.error("SQL error loading player data: " + uuid);
            }
        }
    }

    public boolean isLoaded(UUID uuid) {
        return cache.containsKey(uuid);
    }

    public ServerPlayer get(UUID uuid) {
        if (this.hasPlayedBefore(uuid)) {
            if (!this.isLoaded(uuid)) {
                this.load(uuid);
            }
            if (!Bukkit.getOfflinePlayer(uuid).isOnline()) {
                this.scheduleUnload(uuid);
            }
            return cache.get(uuid);
        }
        return null;
    }

    private void unload(UUID uuid) {
        if (this.isLoaded(uuid)) {
            this.get(uuid).save();
            cache.remove(uuid);
            deletionCache.remove(uuid);
        }
    }

    public void scheduleUnload(UUID uuid) {
        deletionCache.put(uuid, System.currentTimeMillis());
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!Bukkit.getOfflinePlayer(uuid).isOnline() && deletionCache.containsKey(uuid) && System.currentTimeMillis() - deletionCache.get(uuid) > 550000) {
                    unload(uuid);
                }
            }
        };
        runnable.runTaskLaterAsynchronously(plugin, 12000);
    }

}
