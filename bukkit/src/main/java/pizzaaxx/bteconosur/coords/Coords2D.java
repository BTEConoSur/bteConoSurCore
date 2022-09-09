package pizzaaxx.bteconosur.coords;

import com.sk89q.worldedit.BlockVector2D;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.helper.Pair;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;

public class Coords2D {

    private final GeographicProjection geographicProjection =
            EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS).projection();

    private double x;
    private double z;
    private double lon;
    private double lat;

    public Coords2D(double lat, double lon) {
        try {
            double[] mcCoords = geographicProjection.fromGeo(lon, lat);
            initialize(mcCoords[0], mcCoords[1], lon, lat);
        } catch (OutOfProjectionBoundsException e) {
            e.printStackTrace();
        }
    }

    public Coords2D(Location location) {
        this(new BlockVector2D(location.getX(), location.getZ()));
    }

    public Coords2D(BlockVector2D point) {
        try {
            double[] geoCoords = geographicProjection.toGeo(point.getX(), point.getZ());
            initialize(point.getX(), point.getZ(), geoCoords[0], geoCoords[1]);
        } catch (OutOfProjectionBoundsException e) {
            e.printStackTrace();
        }
    }

    private void initialize(double x, double z, double lon, double lat) {
            this.x = x;
            this.z = z;
            this.lon = lon;
            this.lat = lat;
    }

    public BlockVector2D toBlockVector2D() {
        return new BlockVector2D(this.x, this.z);
    }

    public Location toHighestLocation(@NotNull BteConoSur plugin) {
        return new Location(plugin.getWorld(), this.x, getHighestY(plugin), this.z);
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

    public int getBlockX() {
        return (int) x;
    }

    public double getZ() {
        return z;
    }

    public double getBlockZ() {
        return (int) z;
    }

    public int getHighestY(@NotNull BteConoSur plugin) {
        return plugin.getWorld().getHighestBlockYAt((int) x, (int) z);
    }

    public Pair<Double, Double> getGeoPair() {
        return new Pair<>(lon, lat);
    }
}
