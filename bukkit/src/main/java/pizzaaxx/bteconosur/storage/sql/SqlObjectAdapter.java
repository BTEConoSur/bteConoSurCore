package pizzaaxx.bteconosur.storage.sql;

import pizzaaxx.bteconosur.storage.ObjectAdapter;

import java.sql.ResultSet;

public interface SqlObjectAdapter<T> extends ObjectAdapter<T, ResultSet, QueryStatement> {
}
