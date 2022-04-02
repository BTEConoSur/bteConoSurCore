package pizzaaxx.bteconosur.storage.query;

public class Query {

    private final String operation;
    private final String[] metadata;

    public Query(String operation,
                 String[] metadata) {
        this.operation = operation;
        this.metadata = metadata;
    }

    public String getOperation() {
        return operation;
    }

    public String get(int value) {
        return metadata[value];
    }

    public static Query of(String operation, String... metadata) {
        return new Query(operation, metadata);
    }

}
