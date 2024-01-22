package pizzaaxx.bteconosur.utils;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.util.net.HttpRequest;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.terra.TerraCoords;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SatMapHandler {

    public static class SatMapPolygon {
        private final List<TerraCoords> points;
        private final String color;

        public SatMapPolygon(@NotNull List<BlockVector2> points, String color) {
            this.points = new ArrayList<>();
            for (BlockVector2 vector : points) {
                this.points.add(TerraCoords.fromBlockVector(vector));
            }
            this.color = color;
        }

        public SatMapPolygon(@NotNull List<BlockVector2> points) {
            this.points = new ArrayList<>();
            for (BlockVector2 vector : points) {
                this.points.add(TerraCoords.fromBlockVector(vector));
            }
            this.color = "5882fa";
        }

        public List<TerraCoords> getPoints() {
            return points;
        }

        public String getColor() {
            return color;
        }
    }

    private final String key;

    public String getKey() {
        return key;
    }

    public SatMapHandler(@NotNull BTEConoSurPlugin plugin) throws IOException {

        File configFile = new File(plugin.getDataFolder(), "config.json");
        this.key = plugin.getJsonMapper().readTree(configFile).path("mapquest_key").asText();
    }

    public String getMap(SatMapPolygon... polygons) {
        return this.getMap(Arrays.asList(polygons));
    }

    public String getMap(@NotNull Iterable<SatMapPolygon> polygons) {
        StringBuilder builder = new StringBuilder();
        builder.append("https://www.mapquestapi.com/staticmap/v5/map?type=sat&size=1920,1080&imagetype=png");
        builder.append("&key=").append(key);
        for (SatMapPolygon polygon : polygons) {
            StringBuilder shapeBuilder = new StringBuilder("&shape=");
            for (TerraCoords coord : polygon.points) {
                shapeBuilder.append(coord.getLat()).append(",").append(coord.getLon()).append("|");
            }
            shapeBuilder.append("|").append(polygon.points.get(0).getLat()).append(",").append(polygon.points.get(0).getLon());
            shapeBuilder.append("|fill:").append(polygon.getColor()).append("50");
            shapeBuilder.append("|border:").append(polygon.getColor());
            builder.append(shapeBuilder);
        }
        return builder.toString();
    }

    public HttpRequest getMapStream(SatMapPolygon... polygons) throws IOException {
        return this.getMapStream(Arrays.asList(polygons));
    }

    public HttpRequest getMapStream(@NotNull Iterable<SatMapPolygon> polygons) throws IOException {
        URL url = new URL(this.getMap(polygons));
        return HttpRequest.get(url).execute();
    }
}
