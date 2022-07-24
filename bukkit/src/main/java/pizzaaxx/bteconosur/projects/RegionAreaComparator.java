package pizzaaxx.bteconosur.projects;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.Comparator;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;

public class RegionAreaComparator implements Comparator<ProtectedRegion> {

    @Override
    public int compare(ProtectedRegion r1, ProtectedRegion r2) {

        BukkitWorld world = new BukkitWorld(mainWorld);
        Polygonal2DRegion p1 = new Polygonal2DRegion(world, r1.getPoints(), 100, 100);
        Polygonal2DRegion p2 = new Polygonal2DRegion(world, r2.getPoints(), 100, 100);

        return Integer.compare(p1.getArea(), p2.getArea());
    }
}
