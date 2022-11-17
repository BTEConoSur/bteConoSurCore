package pizzaaxx.bteconosur.Projects.Actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.Actions.CityActionException;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Projects.ProjectType;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Utils.StringUtils;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

import static pizzaaxx.bteconosur.Utils.StringUtils.LOWER_CASE;

public class CreateProjectAction {

    private final BTEConoSur plugin;

    private final Country country;
    private final City city;
    private final ProjectType type;
    private final int points;

    private final List<BlockVector2D> region;


    public CreateProjectAction(BTEConoSur plugin, Country country, City city, ProjectType type, int points, List<BlockVector2D> region) {
        this.plugin = plugin;
        this.country = country;
        this.city = city;
        this.type = type;
        this.points = points;
        this.region = region;
    }

    public void exec() throws SQLException, CityActionException, JsonProcessingException {
        String id = StringUtils.generateCode(6, plugin.getProjectRegistry().getIds(), LOWER_CASE);
        plugin.getSqlManager().insert(
                "projects",
                new SQLValuesSet(
                        new SQLValue(
                                "id", id
                        ),
                        new SQLValue(
                                "country", country.getName()
                        ),
                        new SQLValue(
                                "city", city.getName()
                        ),
                        new SQLValue(
                                "pending", false
                        ),
                        new SQLValue(
                                "type", type.getName()
                        ),
                        new SQLValue(
                                "points", points
                        ),
                        new SQLValue(
                                "members", new HashSet<>()
                        )
                )
        ).execute();

        ProtectedPolygonalRegion protectedPolygonalRegion = new ProtectedPolygonalRegion("project_" + id, region, -100, 8000);
        plugin.getRegionManager().addRegion(protectedPolygonalRegion);

        city.addProject(id).execute();

        country.getLogsChannel().sendMessage(":clipboard: Proyecto de tipo `" + type.getDisplayName() + "` creado con la ID `" + id + "`.").queue();
    }
}
