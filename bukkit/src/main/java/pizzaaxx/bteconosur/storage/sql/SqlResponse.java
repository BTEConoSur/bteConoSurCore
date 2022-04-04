package pizzaaxx.bteconosur.storage.sql;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class SqlResponse {

    private final SqlAdapterRegistry sqlAdapterRegistry;

    public SqlResponse(SqlAdapterRegistry sqlAdapterRegistry) {
        this.sqlAdapterRegistry = sqlAdapterRegistry;
    }

    public <T> T response(Class<T> clazz, ResultSet resultSet) {

        SqlObjectAdapter<T> adapter = sqlAdapterRegistry.get(clazz);
        if (adapter != null) {
            return adapter.adapt(resultSet);
        }

        return null;
    }

}
