package pizzaaxx.bteconosur.WorldEdit.Assets;

import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Registry.Registry;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AssetsRegistry implements Registry<String, Asset> {

    private final BTEConoSur plugin;

    private final Map<String, Asset> assetsCache = new HashMap<>();
    private final Map<String, Long> deletionCache = new HashMap<>();

    private final Map<String, String> idsAndNames = new HashMap<>();

    public AssetsRegistry(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public void init() throws SQLException {
        ResultSet set = plugin.getSqlManager().select(
                "assets",
                new SQLColumnSet(
                        "id"
                ),
                new SQLConditionSet()
        ).retrieve();

        while (set.next()) {
            idsAndNames.put(set.getString("id"), set.getString("names"));
        }
    }

    @Override
    public boolean isLoaded(String id) {
        return false;
    }

    @Override
    public void load(String id) {

    }

    @Override
    public void unload(String id) {

    }

    @Override
    public boolean exists(String id) {
        return false;
    }

    @Override
    public Asset get(String id) {
        return null;
    }

    @Override
    public Set<String> getIds() {
        return idsAndNames.keySet();
    }

    public Set<String> getNames() {
        return new HashSet<>(idsAndNames.values());
    }

    @Override
    public void scheduleDeletion(String id) {

    }
}
