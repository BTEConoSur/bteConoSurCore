package pizzaaxx.bteconosur.player;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.SQLResult;
import com.github.PeterMassmann.Values.SQLValue;
import com.github.PeterMassmann.Values.SQLValuesSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.utils.SQLUtils;
import pizzaaxx.bteconosur.utils.registry.BaseRegistry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static pizzaaxx.bteconosur.utilities.BackCommand.BACK_LOCATIONS;

public class PlayerRegistry extends BaseRegistry<OfflineServerPlayer, UUID> {

    private final BTEConoSurPlugin plugin;

    public PlayerRegistry(BTEConoSurPlugin plugin) {
        super(
                plugin,
                () -> {
                    Collection<UUID> uuids = new ArrayList<>();
                    try (SQLResult result = plugin.getSqlManager().select(
                            "players",
                            new SQLColumnSet("uuid"),
                            new SQLANDConditionSet()
                    ).retrieve()) {
                        ResultSet set = result.getResultSet();
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
                    } catch (SQLException | JsonProcessingException e) {
                        plugin.error("Error loading server player instance. (UUID:" + uuid + ")");
                        e.printStackTrace();
                    }
                    return null;
                },
                true
        );
        this.plugin = plugin;
    }

    public void login(UUID uuid) throws SQLException, JsonProcessingException {
        if (this.isLoaded(uuid)) {
            OfflineServerPlayer offline = this.cacheMap.get(uuid);
            if (!(offline instanceof OnlineServerPlayer)) {
                OnlineServerPlayer online = offline.asOnlinePlayer();
                this.cacheMap.put(uuid, online);
            }
        }
    }

    public void quit(UUID uuid) {
        if (this.isLoaded(uuid)) {
            OfflineServerPlayer offline = this.cacheMap.get(uuid);
            if (offline instanceof OnlineServerPlayer online) {
                try {
                    online.disconnected();
                    this.cacheMap.put(uuid, online.asOfflinePlayer());
                } catch (SQLException e) {
                    plugin.error("Error saving player data. (UUID:" + uuid + ")");
                    e.printStackTrace();
                }
            }
        }
        BACK_LOCATIONS.remove(uuid);
    }

}
