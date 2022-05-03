package pizzaaxx.bteconosur.storage.sql;

import pizzaaxx.bteconosur.storage.ObjectAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface SqlObjectAdapter<T> extends ObjectAdapter<T, ResultSet, QueryStatement> {

    List<T> adaptAll(ResultSet resultSet) throws SQLException;

}
