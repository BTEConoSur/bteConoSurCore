package pizzaaxx.bteconosur.LegacyConversion;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Configuration.Configuration;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Posts.Post;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.Projects.ProjectTag;
import pizzaaxx.bteconosur.Projects.ProjectType;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Utils.CoordinatesUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

public class LegacyConverterCommand implements CommandExecutor {

    private final BTEConoSur plugin;

    public LegacyConverterCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        for (Map.Entry<String, ProtectedRegion> entry : plugin.getRegionManager().getRegions().entrySet()) {

            String regionName = entry.getKey();
            ProtectedRegion region = entry.getValue();

            Random random = new Random();

            if (regionName.matches("project_[a-z]{6}")) {

                String id = regionName.replace("project_", "");

                File file = new File(plugin.getDataFolder(), "projects/" + id + ".yml");

                if (!file.exists()) {
                    continue;
                }

                Configuration config = new Configuration(plugin, "projects/" + id);

                if (!config.contains("country") || !config.contains("difficulty")) {
                    continue;
                }

                UUID ownerUUID = null;
                if (config.contains("owner")) {
                    ownerUUID = UUID.fromString(config.getString("owner"));
                }

                Country country = plugin.getCountryManager().get(config.getString("country"));

                if (country == null) {
                    continue;
                }

                String legacyDifficulty = config.getString("difficulty");

                int typeIndex = (legacyDifficulty.equals("facil") ? 0 : (legacyDifficulty.equals("intermedio") ? 1 : 2));

                ProjectType type = country.getProjectType(country.getLegacyDifficulties().get(typeIndex));

                int projectPoints = type.getPointsOptions().get(random.nextInt(type.getPointsOptions().size()));

                boolean pending = config.getBoolean("pending", false);

                String name = config.getString("name", null);

                Set<UUID> members = region.getMembers().getUniqueIds();
                members.remove(ownerUUID);

                String legacyTag = config.getString("tag", null);

                ProjectTag tag = null;
                if (legacyTag != null) {
                    tag = ProjectTag.valueOf(legacyTag.replace("centros_comerciales", "shopping"));
                }

                Set<ProtectedRegion> cityRegions = new HashSet<>();
                for (String cityName : country.getCities()) {
                    cityRegions.add(plugin.getRegionManager().getRegion("city_" + cityName));
                }

                List<ProtectedRegion> intersectingCities = region.getIntersectingRegions(cityRegions);
                Set<City> cities = new HashSet<>();
                for (ProtectedRegion intersectingCity : intersectingCities) {
                    cities.add(plugin.getCityManager().get(intersectingCity.getId().replace("city_", "")));
                }

                for (UUID uuid : region.getMembers().getUniqueIds()) {

                    if (!plugin.getPlayerRegistry().hasPlayedBefore(uuid)) {

                        Configuration playerConfig = new Configuration(plugin, "playerData/" + uuid.toString());

                        String playerName = playerConfig.getString("name", "~");

                        Set<String> roles = new HashSet<>();
                        if (playerConfig.contains("primaryGroup")) {
                            String role = playerConfig.getString("primaryGroup");

                            if (role.equals("mod") || role.equals("admin")) {
                                roles.add(role);
                            }
                        }

                        Map<Country, Map<ProjectType, Double>> points = new HashMap<>();
                        ConfigurationSection pointsSection = playerConfig.getConfigurationSection("points");
                        for (String key : pointsSection.getKeys(false)) {
                            int legacyPoints = pointsSection.getInt(key);

                            Country c = plugin.getCountryManager().get(key);

                            int counterPoints = legacyPoints;

                            Map<ProjectType, Double> pointsMap = new HashMap<>();
                            int counter = 1;
                            int size = c.getProjectTypes().size();
                            for (ProjectType projectType : c.getProjectTypes()) {

                                if (counter == size) {
                                    pointsMap.put(projectType, (double) counterPoints);
                                    break;
                                }

                                int max = projectType.getPointsOptions().get(projectType.getPointsOptions().size() - 1);
                                if (counterPoints > max * 3) {
                                    pointsMap.put(projectType, (double) (max * 3));
                                    counterPoints -= max * 3;
                                } else {
                                    pointsMap.put(projectType, (double) counterPoints);
                                }
                            }
                            points.put(c, pointsMap);
                        }

                        Map<String, String> presets = new HashMap<>();
                        ConfigurationSection presetsSection = playerConfig.getConfigurationSection("presets");
                        for (String preset : presetsSection.getKeys(false)) {
                            presets.put(preset, presetsSection.getString(preset));
                        }

                        try {
                            plugin.getSqlManager().insert(
                                    "players",
                                    new SQLValuesSet(
                                            new SQLValue("uuid", uuid),
                                            new SQLValue("name", playerName),
                                            new SQLValue("roles", roles),
                                            new SQLValue("last_disconnected", 1683852998771L)
                                    )
                            ).execute();
                            plugin.getSqlManager().insert(
                                    "world_edit_managers",
                                    new SQLValuesSet(
                                            new SQLValue(
                                                    "uuid", uuid
                                            ),
                                            new SQLValue(
                                                    "presets", presets
                                            )
                                    )
                            ).execute();
                            if (!points.isEmpty()) {
                                plugin.getSqlManager().insert(
                                        "project_managers",
                                        new SQLValuesSet(
                                                new SQLValue("uuid", uuid),
                                                new SQLValue("points", points)
                                        )
                                ).execute();
                            }
                            if (playerConfig.contains("discord.name")) {
                                String dscName = playerConfig.getString("discord.name");
                                String dscDiscriminator = playerConfig.getString("discord.discriminator");
                                String dscID = playerConfig.getString("discord.id");
                                plugin.getSqlManager().insert(
                                        "discord_managers",
                                        new SQLValuesSet(
                                                new SQLValue("uuid", uuid),
                                                new SQLValue("id", dscID),
                                                new SQLValue("name", dscName),
                                                new SQLValue("discriminator", dscDiscriminator)
                                        )
                                ).execute();
                            }
                            plugin.getPlayerRegistry().registerUUID(uuid);
                            plugin.log("Created database registry for player with UUID §f" + uuid);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                    }

                }

                try {
                    plugin.getSqlManager().insert(
                            "projects",
                            new SQLValuesSet(
                                    new SQLValue("id", id),
                                    new SQLValue("name", name),
                                    new SQLValue("country", country),
                                    new SQLValue("cities", cities),
                                    new SQLValue("pending", pending),
                                    new SQLValue("type", type),
                                    new SQLValue("points", projectPoints),
                                    new SQLValue("members", members),
                                    new SQLValue("owner", ownerUUID),
                                    new SQLValue("tag", tag)
                            )
                    ).execute();
                    plugin.getProjectRegistry().registerID(id);
                    Project project = plugin.getProjectRegistry().get(id);
                    Post.createPost(
                            plugin,
                            project,
                            "Proyecto " + project.getId().toUpperCase(),
                            "N/A"

                    );
                    plugin.getTerramapHandler().drawPolygon(
                            CoordinatesUtils.getCoords2D(plugin, project.getRegionPoints()),
                            (ownerUUID == null ? new Color(78, 255, 71) : new Color(255, 200, 0)),
                            id
                    );
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        for (Country country : plugin.getCountryManager().getAllCountries()) {

            

        }

        return true;
    }
}
