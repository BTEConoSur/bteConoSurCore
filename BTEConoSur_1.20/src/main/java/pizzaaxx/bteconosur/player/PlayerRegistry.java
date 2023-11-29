package pizzaaxx.bteconosur.player;

import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.Values.SQLValue;
import com.github.PeterMassmann.Values.SQLValuesSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.utils.SQLUtils;
import pizzaaxx.bteconosur.utils.registry.BaseRegistry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class PlayerRegistry extends BaseRegistry<OfflineServerPlayer, UUID> {

    private final BTEConoSurPlugin plugin;

    public PlayerRegistry(BTEConoSurPlugin plugin) {
        super(
                plugin,
                () -> {
                    Collection<UUID> uuids = new ArrayList<>();
                    try {
                        ResultSet set = plugin.getSqlManager().select(
                                "players",
                                new SQLColumnSet("uuid"),
                                new SQLANDConditionSet()
                        ).retrieve();

                        while(set.next()) {
                            uuids.add(
                                    SQLUtils.uuidFromBytes(set.getBytes("uuid"))
                            );
                        }
                    } catch (SQLException e) {
                        plugin.error("Could not load player registry.");
                    }
                    return uuids;
                },
                uuid -> {
                    try {
                        OfflineServerPlayer offlineServerPlayer = new OfflineServerPlayer(plugin, uuid);
                        if (offlineServerPlayer.isOnline()) {
                            return offlineServerPlayer.asOnlinePlayer();
                        }
                        return offlineServerPlayer;
                    } catch (SQLException e) {
                        plugin.error("Error loading server player instance. (UUID:" + uuid + ")");
                    }
                    return null;
                },
                true
        );
        this.plugin = plugin;
    }

    public void login(UUID uuid) throws SQLException {
        if (this.isLoaded(uuid)) {
            OfflineServerPlayer current = this.cacheMap.get(uuid);
            if (!(current instanceof OnlineServerPlayer)) {
                OnlineServerPlayer online = current.asOnlinePlayer();
                this.cacheMap.put(uuid, online);
            }
        }
    }

    public void quit(UUID uuid) {
        if (this.isLoaded(uuid)) {
            OfflineServerPlayer offline = this.cacheMap.get(uuid);
            if (offline instanceof OnlineServerPlayer online) {
                online.disconnected();
                this.cacheMap.put(uuid, online.asOfflinePlayer());
            }
        }
        try {
            plugin.getSqlManager().update(
                    "players",
                    new SQLValuesSet(
                            new SQLValue(
                                    "last_disconnected", System.currentTimeMillis()
                            )
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "uuid", "=", uuid
                            )
                    )
            ).execute();
        } catch (SQLException ignored) {}
    }

}
