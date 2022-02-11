package pizzaaxx.bteconosur.coords;

import com.sk89q.worldedit.BlockVector2D;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import org.bukkit.Location;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;

public class Coords2D {

    private double x;
    private double z;
    private double lon;
    private double lat;

    public Coords2D(Double lat, Double lon) {
        GeographicProjection geographicProjection = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS).projection();

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
        this(point.getX(), point.getZ());
    }

    public Coords2D(Location location) {
        this(location.getX(), location.getZ());
    }

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

    public int getHighestY() {
        return mainWorld.getHighestBlockYAt(new Location(mainWorld, this.x, 100, this.z));
    }
}
