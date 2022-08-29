package pizzaaxx.bteconosur.country.cities.projects.ProjectSelector;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.cities.projects.Project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class SmallestProjectSelector implements IProjectSelector {

    private final BteConoSur plugin;

    public SmallestProjectSelector(BteConoSur plugin) {
        this.plugin = plugin;
    }

    private class ProjectAreaComparator implements Comparator<Project> {

        private final BteConoSur plugin;

        public ProjectAreaComparator(BteConoSur plugin) {
            this.plugin = plugin;
        }

        @Override
        public int compare(Project p1, Project p2) {

            BukkitWorld world = new BukkitWorld(plugin.getWorld());
            Polygonal2DRegion r1 = new Polygonal2DRegion(world, p1.getRegion().getPoints(), 100, 100);
            Polygonal2DRegion r2 = new Polygonal2DRegion(world, p2.getRegion().getPoints(), 100, 100);

            return Integer.compare(r1.getArea(), r2.getArea());
        }
    }

    @Override
    public Project select(@NotNull Collection<Project> projects) throws NotInsideProjectException {

        if (projects.size() > 0) {

            List<Project> ps = new ArrayList<>(projects);

            ps.sort(new ProjectAreaComparator(plugin));

            return ps.get(0);
        }
        throw new NotInsideProjectException();

    }
}
