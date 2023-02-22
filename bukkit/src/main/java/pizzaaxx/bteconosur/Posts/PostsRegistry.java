package pizzaaxx.bteconosur.Posts;

import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PostsRegistry {

    private final BTEConoSur plugin;
    public final Map<String, String> idsFromChannelID = new HashMap<>();

    public PostsRegistry(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public void init() throws SQLException {
        ResultSet set = plugin.getSqlManager().select(
                "posts",
                new SQLColumnSet("id", "channel_id"),
                new SQLANDConditionSet()
        ).retrieve();

        while (set.next()) {
            idsFromChannelID.put(set.getString("channel_id"), set.getString("id"));
        }
    }
}
