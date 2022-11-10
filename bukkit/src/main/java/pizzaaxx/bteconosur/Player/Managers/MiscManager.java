package pizzaaxx.bteconosur.Player.Managers;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.ServerPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MiscManager {

    private final BTEConoSur plugin;
    private final ServerPlayer serverPlayer;
    private final int increment;


    public MiscManager(BTEConoSur plugin, ServerPlayer serverPlayer, @NotNull ResultSet set) throws SQLException {
        this.plugin = plugin;
        this.serverPlayer = serverPlayer;
        this.increment = set.getInt("increment");
    }

    public int getIncrement() {
        return increment;
    }
}
