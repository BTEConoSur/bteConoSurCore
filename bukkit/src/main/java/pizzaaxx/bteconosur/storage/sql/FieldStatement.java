package pizzaaxx.bteconosur.storage.sql;

public class FieldStatement<O> {

    private final String name;
    private final O object;
    private final Class<O> clazz;

    public FieldStatement(String name, O object, Class<O> clazz) {
        this.name = name;
        this.object = object;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public O get() {
        return object;
    }

    public Class<O> getClazz() {
        return clazz;
    }

    public static <O> FieldStatement<O> of(O object, Class<O> clazz, String name) {
        return new FieldStatement<>(name, object, clazz);
    }

}
