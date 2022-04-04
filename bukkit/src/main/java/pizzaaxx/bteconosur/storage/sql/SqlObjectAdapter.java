package pizzaaxx.bteconosur.storage.sql;

import pizzaaxx.bteconosur.storage.ObjectAdapter;

import java.sql.ResultSet;

public interface SqlObjectAdapter<O> extends ObjectAdapter<O, ResultSet> {
}
