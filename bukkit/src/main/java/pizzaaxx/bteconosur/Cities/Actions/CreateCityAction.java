package pizzaaxx.bteconosur.Cities.Actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.sql.SQLException;
import java.util.List;

public class CreateCityAction {

    private final BTEConoSur plugin;
    private final String name;
    private final String displayName;
    private final Country country;
    private final List<BlockVector2D> region;

    public CreateCityAction(BTEConoSur plugin, String name, String displayName, Country country, List<BlockVector2D> region) {
        this.plugin = plugin;
        this.name = name;
        this.displayName = displayName;
        this.country = country;
        this.region = region;
    }

    public void execute() throws CityActionException, SQLException, JsonProcessingException {
        if (plugin.getCityManager().exists(name)) {
            throw new CityActionException();
        }

        ProtectedPolygonalRegion cityRegion = new ProtectedPolygonalRegion(
                "city_" + this.name,
                this.region,
                -100,
                8000
        );
        plugin.getRegionManager().addRegion(cityRegion);

        plugin.getSqlManager().insert(
                "cities",
                new SQLValuesSet(
                        new SQLValue(
                                "name",
                                this.name
                        ),
                        new SQLValue(
                                "display_name",
                                this.displayName
                        ),
                        new SQLValue(
                                "showcase_ids",
                                "[]"
                        ),
                        new SQLValue(
                                "projects",
                                "[]"
                        ),
                        new SQLValue(
                                "country",
                                this.country.getName()
                        ),
                        new SQLValue(
                                "urban_area",
                                false
                        )
                )
        ).execute();
        plugin.getCityManager().registerName(this.name);
        country.addCity(name).execute();
    }
}
