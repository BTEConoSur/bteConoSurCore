package pizzaaxx.bteconosur.SQL;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;

import java.sql.Connection;

public class SQLManager {

    private final BTEConoSur plugin;
    private final Connection connection;

    public SQLManager(@NotNull BTEConoSur plugin) {
        this.plugin = plugin;
        this.connection = plugin.getDBConnection();
    }

}
