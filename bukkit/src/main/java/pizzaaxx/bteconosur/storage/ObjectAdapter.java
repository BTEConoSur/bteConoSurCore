package pizzaaxx.bteconosur.storage;

public interface ObjectAdapter<O, D> {

    O adapt(D data);

    void write(O object);

}
