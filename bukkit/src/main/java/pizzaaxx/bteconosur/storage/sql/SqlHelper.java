package pizzaaxx.bteconosur.storage.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlHelper {

    private final Connection connection;

    public SqlHelper(Connection connection) {
        this.connection = connection;
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

    public ResultSet preparedQuery(QueryStatement queryStatement,
                                   String sql) {

        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql);

            for (int i = 1; i < queryStatement.fields().size(); i++) {
                FieldStatement<?> fieldStatement = queryStatement
                        .fields().get(i);

                StatementQueryAdapter.adapt(
                        preparedStatement, i, fieldStatement
                );

            }

            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
