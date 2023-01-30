package pizzaaxx.bteconosur.SQL;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Configuration.Configuration;
import pizzaaxx.bteconosur.SQL.Actions.DeleteAction;
import pizzaaxx.bteconosur.SQL.Actions.InsertAction;
import pizzaaxx.bteconosur.SQL.Actions.SelectAction;
import pizzaaxx.bteconosur.SQL.Actions.UpdateAction;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Utils.StringUtils;
import pizzaaxx.bteconosur.Utils.UUIDUtils;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLManager {

    private final BTEConoSur plugin;
    private final Connection connection;

    public SQLManager(@NotNull BTEConoSur plugin) throws SQLException {
        this.plugin = plugin;
        Configuration databaseConfig = new Configuration(plugin, "database");
        connection = DriverManager.getConnection(
                databaseConfig.getString("url"),
                databaseConfig.getString("username"),
                databaseConfig.getString("password")
        );
    }

    public Connection getConnection() {
        return connection;
    }

    public UpdateAction update(String tableName, SQLValuesSet values, SQLConditionSet conditions) {
        return new UpdateAction(plugin, tableName, values, conditions);
    }

    public InsertAction insert(String tableName, SQLValuesSet values) {
        return new InsertAction(plugin, tableName, values);
    }

    public SelectAction select(String tableName, SQLColumnSet columns, SQLConditionSet conditions) {
        return new SelectAction(plugin, tableName, columns, conditions);
    }

    public DeleteAction delete(String tableName, SQLConditionSet conditions) {
        return new DeleteAction(plugin, tableName, conditions);
    }

    @Nullable
    public UUID getUUID(@NotNull ResultSet set, String column) throws SQLException, IOException {
        InputStream stream = set.getBinaryStream(column);
        if (stream != null) {
            return UUIDUtils.getFromInputStream(set.getBinaryStream(column));
        }
        return null;
    }

}
