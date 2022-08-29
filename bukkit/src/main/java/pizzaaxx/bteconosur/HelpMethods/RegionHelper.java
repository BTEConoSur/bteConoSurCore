package pizzaaxx.bteconosur.HelpMethods;

import com.sk89q.worldedit.BlockVector2D;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.coords.Coords2D;

import java.util.List;

public class RegionHelper {

    @Contract("_ -> new")
    public static @NotNull Coords2D getAverageCoordinate(@NotNull List<BlockVector2D> points) {

        double xMin = points.get(0).getX();
        double xMax = points.get(0).getX();
        double zMin = points.get(0).getZ();
        double zMax = points.get(0).getZ();

        for (BlockVector2D point : points) {

            if (point.getX() < xMin) {
                xMin = point.getX();
            }
            if (point.getX() > xMax) {
                xMax = point.getX();
            }
            if (point.getZ() < zMin) {
                zMin = point.getZ();
            }
            if (point.getZ() > zMax) {
                zMax = point.getZ();
            }
        }

        double avgX = (xMin + xMax) / 2;
        double avgZ = (zMin + zMax) / 2;

        return new Coords2D(new BlockVector2D(avgX, avgZ));

    }

}
