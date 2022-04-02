package pizzaaxx.bteconosur.storage.query;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CompoundQuery {

    private final Set<Query> queries;

    public CompoundQuery(Set<Query> queries) {
        this.queries = queries;
    }

    public Set<Query> values() {
        return queries;
    }

    public static CompoundQuery simple(Query query) {
        Set<Query> queries = new HashSet<>();
        queries.add(query);

        return new CompoundQuery(queries);
    }

}
