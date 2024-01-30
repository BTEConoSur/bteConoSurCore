package pizzaaxx.bteconosur.utils.registry;

import java.sql.SQLException;

public interface RegistrableEntity<K> {

    K getID();

    /**
     * Called when the server stops or the plugin is disabled, if any data needs to be saved or a process needs to be stopped.
     */
    void disconnected() throws SQLException;

}
