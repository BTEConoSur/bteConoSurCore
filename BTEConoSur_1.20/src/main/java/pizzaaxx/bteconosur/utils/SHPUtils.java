package pizzaaxx.bteconosur.utils;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.referencing.wkt.Parser;
import org.jetbrains.annotations.NotNull;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import java.io.File;
import java.io.IOException;
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
        return cityFeatures;
    }

}
