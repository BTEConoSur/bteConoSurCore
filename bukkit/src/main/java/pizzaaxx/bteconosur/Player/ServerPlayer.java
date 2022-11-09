package pizzaaxx.bteconosur.Player;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ServerPlayer {

    private final BTEConoSur plugin;
    private final UUID uuid;
    private final String name;

    public ServerPlayer(@NotNull BTEConoSur plugin, UUID uuid) throws SQLException {

        ResultSet set = plugin.getSqlManager().select(
                "players",
                new SQLColumnSet(
                        "*"
                ),
                new SQLConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", uuid
                        )
                )
        ).retrieve();

        if (set.next()) {

            this.plugin = plugin;
            this.uuid = uuid;
            this.name = set.getString("name");

        } else {
            plugin.error("Missing player data: " + uuid);
            throw new SQLException();
        }

    }

    public BTEConoSur getPlugin() {
        return plugin;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void save() {

    }

}
