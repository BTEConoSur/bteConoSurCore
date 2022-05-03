package pizzaaxx.bteconosur.storage.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlQueryWrapper {

    private final Connection connection;

    public SqlQueryWrapper(Connection connection) {
        this.connection = connection;
    }

    public void simpleQuery(String sql) {
        try {
            connection.createStatement()
                    .execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet query(String sql) {
        try {
            return connection.createStatement()
                    .executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int updateQuery(QueryStatement queryStatement,
                            String sql) {

        try {
            PreparedStatement preparedStatement = generalPreparedQuery(queryStatement, sql);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public PreparedStatement generalPreparedQuery(QueryStatement queryStatement, String sql) throws SQLException {
        PreparedStatement preparedStatement =
                connection.prepareStatement(sql);

        System.out.println("Sql > " + sql + ";");

        for (int i = 0; i < queryStatement.fields().size(); i++) {
            FieldStatement<?> fieldStatement = queryStatement
                    .fields().get(i);

            System.out.println(fieldStatement.getName());

            StatementQueryAdapter.adapt(
                    preparedStatement, i + 1, fieldStatement
            );

        }

        return preparedStatement;
    }

    public ResultSet preparedQuery(QueryStatement queryStatement,
                                   String sql) {

        try {

            PreparedStatement preparedStatement =
                    generalPreparedQuery(queryStatement, sql);

            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
