package pizzaaxx.bteconosur.player.worldedit;

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

public class WorldEditManager implements PlayerManager {

    private final BTEConoSurPlugin plugin;
    private final OnlineServerPlayer player;

    private int increment;

    public WorldEditManager(@NotNull BTEConoSurPlugin plugin, @NotNull OnlineServerPlayer player) throws SQLException {
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
            } else {
                increment = set.getInt("increment");
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
