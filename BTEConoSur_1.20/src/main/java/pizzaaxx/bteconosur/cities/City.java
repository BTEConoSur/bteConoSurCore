package pizzaaxx.bteconosur.cities;

import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.MultiPolygon;
import org.opengis.feature.simple.SimpleFeature;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.utils.registry.RegistrableEntity;

public class City implements RegistrableEntity<Integer> {

    private final BTEConoSurPlugin plugin;
    private final String country;
    private final int id;
    private final String name;
    private final long area;
    private final MultiPolygon multiPolygon;

    public City(@NotNull BTEConoSurPlugin plugin, String countryName, int id) {
        this.plugin = plugin;
        this.country = countryName;
        this.id = id;

        SimpleFeature feature = plugin.getShapefile(countryName).get(id);
        this.name = feature.getAttribute("name").toString();
        this.area = (long) feature.getAttribute("area");
        this.multiPolygon = (MultiPolygon) feature.getDefaultGeometry();
    }

    @Override
    public Integer getID() {
        return id;
    }

    @Override
    public void disconnected() {

    }

    public String getName() {
        return name;
    }
}
