package pizzaaxx.bteconosur.Cities;

import clipper2.Clipper;
import clipper2.core.FillRule;
import clipper2.core.Path64;
import clipper2.core.Paths64;
import clipper2.core.Point64;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mysql.cj.protocol.ResultStreamer;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.Actions.DeleteUrbanAreaCityAction;
import pizzaaxx.bteconosur.Cities.Actions.RedefineRegionCityAction;
import pizzaaxx.bteconosur.Cities.Actions.SetDisplayNameCityAction;
import pizzaaxx.bteconosur.Cities.Actions.SetUrbanAreaCityAction;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLJSONArrayCondition;
import pizzaaxx.bteconosur.SQL.Conditions.SQLNullCondition;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.JSONParsable;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Scoreboard.ScoreboardDisplay;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class City implements JSONParsable, ScoreboardDisplay {

    private final BTEConoSur plugin;
    private final String name;
    private final String displayName;
    private final Country country;
    private final ProtectedRegion region;
    private ProtectedRegion urbanRegion;
    private int finishedArea;

    public City(@NotNull BTEConoSur plugin, String name) throws SQLException, JsonProcessingException {
        this.plugin = plugin;
        ResultSet set = plugin.getSqlManager().select(
                "cities",
                new SQLColumnSet(
                        "*"
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "name", "=", name
                        )
                )
        ).retrieve();

        if (set.next()) {
            this.name = set.getString("name");
            this.displayName = set.getString("display_name");
            this.country = plugin.getCountryManager().get(set.getString("country"));
            if (plugin.getRegionManager().hasRegion("city_" + this.name + "_urban")) {
                this.urbanRegion = plugin.getRegionManager().getRegion("city_" + this.name + "_urban");
            }
            this.region = plugin.getRegionManager().getRegion("city_" + this.name);
            this.finishedArea = set.getInt("finished_area");
        } else {
            throw new IllegalArgumentException();
        }
    }

    public BTEConoSur getPlugin() {
        return plugin;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Country getCountry() {
        return country;
    }

    public int getAvailableProjectsAmount() throws SQLException {
        ResultSet set = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet(
                        "COUNT(id) as count"
                ),
                new SQLANDConditionSet(
                        new SQLJSONArrayCondition(
                                "cities", this.name
                        ),
                        new SQLNullCondition(
                                "owner", true
                        )
                )
        ).retrieve();

        set.next();
        return set.getInt("count");
    }

    public int getClaimedProjectsAmount() throws SQLException {
        ResultSet set = plugin.getSqlManager().select(
                "projects",
                new SQLColumnSet(
                        "COUNT(id) as count"
                ),
                new SQLANDConditionSet(
                        new SQLJSONArrayCondition(
                                "cities", this.name
                        ),
                        new SQLNullCondition(
                                "owner", false
                        )
                )
        ).retrieve();

        set.next();
        return set.getInt("count");
    }

    public int getFinishedProjectsAmount() throws SQLException {
        ResultSet set = plugin.getSqlManager().select(
                "finished_projects",
                new SQLColumnSet(
                        "COUNT(id) as count"
                ),
                new SQLANDConditionSet(
                        new SQLJSONArrayCondition(
                                "cities", this.name
                        )
                )
        ).retrieve();

        set.next();
        return set.getInt("count");
    }

    public List<String> getPosts() throws SQLException {
        List<String> ids = new ArrayList<>();
        ResultSet set = plugin.getSqlManager().select(
                "posts",
                new SQLColumnSet(
                        "id"
                ),
                new SQLANDConditionSet(
                        new SQLJSONArrayCondition(
                                "cities", this.name
                        )
                )
        ).retrieve();

        while (set.next()) {
            ids.add(set.getString("id"));
        }

        return ids;
    }

    public boolean hasUrbanArea() {
        return urbanRegion != null;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public ProtectedRegion getUrbanRegion() {
        return urbanRegion;
    }

    public SetDisplayNameCityAction setDisplayName(@NotNull String displayName) {
        return new SetDisplayNameCityAction(
                plugin,
                name,
                displayName
        );
    }

    public SetUrbanAreaCityAction setUrbanArea(List<BlockVector2D> points) {
        return new SetUrbanAreaCityAction(
                plugin,
                name,
                points
        );
    }

    public DeleteUrbanAreaCityAction deleteUrbanArea() {
        return new DeleteUrbanAreaCityAction(
                plugin,
                name
        );
    }

    public RedefineRegionCityAction redefine(List<BlockVector2D> points) {
        return new RedefineRegionCityAction(
                plugin,
                name,
                points
        );
    }

    public int getFinishedArea() {
        return finishedArea;
    }

    public void updateFinishedArea() throws SQLException, JsonProcessingException {

        ResultSet set = plugin.getSqlManager().select(
                "finished_projects",
                new SQLColumnSet(
                        "region_points"
                ),
                new SQLANDConditionSet(
                        new SQLJSONArrayCondition(
                                "cities", this.name
                        )
                )
        ).retrieve();

        Paths64 polygons = new Paths64();
        while (set.next()) {

            Path64 polygon = new Path64();

            List<BlockVector2D> vectors = new ArrayList<>();
            List<Object> coordsRaw = plugin.getJSONMapper().readValue(set.getString("region_points"), ArrayList.class);
            for (Object obj : coordsRaw) {
                Map<String, Double> coords = (Map<String, Double>) obj;
                vectors.add(new BlockVector2D(coords.get("x"), coords.get("z")));
            }

            vectors.forEach(vector -> {
                Point64 point = new Point64();
                point.x = (long) vector.getX();
                point.y = (long) vector.getZ();
                polygon.add(point);
            });

            BlockVector2D vector = vectors.get(0);
            Point64 point = new Point64();
            point.x = (long) vector.getX();
            point.y = (long) vector.getZ();
            polygon.add(point);

            polygons.add(polygon);
        }
        Paths64 result = Clipper.Union(polygons, FillRule.EvenOdd);

        Paths64 cityPoly = new Paths64();
        Path64 poly = new Path64();
        for (BlockVector2D vector : this.region.getPoints()) {
            Point64 point = new Point64();
            point.x = (long) vector.getX();
            point.y = (long) vector.getZ();
            poly.add(point);
        }

        BlockVector2D vector = this.getRegion().getPoints().get(0);
        Point64 point = new Point64();
        point.x = (long) vector.getX();
        point.y = (long) vector.getZ();
        poly.add(point);
        cityPoly.add(poly);

        Paths64 finalResult = Clipper.Intersect(result, cityPoly, FillRule.NonZero);

        int area = (int) Clipper.Area(finalResult);

        plugin.getSqlManager().update(
                "cities",
                new SQLValuesSet(
                        new SQLValue(
                                "finished_area", area
                        )
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "name", "=", this.name
                        )
                )
        ).execute();

        this.finishedArea = area;
    }


    @Override
    public String getJSON(boolean insideJSON) {
        return (insideJSON?"\"":"'") + this.name + (insideJSON?"\"":"'");
    }

    @Override
    public boolean equals(Object obj) {

        if (getClass() != obj.getClass()) {
            return false;
        }

        City city = (City) obj;
        return this.name.equals(city.name);
    }

    @Override
    public String getScoreboardTitle() {
        return "§a§l" + this.displayName;
    }

    @Override
    public List<String> getScoreboardLines() {

        List<String> lines = new ArrayList<>();

        double finishedArea = this.finishedArea / 1000000.0;
        double totalArea = new Polygonal2DRegion(plugin.getWorldEditWorld(), region.getPoints(), 100, 100).getArea() / 1000000.0;
        double percentage = (finishedArea / totalArea) * 100;

        DecimalFormat format = new DecimalFormat("#.##");

        lines.add("§fÁrea terminada:§7 " + format.format(finishedArea) + "km² (" + format.format(Math.min(percentage, 100)) + "%)");

        try {
            lines.add("§fProyectos terminados: §7" + this.getFinishedProjectsAmount());

            lines.add("§fProyectos activos: §7" + this.getClaimedProjectsAmount());

            lines.add("§fProyectos disponibles: §7" + this.getAvailableProjectsAmount());
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return lines;
    }

    @Override
    public String getScoreboardType() {
        return "city";
    }
}
