package pizzaaxx.bteconosur.projects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.SQLResult;
import com.github.PeterMassmann.Values.SQLValue;
import com.github.PeterMassmann.Values.SQLValuesSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.countries.Country;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplay;
import pizzaaxx.bteconosur.player.scoreboard.ScoreboardDisplayProvider;
import pizzaaxx.bteconosur.projects.selectors.region.ProjectRegionSelector;
import pizzaaxx.bteconosur.utils.SQLUtils;
import pizzaaxx.bteconosur.utils.StringUtils;
import pizzaaxx.bteconosur.utils.registry.BaseRegistry;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.format.NamedTextColor.DARK_RED;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static pizzaaxx.bteconosur.utils.ChatUtils.DARK_GRAY;

public class ProjectsRegistry extends BaseRegistry<Project, String> implements ScoreboardDisplayProvider {

    private final BTEConoSurPlugin plugin;
    private final Map<String, Polygon> regions = new HashMap<>();
    private final Map<String, Set<UUID>> members = new HashMap<>();

    public ProjectsRegistry(BTEConoSurPlugin plugin) {
        super(
                plugin,
                () -> {
                    List<String> ids = new ArrayList<>();
                    try (SQLResult result = plugin.getSqlManager().select(
                            "projects",
                            new SQLColumnSet("id"),
                            new SQLANDConditionSet()
                    ).retrieve()) {
                        ResultSet set = result.getResultSet();
                        while (set.next()) {
                            ids.add(set.getString("id"));
                        }
                    } catch (SQLException e) {
                        plugin.error("Could not load projects registry.");
                    }
                    return ids;
                },
                id -> {
                    try {
                        return new Project(plugin, id);
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                        plugin.error("Error loading project instance. (ID: " + id + ")");
                    }
                    return null;
                },
                true
        );
        this.plugin = plugin;
    }

    public void init() throws SQLException, JsonProcessingException {
        try (SQLResult result = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet("ST_AsWKT(region) AS region", "members", "owner", "id"),
                new SQLANDConditionSet()
        ).retrieve()) {
            ResultSet set = result.getResultSet();
            while (set.next()) {
                Polygon polygon = SQLUtils.polygonFromWKT(set.getString("region"));
                regions.put(set.getString("id"), polygon);
                plugin.getRegionListener().registerRegion(
                        "project_" + set.getString("id"),
                        polygon
                );

                Set<UUID> members = new HashSet<>();
                if (set.getString("owner") != null) {
                    members.add(SQLUtils.uuidFromBytes(set.getBytes("owner")));
                }
                JsonNode membersNode = plugin.getJsonMapper().readTree(set.getString("members"));
                for (JsonNode memberNode : membersNode) {
                    members.add(UUID.fromString(memberNode.asText()));
                }
                this.members.put(set.getString("id"), members);
            }
        }
    }

    public String createProject(
            @NotNull Country country,
            int city,
            @NotNull ProjectType type,
            int points,
            Polygon region
    ) throws SQLException {
        String id = StringUtils.generateCode(
                6,
                this.ids,
                StringUtils.LOWER_CASE
        );
        plugin.getSqlManager().insert(
                "projects",
                new SQLValuesSet(
                        new SQLValue("id", id),
                        new SQLValue("country", country.getName()),
                        new SQLValue("city", city),
                        new SQLValue("type", type.getName()),
                        new SQLValue("points", points),
                        new SQLValue("region", region)
                )
        ).execute();
        this.registerID(id);
        this.regions.put(id, region);
        this.members.put(id, new HashSet<>());
        plugin.getRegionListener().registerRegion(
                "project_" + id,
                region
        );
        country.getLogs().sendMessage(
                ":clipboard: Proyecto de tipo " + type.getDisplayName() + " creado con la ID `" + id + "`."
        ).queue();
        return id;
    }

    public void deleteProject() {

    }

    public boolean canBuildAt(UUID uuid, double x, double z) {
        return regions.entrySet().stream()
                .filter(
                        entry -> entry.getValue().contains(new GeometryFactory().createPoint(new Coordinate(x, z)))
                ).anyMatch(
                        entry -> members.get(entry.getKey()).contains(uuid) && !this.get(entry.getKey()).isPending()
                );
    }

    public Set<String> getProjectsAt(@NotNull Location location, ProjectRegionSelector... selectors) {
        return this.getProjectsAt(
                location.getX(),
                location.getZ(),
                selectors
        );
    }

    public Set<String> getProjectsAt(double x, double z, ProjectRegionSelector... selectors) {
        return regions.entrySet().stream()
                .filter(
                        entry -> {
                            if (!entry.getValue().contains(new GeometryFactory().createPoint(new Coordinate(x, z)))) {
                                return false;
                            }
                            for (ProjectRegionSelector selector : selectors) {
                                if (!selector.check(this.get(entry.getKey()))) {
                                    return false;
                                }
                            }
                            return true;
                        }
                ).map(
                        Map.Entry::getKey
                ).collect(
                        Collectors.toSet()
                );
    }

    @Override
    public ScoreboardDisplay getDisplay(Player player) {
        for (Map.Entry<String, Polygon> entry : regions.entrySet()) {
            if (entry.getValue().contains(
                    new GeometryFactory().createPoint(
                            new Coordinate(
                                    player.getLocation().getX(),
                                    player.getLocation().getZ()
                            )
                    )
            )) {
                return this.get(entry.getKey());
            }
        }
        ScoreboardDisplayProvider provider = this;
        return new ScoreboardDisplay() {
            @Override
            public Component getTitle() {
                return Component.text(StringUtils.transformToSmallCapital("PROYECTO"), Style.style(DARK_RED, TextDecoration.BOLD));
            }

            @Override
            public List<Component> getLines() {
                return List.of(
                        Component.text("◆")
                                .append(Component.text("                                 ", Style.style(TextDecoration.STRIKETHROUGH)))
                                .append(Component.text("◆"))
                                .color(TextColor.color(DARK_GRAY)),

                        Component.text(StringUtils.transformToSmallCapital("  No encontrado"), Style.style(RED, TextDecoration.BOLD)),
                        Component.text("◆")
                                .append(Component.text("                                 ", Style.style(TextDecoration.STRIKETHROUGH)))
                                .append(Component.text("◆"))
                                .color(TextColor.color(DARK_GRAY))
                );
            }

            @Override
            public ScoreboardDisplayProvider getProvider() {
                return provider;
            }

            @Override
            public boolean isSavable() {
                return true;
            }
        };
    }

    @Override
    public String getIdentifier() {
        return "project";
    }
}
