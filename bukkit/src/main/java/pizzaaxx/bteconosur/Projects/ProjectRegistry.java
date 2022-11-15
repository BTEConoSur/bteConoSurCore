package pizzaaxx.bteconosur.Projects;

import org.bukkit.scheduler.BukkitRunnable;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLConditionSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProjectRegistry {

    private final Map<String, Project> projectsCache = new HashMap<>();
    private final Map<String, Long> deletionCache = new HashMap<>();
    private final Set<String> ids = new HashSet<>();

    private final BTEConoSur plugin;

    public ProjectRegistry(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public void init() throws SQLException {
        ResultSet set = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet(
                        "id"
                ),
                new SQLConditionSet()
        ).retrieve();

        while (set.next()) {
            ids.add(set.getString("id"));
        }
    }

    public boolean exists(String id) {
        return ids.contains(id);
    }

    public void load(String id) {
        if (this.exists(id)) {
            projectsCache.put(id, new Project());
        }
    }

    public void unload(String id) {
        if (this.isLoaded(id)) {
            deletionCache.remove(id);
            projectsCache.remove(id);
        }
    }

    public Project get(String id) {
        if (!this.isLoaded(id)) {
            load(id);
        }
        this.scheduleDeletion(id);
        return projectsCache.get(id);
    }

    public boolean isLoaded(String id) {
        return projectsCache.containsKey(id);
    }

    private void scheduleDeletion(String id) {
        if (this.isLoaded(id)) {
            deletionCache.put(id, System.currentTimeMillis());
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (deletionCache.containsKey(id) && System.currentTimeMillis() - deletionCache.get(id) > 550000) {
                        unload(id);
                    }
                }
            };
            runnable.runTaskLaterAsynchronously(plugin, 12000);
        }
    }
}
