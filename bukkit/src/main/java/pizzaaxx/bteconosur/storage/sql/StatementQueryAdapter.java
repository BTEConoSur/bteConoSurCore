package pizzaaxx.bteconosur.storage.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatementQueryAdapter {

    public static void adapt(PreparedStatement preparedStatement,
                             int index,
                             FieldStatement<?> fieldStatement) throws SQLException {

        switch (fieldStatement.getClazz().getSimpleName()) {
            case "String":
                System.out.println("Set index " + index + " field " + fieldStatement.get() + " name " + fieldStatement.getName());
                preparedStatement.setString(index, (String) fieldStatement.get());
                break;
            case "Integer":
                preparedStatement.setInt(index, (int) fieldStatement.get());
                break;
            case "Double":
                preparedStatement.setDouble(index, (double) fieldStatement.get());
                break;
            case "Long":
                preparedStatement.setLong(index, (long) fieldStatement.get());
                break;
            case "Float":
                preparedStatement.setFloat(index, (long) fieldStatement.get());
            case "Boolean":
                preparedStatement.setBoolean(index, (boolean) fieldStatement.get());
        }

    }

}