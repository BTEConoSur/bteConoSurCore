package pizzaaxx.bteconosur.Player.Managers;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MiscManager {

    private final BTEConoSur plugin;
    private final ServerPlayer serverPlayer;
    private int increment;


    public MiscManager(BTEConoSur plugin, ServerPlayer serverPlayer, @NotNull ResultSet set) throws SQLException {
        this.plugin = plugin;
        this.serverPlayer = serverPlayer;
        this.increment = set.getInt("increment");
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) throws SQLException {
        plugin.getSqlManager().update(
                "players",
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
}
