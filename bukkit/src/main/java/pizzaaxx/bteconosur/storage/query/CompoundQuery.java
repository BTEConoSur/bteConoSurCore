package pizzaaxx.bteconosur.storage.query;

import java.util.Set;

public class CompoundQuery {

    private final Set<Query> queries;

    public CompoundQuery(Set<Query> queries) {
        this.queries = queries;
    }

    public Set<Query> values() {
        return queries;
    }

}
