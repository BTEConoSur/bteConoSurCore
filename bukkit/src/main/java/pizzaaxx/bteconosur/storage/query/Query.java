package pizzaaxx.bteconosur.storage.query;

public class Query {

    private final String operation;
    private final String metadata;

    public Query(String operation,
                 String metadata) {
        this.operation = operation;
        this.metadata = metadata;
    }

    public String getOperation() {
        return operation;
    }

    public String getMetadata() {
        return metadata;
    }

}
