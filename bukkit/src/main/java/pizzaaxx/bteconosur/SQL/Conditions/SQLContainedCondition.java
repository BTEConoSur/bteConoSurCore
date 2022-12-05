package pizzaaxx.bteconosur.SQL.Conditions;

import pizzaaxx.bteconosur.SQL.SQLParser;

public class SQLContainedCondition<K> implements SQLCondition {

    private final String column;
    private final Iterable<K> values;
    private final boolean contained;

    public SQLContainedCondition(String column, Iterable<K> values, boolean contained) {
        this.column = column;
        this.values = values;
        this.contained = contained;
    }

    @Override
    public String getString() {
        StringBuilder builder = new StringBuilder();
        builder.append(column).append((contained?" IN ":" NOT IN")).append("(");
        for (K value : values) {
            builder.append(SQLParser.getString(value)).append(", ");
        }
        builder.append(")");
        return builder.toString();
    }

}
