package pizzaaxx.bteconosur;

import pizzaaxx.bteconosur.storage.DataSource;
import pizzaaxx.bteconosur.storage.connection.DatabaseConnection;
import pizzaaxx.bteconosur.storage.sql.MysqlConnection;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException {

        Map<String,String> source = new HashMap<>();
        DataSource dataSource = new DataSource();

        DatabaseConnection<Connection>
                databaseConnection = new MysqlConnection()
    }

}
