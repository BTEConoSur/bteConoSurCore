package pizzaaxx.bteconosur.storage;

import pizzaaxx.bteconosur.storage.query.CompoundQuery;
import pizzaaxx.bteconosur.storage.query.Query;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ObjectRepository<O> {

    void save(O object);

    O load(String identifier);

    default CompletableFuture<O> loadAsync(String identifier) {
        return query(
                CompoundQuery.simple(Query.of("contains", "id", identifier))
        );
    }

    CompletableFuture<Void> saveAsync(O object);

    CompletableFuture<O> query(CompoundQuery queries);

    CompletableFuture<List<O>> queryAll(CompoundQuery queries);

}
