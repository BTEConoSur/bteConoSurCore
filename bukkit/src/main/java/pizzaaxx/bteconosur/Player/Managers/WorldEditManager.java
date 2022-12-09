package pizzaaxx.bteconosur.Player.Managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WorldEditManager {

    private final BTEConoSur plugin;
    private final ServerPlayer serverPlayer;
    private int increment;
    private final Map<String, String> presets;
    private final Set<String> favAssets;

    // --- CONSTRUCTOR ---

    public WorldEditManager(@NotNull BTEConoSur plugin, @NotNull ServerPlayer serverPlayer) throws SQLException, JsonProcessingException {

        this.plugin = plugin;
        this.serverPlayer = serverPlayer;

        ResultSet set = plugin.getSqlManager().select(
                "world_edit_managers",
                new SQLColumnSet(
                        "increment",
                        "presets",
                        "fav_assets"
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", serverPlayer.getUUID()
                        )
                )
        ).retrieve();

        if (set.next()) {

            this.increment = set.getInt("increment");
            this.presets = plugin.getJSONMapper().readValue(set.getString("presets"), HashMap.class);
            this.favAssets = plugin.getJSONMapper().readValue(set.getString("fav_assets"), HashSet.class);

        } else {
            plugin.getSqlManager().insert(
                    "world_edit_managers",
                    new SQLValuesSet(
                        new SQLValue(
                                "uuid", serverPlayer.getUUID()
                        )
                    )
            ).execute();
            this.increment = 1;
            this.presets = new HashMap<>();
            this.favAssets = new HashSet<>();
        }
    }

    // --- GET ---

    public ServerPlayer getServerPlayer() {
        return serverPlayer;
    }

    public int getIncrement() {
        return increment;
    }

    public Map<String, String> getPresets() {
        return presets;
    }

    public String getPreset(String preset) {
        return presets.get(preset);
    }

    // --- SET ---

    public void setIncrement(int increment) throws SQLException {
        plugin.getSqlManager().update(
                "world_edit_managers",
                new SQLValuesSet(
                        new SQLValue(
                                "increment", increment
                        )
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", serverPlayer.getUUID()
                        )
                )
        ).execute();
        this.increment = increment;
    }

    public void setPreset(String name, String preset) throws SQLException {
        this.presets.put(name, preset);
        plugin.getSqlManager().update(
                "world_edit_managers",
                new SQLValuesSet(
                        new SQLValue(
                                "presets", this.presets
                        )
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", serverPlayer.getUUID()
                        )
                )
        ).execute();
    }

    public void deletePreset(String name) throws SQLException {
        this.presets.remove(name);
        plugin.getSqlManager().update(
                "world_edit_managers",
                new SQLValuesSet(
                        new SQLValue(
                                "presets", this.presets
                        )
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", serverPlayer.getUUID()
                        )
                )
        ).execute();
    }

    public Set<String> getFavAssets() {
        return favAssets;
    }

    public void addFavAsset(String id) throws SQLException {
        this.favAssets.add(id);
        plugin.getSqlManager().update(
                "world_edit_managers",
                new SQLValuesSet(
                        new SQLValue(
                                "fav_assets", this.favAssets
                        )
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", serverPlayer.getUUID()
                        )
                )
        ).execute();
    }

    public void removeFavAsset(String id) throws SQLException {
        this.favAssets.remove(id);
        plugin.getSqlManager().update(
                "world_edit_managers",
                new SQLValuesSet(
                        new SQLValue(
                                "fav_assets", this.favAssets
                        )
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", serverPlayer.getUUID()
                        )
                )
        ).execute();
    }

    public boolean isFavourite(String id) {
        return favAssets.contains(id);
    }
}
