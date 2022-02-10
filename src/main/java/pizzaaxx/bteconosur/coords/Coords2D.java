package pizzaaxx.bteconosur.coords;

import com.sk89q.worldedit.BlockVector2D;
import jdk.nashorn.internal.ir.Block;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import org.bukkit.Location;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;

public class Coords2D {

    public static GeographicProjection geographicProjection = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS).projection();

    private double x;
    private double z;
    private double lon;
    private double lat;

    // CONSTRUCTOR
    public Coords2D(Double lat, Double lon) {
        try {
            double[] mcCoords = geographicProjection.fromGeo(lon, lat);
            this.lon = lat;
            this.lat = lon;
            this.x = mcCoords[0];
            this.z = mcCoords[1];
        } catch (OutOfProjectionBoundsException e) {
            e.printStackTrace();
        }
    }

    public Coords2D(BlockVector2D point) {
        try {
            double[] geoCoords = geographicProjection.toGeo(point.getX(), point.getZ());
            this.x = point.getX();
            this.z = point.getZ();
            this.lon = geoCoords[0];
            this.lat = geoCoords[1];
        } catch (OutOfProjectionBoundsException e) {
            e.printStackTrace();
        }
    }

    public Coords2D(Location location) {
        try {
            double[] geoCoords = geographicProjection.toGeo(location.getX(), location.getZ());
            this.x = location.getX();
            this.z = location.getZ();
            this.lon = geoCoords[0];
            this.lat = geoCoords[1];
        } catch (OutOfProjectionBoundsException e) {
            e.printStackTrace();
        }
    }

    // GETTER

    public BlockVector2D toBlockVector2D() {
        return new BlockVector2D(this.x, this.z);
    }

    public Location toHighestLocation() {
        return new Location(mainWorld, this.x, getHighestY(), this.z);
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }

    public Integer getHighestY() {
        return mainWorld.getHighestBlockYAt(new Location(mainWorld, this.x, 100, this.z));
    }
}
