package pizzaaxx.bteconosur.SQL.Conditions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SQLConditionSet {

    private final Set<SQLCondition> conditions;

    public SQLConditionSet(SQLCondition... conditions) {
        this.conditions = new HashSet<>(Arrays.asList(conditions));
    }

    public Set<SQLCondition> getConditions() {
        return conditions;
    }

    public boolean isEmpty() {
        return conditions.isEmpty();
    }

    public void addCondition(SQLCondition condition) {
        conditions.add(condition);
    }

    public String getString() {
        StringBuilder builder = new StringBuilder();

        int size = conditions.size();
        int counter = 1;
        for (SQLCondition condition : conditions) {
            builder.append(condition.getString());

            if (counter < size) {
                builder.append(" AND ");
            }
            counter++;
        }

        return builder.toString();
    }
}
