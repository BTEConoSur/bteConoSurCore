package pizzaaxx.bteconosur.storage;

public interface ObjectAdapter<T, O, I> {

    T adapt(O out);

    I write(T object);

}
