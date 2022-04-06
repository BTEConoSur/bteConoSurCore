package pizzaaxx.bteconosur.storage.sql;

public class FieldStatement<O> {

    private final O object;
    private final Class<O> clazz;

    public FieldStatement(O object, Class<O> clazz) {
        this.object = object;
        this.clazz = clazz;
    }

    public O get() {
        return object;
    }

    public Class<O> getClazz() {
        return clazz;
    }

    public static <O> FieldStatement<O> of(O object, Class<O> clazz) {
        return new FieldStatement<>(object, clazz);
    }

}
