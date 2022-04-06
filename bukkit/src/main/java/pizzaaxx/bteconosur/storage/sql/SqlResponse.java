package pizzaaxx.bteconosur.storage.sql;

import pizzaaxx.bteconosur.server.player.Identifiable;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class SqlResponse {

    private final SqlAdapterRegistry sqlAdapterRegistry;

    public SqlResponse(SqlAdapterRegistry sqlAdapterRegistry) {
        this.sqlAdapterRegistry = sqlAdapterRegistry;
    }

    public <T extends Identifiable> T response(Class<T> clazz, ResultSet resultSet) {

        SqlObjectAdapter<T> adapter = sqlAdapterRegistry.get(clazz);
        if (adapter != null) {
            return adapter.adapt(resultSet);
        }

        return null;
    }

}
