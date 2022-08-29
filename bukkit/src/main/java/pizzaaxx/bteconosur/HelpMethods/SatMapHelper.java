package pizzaaxx.bteconosur.HelpMethods;

import com.sk89q.worldedit.BlockVector2D;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.helper.Pair;

import java.util.List;

import static pizzaaxx.bteconosur.BteConoSur.key;

public class SatMapHelper {

    @SafeVarargs
    @Contract(pure = true)
    public static @NotNull String getURL(Pair<List<BlockVector2D>, String> @NotNull ... polygons) {

        StringBuilder result = new StringBuilder("https://open.mapquestapi.com/staticmap/v5/map?key=" + key + "&type=sat&size=1920,1080&imagetype=png");

        for (Pair<List<BlockVector2D>, String> polygon : polygons) {

            result.append("&shape=");

            List<BlockVector2D> points = polygon.getKey();

            for (BlockVector2D point : points) {

                Coords2D geoCoord = new Coords2D(point);

                result.append(geoCoord.getLat()).append(",").append(geoCoord.getLon()).append("|");

            }

            Coords2D geoCoord = new Coords2D(points.get(0));
            result.append(geoCoord.getLat()).append(",").append(geoCoord.getLon()).append("|");

            result.append("fill:").append(polygon.getValue());

        }

        return result.toString();

    }

}
