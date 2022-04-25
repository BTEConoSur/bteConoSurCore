package pizzaaxx.bteconosur.storage.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatementQueryAdapter {

    public static void adapt(PreparedStatement preparedStatement,
                             int index,
                             FieldStatement<?> fieldStatement) throws SQLException {

        switch (fieldStatement.getClazz().getSimpleName()) {
            case "String":
                preparedStatement.setString(index, fieldStatement.get().toString());
                break;
            case "Integer":
                preparedStatement.setInt(index, Integer.parseInt(fieldStatement.get().toString()));
                break;
            case "Double":
                preparedStatement.setDouble(index, Double.parseDouble(fieldStatement.get().toString()));
                break;
            case "Long":
                preparedStatement.setLong(index, Long.parseLong(fieldStatement.get().toString()));
                break;
            case "Float":
                preparedStatement.setFloat(index, Float.parseFloat(fieldStatement.get().toString()));
                break;
            case "Boolean":
                preparedStatement.setBoolean(index, Boolean.parseBoolean(fieldStatement.get().toString()));
        }
    }
}
