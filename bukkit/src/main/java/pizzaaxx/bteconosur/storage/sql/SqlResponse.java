package pizzaaxx.bteconosur.storage.sql;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class SqlResponse {

    private final Map<Class<?>, SqlObjectAdapter<?>> adapters = new HashMap<>();

    public <O> O response(Class<O> clazz, ResultSet resultSet) {

        SqlObjectAdapter<O> adapter = (SqlObjectAdapter<O>) adapters.get(clazz);
        if (adapter != null) {
            return adapter.adapt(resultSet);
        }

        return null;
    }

    public <O> void registerAdapter(Class<O> clazz, SqlObjectAdapter<O> adapter) {
        adapters.put(clazz, adapter);
    }

}
