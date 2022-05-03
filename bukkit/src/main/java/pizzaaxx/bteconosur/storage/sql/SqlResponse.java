package pizzaaxx.bteconosur.storage.sql;

import pizzaaxx.bteconosur.server.player.Identifiable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlResponse {

    private final SqlAdapterRegistry sqlAdapterRegistry;

    public SqlResponse(SqlAdapterRegistry sqlAdapterRegistry) {
        this.sqlAdapterRegistry = sqlAdapterRegistry;
    }

    public <T extends Identifiable> T response(Class<T> clazz, ResultSet resultSet) {

        SqlObjectAdapter<T> adapter = sqlAdapterRegistry.get(clazz);
        if (adapter != null) {
            System.out.println("resolving adapter...");
            return adapter.adapt(resultSet);
        }

        return null;
    }

    public <T extends Identifiable> List<T> responseAll(Class<T> clazz, ResultSet resultSet) {

        SqlObjectAdapter<T> adapter = sqlAdapterRegistry.get(clazz);
        if (adapter != null) {
            try {
                return adapter.adaptAll(resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return new ArrayList<>();

    }

}
