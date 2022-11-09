package pizzaaxx.bteconosur.SQL.Actions;

import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLCondition;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SelectAction {

    private final BTEConoSur plugin;
    private final String tableName;
    private final SQLColumnSet columns;
    private final SQLConditionSet conditions;

    public SelectAction(BTEConoSur plugin, String tableName, SQLColumnSet columns, SQLConditionSet conditions) {
        this.plugin = plugin;
        this.tableName = tableName;
        this.columns = columns;
        this.conditions = conditions;
    }

    public void addCondition(SQLCondition condition) {
        this.conditions.addCondition(condition);
    }

    public void addColumn(String column) {
        this.columns.addColumn(column);
    }

    public ResultSet retrieve() throws SQLException {
        StringBuilder query = new StringBuilder("SELECT ");
        query.append(columns.getString()).append(" FROM ").append(tableName).append(" WHERE ");
        query.append(conditions.getString());
        return plugin.getSqlManager().getConnection().createStatement().executeQuery(query.toString());
    }
}
