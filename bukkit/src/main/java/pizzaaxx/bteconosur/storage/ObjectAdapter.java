package pizzaaxx.bteconosur.storage;

import java.sql.SQLException;

public interface ObjectAdapter<T, O, I> {

    T adapt(O out) throws SQLException;

    I write(T object);

}
