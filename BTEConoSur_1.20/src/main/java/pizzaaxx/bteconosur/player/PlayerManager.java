package pizzaaxx.bteconosur.player;

import java.sql.SQLException;

public interface PlayerManager {

    void saveValue(String key, Object value) throws SQLException;

}
