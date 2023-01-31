package pizzaaxx.bteconosur.Player;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PlayerRegistry { // REGISTRO CIVIL XD

    private final BTEConoSur plugin;
    private final Map<UUID, ServerPlayer> cache = new HashMap<>();
    private final Map<UUID, Long> deletionCache = new HashMap<>();

    public PlayerRegistry(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public boolean hasPlayedBefore(String name){
        try {
            return plugin.getSqlManager().select(
                    "players",
                    new SQLColumnSet(
                            "name"
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "name", "=", name
                            )
                    )
            ).retrieve().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean hasPlayedBefore(UUID uuid){
        if (cache.containsKey(uuid)) {
            return true;
        }

        try {
            return plugin.getSqlManager().select(
                    "players",
                    new SQLColumnSet(
                            "uuid"
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "uuid", "=", uuid
                            )
                    )
            ).retrieve().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public Collection<ServerPlayer> getLoadedPlayers() {
        return cache.values();
    }

    public void load(UUID uuid) {
        if (!cache.containsKey(uuid)) {
            try {
                cache.put(uuid, new ServerPlayer(plugin, uuid));
            } catch (SQLException | JsonProcessingException e) {
                e.printStackTrace();
                plugin.error("SQL error loading player data: " + uuid);
            }
        }
    }

    public boolean isLoaded(UUID uuid) {
        return cache.containsKey(uuid);
    }

    public ServerPlayer get(String name) throws SQLException, IOException {
        ResultSet set = plugin.getSqlManager().select(
                "players",
                new SQLColumnSet(
                        "uuid"
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "name", "=", name
                        )
                )
        ).retrieve();

        if (set.next()) {
            UUID uuid = plugin.getSqlManager().getUUID(set, "uuid");
            return this.get(uuid);
        } else {
            return null;
        }
    }

    public ServerPlayer get(UUID uuid){
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
        cache.remove(uuid);
        deletionCache.remove(uuid);
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
