package pizzaaxx.bteconosur.player.projects;

import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLJSONArrayCondition;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.countries.Country;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.PlayerManager;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.projects.ProjectType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProjectsManager implements PlayerManager {

    private final BTEConoSurPlugin plugin;
    private final OfflineServerPlayer player;
    private final Set<String> projects = new HashSet<>();
    private final Map<ProjectType, Map<String, Integer>> finishedProjects = new HashMap<>();

    public ProjectsManager(BTEConoSurPlugin plugin, OfflineServerPlayer player) throws SQLException {
        this.plugin = plugin;
        this.player = player;

        try (ResultSet projectsSet = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet("id"),
                new SQLANDConditionSet(
                        new SQLJSONArrayCondition("members", player.getUUID()),
                        new SQLOperatorCondition("owner", "=", player.getUUID().toString())
                )
        ).retrieve().getResultSet()) {
            while (projectsSet.next()) {
                projects.add(projectsSet.getString("id"));
            }
        }

        try (ResultSet finishedSet = plugin.getSqlManager().select(
                "finished_projects",
                new SQLColumnSet("type", "id", "points"),
                new SQLANDConditionSet(
                        new SQLJSONArrayCondition("members", player.getUUID()),
                        new SQLOperatorCondition("owner", "=", player.getUUID().toString())
                )
        ).retrieve().getResultSet()) {
            while (finishedSet.next()) {
                ProjectType type = Country.PROJECT_TYPES.get(finishedSet.getString("type"));
                if (type == null) {
                    plugin.error("Project type not found: " + finishedSet.getString("type"));
                    continue;
                }
                finishedProjects.computeIfAbsent(type, t -> new HashMap<>()).put(finishedSet.getString("id"), finishedSet.getInt("points"));
            }
        }
    }

    public int getPoints() {
        return finishedProjects.values().stream()
                .mapToInt(stringIntegerMap -> stringIntegerMap.values().stream().mapToInt(Integer::intValue).sum())
                .sum();
    }

    public int getPoints(Country country) {
        return finishedProjects.entrySet().stream()
                .filter(entry -> entry.getKey().getCountry().equals(country))
                .mapToInt(entry -> entry.getValue().values().stream().mapToInt(Integer::intValue).sum())
                .sum();
    }

    public int getFinishedProjects() {
        return finishedProjects.values().stream()
                .mapToInt(Map::size)
                .sum();
    }

    public int getFinishedProjects(Country country) {
        return finishedProjects.entrySet().stream()
                .filter(entry -> entry.getKey().getCountry().equals(country))
                .mapToInt(entry -> entry.getValue().size())
                .sum();
    }

    public Set<String> getProjects() {
        return projects;
    }

    public void addProject(@NotNull Project project) {
        projects.add(project.getID());
    }

    public void removeProject(@NotNull Project project) {
        projects.remove(project.getID());
    }

    public void addFinishedProject() {

    }

    @Override
    public void saveValue(String key, Object value) throws SQLException {

    }

    public enum BuilderRank {
        NONE,
        APPLIER,
        BUILDER
    }

    public BuilderRank getBuilderRank() {
        if (!finishedProjects.isEmpty()) {
            return BuilderRank.BUILDER;
        } else if (!projects.isEmpty()) {
            return BuilderRank.APPLIER;
        } else {
            return BuilderRank.NONE;
        }
    }
}
