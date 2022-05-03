package pizzaaxx.bteconosur.storage.sql;

import pizzaaxx.bteconosur.server.player.Identifiable;

import java.util.HashMap;
import java.util.Map;

public class SqlAdapterRegistry {

    private final Map<Class<?>, SqlObjectAdapter<?>> adapters = new HashMap<>();

    public <O> SqlObjectAdapter<O> get(Class<? extends Identifiable> clazz) {
        return (SqlObjectAdapter<O>) adapters.get(clazz);
    }

    public <O> void registerAdapter(Class<O> clazz, SqlObjectAdapter<O> adapter) {
        adapters.put(clazz, adapter);
    }

}