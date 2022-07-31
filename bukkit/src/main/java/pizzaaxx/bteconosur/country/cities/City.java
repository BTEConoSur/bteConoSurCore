package pizzaaxx.bteconosur.country.cities;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.country.cities.projects.ProjectsRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class City {

    private final Country country;
    private final String name;

    private final String displayName;

    private final Polygonal2DRegion urbanArea;

    private final List<String> showcaseIDs = new ArrayList<>();

    private final List<List<BlockVector2D>> finishedRegions = new ArrayList<>();

    private final File folder;
    private final ProjectsRegistry registry;


    public String getName() {
        return name;
    }

    public File getFolder() {
        return folder;
    }

    public Country getCountry() {
        return country;
    }

    public ProjectsRegistry getRegistry() {
        return registry;
    }
}
