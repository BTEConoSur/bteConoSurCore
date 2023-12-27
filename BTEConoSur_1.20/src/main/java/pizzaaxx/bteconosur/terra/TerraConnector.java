package pizzaaxx.bteconosur.terra;

import net.buildtheearth.terraminusminus.dataset.IScalarDataset;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorPipelines;
import net.buildtheearth.terraminusminus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraminusminus.generator.GeneratorDatasets;
import net.buildtheearth.terraminusminus.projection.OutOfProjectionBoundsException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import java.util.concurrent.CompletableFuture;

public class TerraConnector {

    private static final EarthGeneratorSettings bteGeneratorSettings = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);

    /**
     * Gets the geographical location from in-game coordinates
     *
     * @param x X-Axis in-game
     * @param z Z-Axis in-game
     * @return The geographical location (Long, Lat)
     */
    public static double[] toGeo(double x, double z) {
        try {
            return bteGeneratorSettings.projection().toGeo(x, z);
        } catch (OutOfProjectionBoundsException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets in-game coordinates from geographical location
     *
     * @param lon Geographical Longitude
     * @param lat Geographic Latitude
     * @return The in-game coordinates (x, z)
     */
    public static double[] fromGeo(double lon, double lat) {
        // create final lat and lon, where lon is between -180 and 180 and lat is between -90 and 90
        double finalLat = Math.min(Math.max(lat, -90), 90);
        double finalLon = Math.min(Math.max(lon, -180), 180);
        try {
            return bteGeneratorSettings.projection().fromGeo(finalLon, finalLat);
        } catch (OutOfProjectionBoundsException e) {
            throw new RuntimeException(e);
        }
    }


    public static CompletableFuture<Double> getHeight(double x, double z) {
        CompletableFuture<Double> altFuture;
        try {
            double[] adjustedProj = bteGeneratorSettings.projection().toGeo(x, z);

            double adjustedLon = adjustedProj[0];
            double adjustedLat = adjustedProj[1];
            GeneratorDatasets datasets = new GeneratorDatasets(bteGeneratorSettings);


            altFuture = datasets.<IScalarDataset>getCustom(EarthGeneratorPipelines.KEY_DATASET_HEIGHTS)
                    .getAsync(adjustedLon, adjustedLat)
                    .thenApply(a -> a + 1.0d);
        } catch (OutOfProjectionBoundsException e) {
            altFuture = CompletableFuture.completedFuture(0.0);
        }
        return altFuture;
    }

    public static void teleportAsync(BTEConoSurPlugin plugin, double x, double z, Player player) {
        CompletableFuture<Double> altFuture = getHeight(x, z);
        altFuture.thenAcceptAsync(alt -> player.teleportAsync(
                new Location(
                        plugin.getWorld(alt),
                        x,
                        alt,
                        z
                )
        ));
    }

}
