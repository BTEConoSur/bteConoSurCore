package pizzaaxx.bteconosur.storage.query;

import java.util.HashSet;
import java.util.Set;

public class QueryBuilder {

    private final Set<Query> queries = new HashSet<>();

    public QueryBuilder insert(Query query) {
        queries.add(query);
        return this;
    }

    public CompoundQuery build() {
        return new CompoundQuery(queries);
    }

    public static QueryBuilder create() {
        return new QueryBuilder();
    }

}
