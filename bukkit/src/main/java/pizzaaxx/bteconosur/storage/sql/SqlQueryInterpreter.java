package pizzaaxx.bteconosur.storage.sql;

import pizzaaxx.bteconosur.storage.query.CompoundQuery;
import pizzaaxx.bteconosur.storage.query.Query;

public class SqlQueryInterpreter {

    public String interpret(CompoundQuery compoundQuery) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Query query : compoundQuery.values()) {
            stringBuilder.append(
                    interpret(query)
            ).append(" ");
        }

        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    public String interpret(Query query) {
        switch (query.getOperation()) {
            case "contains":
                return " WHERE " + query.get(0) + " = '" + query.get(1) + "'";
        }
        return "";
    }

}
