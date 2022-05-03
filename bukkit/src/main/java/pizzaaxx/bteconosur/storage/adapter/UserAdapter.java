package pizzaaxx.bteconosur.storage.adapter;

import pizzaaxx.bteconosur.server.player.ServerPlayer;
import pizzaaxx.bteconosur.storage.sql.QueryStatement;
import pizzaaxx.bteconosur.storage.sql.SqlObjectAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserAdapter implements SqlObjectAdapter<ServerPlayer> {

    @Override
    public ServerPlayer adapt(ResultSet out) throws SQLException {

        if (!out.next()) {
            return null;
        }

        String id = out.getString("id");
        boolean hide = out.getBoolean("hide");
        boolean linked = out.getBoolean("linked");
        int points = out.getInt("points");

        return new ServerPlayer();
    }

    @Override
    public QueryStatement write(ServerPlayer object) {
        return null;
    }

    @Override
    public List<ServerPlayer> adaptAll(ResultSet resultSet) throws SQLException {
        return null;
    }

}
