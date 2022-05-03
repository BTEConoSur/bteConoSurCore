package pizzaaxx.bteconosur.storage;

import pizzaaxx.bteconosur.server.player.Identifiable;
import pizzaaxx.bteconosur.storage.query.CompoundQuery;
import pizzaaxx.bteconosur.storage.query.Query;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ObjectRepository<O extends Identifiable> {

    void save(O object);

    void delete(O object);

    default CompletableFuture<O> loadAsync(String identifier) {
        return query(
                CompoundQuery.simple(Query.of("contains", "id", identifier))
        );
    }

    boolean exists(O object);

    default CompletableFuture<Void> saveAsync(O object) {
        return CompletableFuture.runAsync(() -> save(object));
    }

    CompletableFuture<O> query(CompoundQuery queries);

    O querySync(CompoundQuery queries);


    CompletableFuture<List<O>> queryAll(CompoundQuery queries);

}
