package pizzaaxx.bteconosur.SQL.Conditions;

public class SQLNullCondition implements SQLCondition {

    private final boolean isNull;

    public SQLNullCondition(boolean isNull) {
        this.isNull = isNull;
    }

    @Override
    public String getString() {
        return (isNull ? "IS NULL" : "IS NOT NULL");
    }
}
