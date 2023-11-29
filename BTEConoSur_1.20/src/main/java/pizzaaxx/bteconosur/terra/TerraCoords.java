package pizzaaxx.bteconosur.terra;

import com.sk89q.worldedit.math.BlockVector2;
import org.jetbrains.annotations.NotNull;

public class TerraCoords {

    private final double x;
    private final double z;
    private final double lon;
    private final double lat;

    public TerraCoords(double x, double z, double lon, double lat) {
        this.x = x;
        this.z = z;
        this.lon = lon;
        this.lat = lat;
    }

    public static @NotNull TerraCoords fromGeo(double lon, double lat) {
        double[] mcCoords = TerraConnector.fromGeo(lon, lat);
        return new TerraCoords(
                mcCoords[0],
                mcCoords[1],
                lon,
                lat
        );
    }

    public static @NotNull TerraCoords fromMc(double x, double z) {
        double[] geoCoords = TerraConnector.toGeo(x, z);
        return new TerraCoords(
                x,
                z,
                geoCoords[0],
                geoCoords[1]
        );
    }

    public static @NotNull TerraCoords fromBlockVector(BlockVector2 vector) {
        return TerraCoords.fromMc(
                vector.getX(),
                vector.getZ()
        );
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }
}
