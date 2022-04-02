package pizzaaxx.bteconosur.storage.connection;

/**
 * Represent a connection
 */

public interface DatabaseConnection<C> {

    void connect();

    C get();

    boolean statusConnection();

}
