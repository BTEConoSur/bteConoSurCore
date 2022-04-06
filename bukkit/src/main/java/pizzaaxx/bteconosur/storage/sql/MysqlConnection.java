package pizzaaxx.bteconosur.storage.sql;

import pizzaaxx.bteconosur.storage.DataSource;
import pizzaaxx.bteconosur.storage.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlConnection implements DatabaseConnection<Connection> {

    private static final String CLASS_NAME = "com.mysql.jdbc.Driver";

    private final DataSource source;

    private Connection connection;
    private boolean statusConnection;

    public MysqlConnection(DataSource source) throws ClassNotFoundException {
        this.source = source;
        Class.forName(CLASS_NAME);
    }

    @Override
    public void connect() {
        String host = source.get("host");
        String user = source.get("user");
        String password = source.get("password");
        int port = source.
                get("port", Integer.class);

        try {
            String prefix_url = "jdbc:mysql://";
            connection = DriverManager.getConnection(prefix_url + host + ":" + port, user, password);
            statusConnection = true;
        } catch (SQLException e) {
            e.printStackTrace();
            statusConnection = false;
        }

    }

    @Override
    public Connection get() {
        return connection;
    }

    @Override
    public boolean statusConnection() {
        return statusConnection;
    }

}
