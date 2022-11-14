package pizzaaxx.bteconosur.Cities.Actions;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import pizzaaxx.bteconosur.BTEConoSur;

import java.util.List;

public class RedefineRegionCityAction {

    private final BTEConoSur plugin;
    private final String name;
    private final List<BlockVector2D> points;

    public RedefineRegionCityAction(BTEConoSur plugin, String name, List<BlockVector2D> points) {
        this.plugin = plugin;
        this.name = name;
        this.points = points;
    }

    public void execute() throws CityActionException {
        if (!plugin.getCityManager().exists(name)) {
            throw new CityActionException();
        }

        ProtectedRegion region = new ProtectedPolygonalRegion(
                "city_" + name,
                points,
                -100,
                8000
        );

        plugin.getRegionManager().addRegion(region);
    }
}
