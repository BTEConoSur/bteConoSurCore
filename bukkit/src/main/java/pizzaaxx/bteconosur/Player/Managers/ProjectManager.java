package pizzaaxx.bteconosur.Player.Managers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.Projects.SQLSelectors.ProjectSQLSelector;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLContainedCondition;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProjectManager {

    private final BTEConoSur plugin;
    private final ServerPlayer serverPlayer;
    private final Set<Country> adminPermission;
    private final Set<String> ids;
    private final Map<Country, Integer> finished;
    private final Map<Country, Integer> points;

    public ProjectManager(@NotNull BTEConoSur plugin, @NotNull ServerPlayer serverPlayer) throws SQLException, JsonProcessingException {
        this.plugin = plugin;
        this.serverPlayer = serverPlayer;

        ResultSet set = plugin.getSqlManager().select(
                "project_managers",
                new SQLColumnSet(
                        "*"
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", serverPlayer.getUUID()
                        )
                )
        ).retrieve();

        if (set.next()) {
            adminPermission = new HashSet<>();
            Set<String> adminRaw = plugin.getJSONMapper().readValue(set.getString("admin"), HashSet.class);
            for (String name : adminRaw) {
                adminPermission.add(plugin.getCountryManager().get(name));
            }
            ids = plugin.getJSONMapper().readValue(set.getString("projects"), HashSet.class);

            finished = new HashMap<>();
            Map<String, Object> finishedRaw = plugin.getJSONMapper().readValue(set.getString("finished_projects"), HashMap.class);
            for (Map.Entry<String, Object> entry : finishedRaw.entrySet()) {
                finished.put(plugin.getCountryManager().get(entry.getKey()), (Integer) entry.getValue());
            }

            points = new HashMap<>();
            Map<String, Object> pointsRaw = plugin.getJSONMapper().readValue(set.getString("points"), HashMap.class);
            for (Map.Entry<String, Object> entry : pointsRaw.entrySet()) {
                points.put(plugin.getCountryManager().get(entry.getKey()), (Integer) entry.getValue());
            }
        } else {
            plugin.getSqlManager().insert(
                    "project_managers",
                    new SQLValuesSet(
                            new SQLValue(
                                    "uuid", serverPlayer.getUUID()
                            )
                    )
            ).execute();
            this.adminPermission = new HashSet<>();
            this.ids = new HashSet<>();
            this.points = new HashMap<>();
            this.finished = new HashMap<>();
        }
    }

    public Set<String> getProjects(ProjectSQLSelector... selectors) throws SQLException {
        if (ids.isEmpty()) {
            return new HashSet<>();
        }

        SQLANDConditionSet conditionSet = new SQLANDConditionSet(
                new SQLContainedCondition<>(
                        "id",
                        ids,
                        true
                )
        );
        for (ProjectSQLSelector selector : selectors) {
            conditionSet.addCondition(selector.getCondition());
        }
        ResultSet set = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet(
                        "id"
                ),
                conditionSet
        ).retrieve();

        Set<String> result = new HashSet<>();
        while (set.next()) {
            result.add(set.getString("id"));
        }
        return result;
    }

    public int getFinishedProjects(Country country) {
        return finished.getOrDefault(country, 0);
    }

    public int getPoints(Country country) {
        return points.getOrDefault(country, 0);
    }

    public boolean hasAdminPermission(Country country) {
        return adminPermission.contains(country);
    }

    public void addPoints(Country country, int points) throws SQLException {
        int actual = this.points.getOrDefault(country, 0);
        this.points.put(country, actual + points);
        plugin.getSqlManager().update(
                "project_managers",
                new SQLValuesSet(
                        new SQLValue(
                                "points", this.points
                        )
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", serverPlayer.getUUID()
                        )
                )
        ).execute();
        country.getLogsChannel().sendMessage(
                ":chart_with_upwards_trend: Se han añadido **" + points + "** puntos a **" + serverPlayer.getName() + "**."
        ).queue();
    }

    public void addProject(@NotNull Project project) throws SQLException {
        this.ids.add(project.getId());
        plugin.getSqlManager().update(
                "project_managers",
                new SQLValuesSet(
                        new SQLValue(
                                "projects", this.ids
                        )
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", serverPlayer.getUUID()
                        )
                )
        ).execute();
    }

    public void removeProject(@NotNull Project project) throws SQLException {
        this.ids.remove(project.getId());
        plugin.getSqlManager().update(
                "project_managers",
                new SQLValuesSet(
                        new SQLValue(
                                "projects", this.ids
                        )
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", serverPlayer.getUUID()
                        )
                )
        ).execute();
    }

    public void addFinished(Country country) throws SQLException {
        int actual = this.points.getOrDefault(country, 0);
        this.finished.put(country, actual + 1);
        plugin.getSqlManager().update(
                "project_managers",
                new SQLValuesSet(
                        new SQLValue(
                                "finished_projects", this.finished
                        )
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "uuid", "=", serverPlayer.getUUID()
                        )
                )
        ).execute();
    }

}
