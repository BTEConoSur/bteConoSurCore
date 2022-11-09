package pizzaaxx.bteconosur.SQL;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Configuration.Configuration;
import pizzaaxx.bteconosur.SQL.Actions.InsertAction;
import pizzaaxx.bteconosur.SQL.Actions.SelectAction;
import pizzaaxx.bteconosur.SQL.Actions.UpdateAction;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

}
