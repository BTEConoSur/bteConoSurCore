package pizzaaxx.bteconosur.storage.mysql;

import pizzaaxx.bteconosur.storage.connection.DatabaseConnection;

import java.sql.Connection;

public class MysqlConnection implements DatabaseConnection<Connection> {



    @Override
    public void connect() {

    }

    @Override
    public Connection get() {
        return null;
    }

    @Override
    public boolean statusConnection() {
        return false;
    }
}
