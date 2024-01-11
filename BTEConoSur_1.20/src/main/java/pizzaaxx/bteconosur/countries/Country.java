package pizzaaxx.bteconosur.countries;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.JSONParsable;
import com.github.PeterMassmann.SQLManager;
import com.github.PeterMassmann.SQLResult;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.locationtech.jts.geom.*;
import org.opengis.feature.simple.SimpleFeature;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.cities.City;
import pizzaaxx.bteconosur.projects.ProjectType;
import pizzaaxx.bteconosur.terra.TerraCoords;
import pizzaaxx.bteconosur.utils.registry.BaseRegistry;
import pizzaaxx.bteconosur.utils.registry.RegistrableEntity;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pizzaaxx.bteconosur.discord.DiscordConnector.BOT;

public class Country extends BaseRegistry<City, Integer> implements RegistrableEntity<String>, JSONParsable {

    public static Map<String, ProjectType> PROJECT_TYPES = new HashMap<>();

    private final BTEConoSurPlugin plugin;
    private final String name;
    private final String displayName;
    private final String abbreviation;
    private final String guildId;
    private final String showcaseId;
    private final String chatId;
    private final String logsId;
    private final String requestsId;
    private final String iconUrl;
    private final Location spawn;
    private final List<ProjectType> projectTypes;
    private final String headValue;
    private final String emoji;
    private final Component chatPrefix;
    private final Component tabPrefix;
    public final Map<Integer, MultiPolygon> cityRegions = new HashMap<>();

    public Country(@NotNull BTEConoSurPlugin plugin, String name) throws SQLException, IOException {
        super(
                plugin,
                () -> {
                    Map<Integer, SimpleFeature> cityFeatures = plugin.getShapefile(name);
                    return cityFeatures.keySet();
                },
                id -> new City(plugin, name, id),
                false
        );
        this.plugin = plugin;
        this.name = name;
        try (SQLResult result = plugin.getSqlManager().select(
                "countries",
                new SQLColumnSet("*"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition("name", "=", name)
                )
        ).retrieve()) {
            ResultSet set = result.getResultSet();
            if (!set.next()) {
                throw new SQLException("Country not found.");
            }

            this.displayName = set.getString("display_name");
            this.abbreviation = set.getString("abbreviation");
            this.guildId = set.getString("guild_id");
            this.showcaseId = set.getString("showcase_id");
            this.chatId = set.getString("chat_id");
            this.logsId = set.getString("logs_id");
            this.requestsId = set.getString("requests_id");
            this.iconUrl = set.getString("icon_url");

            JsonNode spawnNode = plugin.getJsonMapper().readTree(set.getString("spawn_point"));
            this.spawn = new Location(
                    plugin.getWorld(spawnNode.path("y").asDouble()),
                    spawnNode.path("x").asDouble(),
                    spawnNode.path("y").asDouble(),
                    spawnNode.path("z").asDouble()
            );

            this.projectTypes = new ArrayList<>();
            JsonNode projectTypesNode = plugin.getJsonMapper().readTree(set.getString("project_types"));
            for (JsonNode typeNode : projectTypesNode) {
                ProjectType type = new ProjectType(plugin, this, typeNode.asText());
                this.projectTypes.add(
                        type
                );
                PROJECT_TYPES.put(typeNode.asText(), type);
            }

            this.headValue = set.getString("head_value");
            this.emoji = set.getString("emoji");
            this.chatPrefix = LegacyComponentSerializer.legacyAmpersand().deserialize(set.getString("chat_prefix"));
            this.tabPrefix = LegacyComponentSerializer.legacyAmpersand().deserialize(set.getString("tab_prefix"));

            Map<Integer, SimpleFeature> featureMap = plugin.getShapefile(name);
            featureMap.forEach((key, value) -> {
                MultiPolygon polygon = (MultiPolygon) value.getDefaultGeometry();
                polygon.apply(
                        (CoordinateFilter) coord -> {
                            TerraCoords coords = TerraCoords.fromGeo(
                                    coord.x,
                                    coord.y
                            );
                            coord.setX(coords.getX());
                            coord.setY(coords.getZ());
                        }
                );
                polygon.geometryChanged();
                plugin.getRegionListener().registerRegion(
                        "city_" + name + "_" + key,
                        polygon
                );
                cityRegions.put(key, polygon);
            });
        }
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull String getDisplayName() {
        return displayName;
    }

    public @NotNull String getAbbreviation() {
        return abbreviation;
    }

    public @NotNull String getGuildID() {
        return guildId;
    }

    public Guild getGuild() {
        return BOT.getGuildById(guildId);
    }

    public @NotNull String getShowcaseID() {
        return showcaseId;
    }

    public TextChannel getShowcase() {
        return BOT.getTextChannelById(showcaseId);
    }

    public @NotNull String getChatID() {
        return chatId;
    }

    public TextChannel getChat() {
        return BOT.getTextChannelById(chatId);
    }

    public @NotNull String getLogsID() {
        return logsId;
    }

    public TextChannel getLogs() {
        return BOT.getTextChannelById(logsId);
    }

    public @NotNull String getRequestsID() {
        return requestsId;
    }

    public TextChannel getRequests() {
        return BOT.getTextChannelById(requestsId);
    }

    public @NotNull String getIconURL() {
        return iconUrl;
    }

    public @NotNull Location getSpawn() {
        return spawn;
    }

    public @NotNull List<ProjectType> getProjectTypes() {
        return projectTypes;
    }

    @Nullable
    public ProjectType getProjectType(String name) {
        for (ProjectType type : projectTypes) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public @NotNull String getHeadValue() {
        return headValue;
    }

    public @NotNull String getEmoji() {
        return emoji;
    }

    public @NotNull Component getChatPrefix() {
        return chatPrefix;
    }

    public @NotNull Component getTabPrefix() {
        return tabPrefix;
    }

    public boolean contains(double x, double z) {
        Point point = new GeometryFactory().createPoint(new Coordinate(x, z));
        for (Map.Entry<Integer, MultiPolygon> entry : cityRegions.entrySet()) {
            if (entry.getValue().contains(point)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public City getCityAt(double x, double z) {
        Point point = new GeometryFactory().createPoint(
                new Coordinate(
                        x,
                        z
                )
        );
        for (Map.Entry<Integer, MultiPolygon> entry : cityRegions.entrySet()) {
            if (entry.getValue().contains(point)) {
                return this.get(entry.getKey());
            }
        }
        return null;
    }

    @Override
    public String getID() {
        return name;
    }

    @Override
    public void disconnected() {

    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Country c) {
            return c.name.equals(this.name);
        }
        return false;
    }

    @Override
    public String getJSON(@NotNull SQLManager sqlManager, boolean b) {
        return sqlManager.parse(this.name, b);
    }
}
