package pizzaaxx.bteconosur.storage.sql;

import java.util.ArrayList;
import java.util.List;

public class QueryStatement {

    private final List<FieldStatement<?>> fieldsStatement
            = new ArrayList<>();

    public QueryStatement insert(FieldStatement<?> fieldStatement) {
        fieldsStatement.add(fieldStatement);
        return this;
    }

    public List<FieldStatement<?>> fields() {
        return fieldsStatement;
    }

    public static QueryStatement create() {
        return new QueryStatement();
    }

}
