package pizzaaxx.bteconosur.player.worldedit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.SQLResult;
import com.github.PeterMassmann.Values.SQLValue;
import com.github.PeterMassmann.Values.SQLValuesSet;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;
import pizzaaxx.bteconosur.player.PlayerManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class WorldEditManager implements PlayerManager {

    private final BTEConoSurPlugin plugin;
    private final OnlineServerPlayer player;

    private int increment;
    private final Map<String, String> presets;

    public WorldEditManager(@NotNull BTEConoSurPlugin plugin, @NotNull OnlineServerPlayer player) throws SQLException, JsonProcessingException {
        this.plugin = plugin;
        this.player = player;

        try (SQLResult result = plugin.getSqlManager().select(
                "worldedit_managers",
                new SQLColumnSet("*"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition("uuid", "=", player.getUUID())
                )
        ).retrieve()) {
            ResultSet set = result.getResultSet();

            if (!set.next()) {
                plugin.getSqlManager().insert(
                        "worldedit_managers",
                        new SQLValuesSet(
                                new SQLValue("uuid", player.getUUID()),
                                new SQLValue("increment", 1),
                                new SQLValue("presets", "{}"),
                                new SQLValue("fav_assets", "[]"),
                                new SQLValue("asset_groups", "[]")
                        )
                ).execute();
                increment = 1;
                this.presets = new HashMap<>();
            } else {
                increment = set.getInt("increment");
                this.presets = new HashMap<>();
                JsonNode presetsNode = plugin.getJsonMapper().readTree(set.getString("presets"));
                presetsNode.fields().forEachRemaining(entry -> {
                    presets.put(entry.getKey(), entry.getValue().asText());
                });
            }
        }
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) throws SQLException {
        this.increment = increment;
        this.saveValue("increment", increment);
    }

    public void setPreset(String name, String value) throws SQLException {
        presets.put(name, value);
        this.saveValue("presets", presets);
    }

    public String getPreset(String name) {
        return presets.get(name);
    }

    public void removePreset(String name) throws SQLException {
        presets.remove(name);
        this.saveValue("presets", presets);
    }

    public Map<String, String> getPresets() {
        return presets;
    }

    @Override
    public void saveValue(String key, Object value) throws SQLException {
        plugin.getSqlManager().update(
                "discord_managers",
                new SQLValuesSet(
                        new SQLValue(key, value)
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition("uuid", "=", player.getUUID())
                )
        ).execute();
    }
}
