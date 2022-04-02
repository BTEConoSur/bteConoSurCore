package pizzaaxx.bteconosur.storage;

public interface ObjectRepository<O> {

    void save(O object);

    O load(String identifier);



}
