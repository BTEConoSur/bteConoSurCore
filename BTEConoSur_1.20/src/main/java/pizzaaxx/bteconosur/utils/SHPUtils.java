package pizzaaxx.bteconosur.utils;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.referencing.wkt.Parser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SHPUtils {

    public static @NotNull Map<Integer, SimpleFeature> getCityFeatures(@NotNull BTEConoSurPlugin plugin, String name) throws IOException {

        Map<Integer, SimpleFeature> cityFeatures = new HashMap<>();

        // load city regions from shapefile
        File shapefile = new File(plugin.getDataFolder(), "cities/" + name + "/" + name + ".shp");

        if (!shapefile.exists()) {
            throw new IOException("Shapefile not found.");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("url", shapefile.toURI().toURL());

        DataStore dataStore = DataStoreFinder.getDataStore(map);
        String typeName = dataStore.getTypeNames()[0];

        SimpleFeatureSource source = dataStore.getFeatureSource(typeName);
        Filter filter = Filter.INCLUDE;

        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(filter);
        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                cityFeatures.put(
                        Math.toIntExact((Long) feature.getAttribute("id")),
                        feature
                );
            }
        }
        dataStore.dispose();
        return cityFeatures;
    }

    @Contract("_ -> new")
    public static double @NotNull [] getCentroid(@NotNull Polygon polygon) {
        Coordinate[] coordinates = new Coordinate[polygon.npoints];
        for (int i = 0; i < polygon.npoints; i++) {
            coordinates[i] = new Coordinate(polygon.xpoints[i], polygon.ypoints[i]);
        }
        org.locationtech.jts.geom.Polygon jtsPolygon = new GeometryFactory()
                .createPolygon(
                        coordinates
                );
        org.locationtech.jts.geom.Point point = jtsPolygon.getCentroid();
        return new double[] {
                point.getX(),
                point.getY()
        };
    }
}
