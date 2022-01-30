package pizzaaxx.bteconosur.worldedit;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.worldedit.trees.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;
import static pizzaaxx.bteconosur.bteConoSur.mainWorld;
import static pizzaaxx.bteconosur.worldedit.methods.getEditSession;
import static pizzaaxx.bteconosur.worldedit.methods.getLocalSession;

public class PoissonDiskSampling {
    private BlockVector2D[][] grid;
    private final double radius;
    private final Polygonal2DRegion region;
    private final Polygonal2DRegion flatRegion;
    private final Player player;
    private final List<Tree> trees;
    private final double cellsize;
    private final List<BlockVector2D> active = new ArrayList<>();
    private final List<BlockVector2D> points = new ArrayList<>();
    private final Random random = new Random();
    private EditSession editSession;

    // CONSTRUCTOR
    public PoissonDiskSampling(int radius, Region region, Player player, List<Tree> trees) {
        this.radius = radius;
        if (region instanceof CuboidRegion) {
            CuboidRegion cuboidRegion = (CuboidRegion) region;
            Vector first = cuboidRegion.getPos1();
            Vector second = cuboidRegion.getPos2();

            int maxY = cuboidRegion.getMaximumY();
            int minY = cuboidRegion.getMinimumY();

            List<BlockVector2D> cuboidPoints = new ArrayList<>();

            cuboidPoints.add(new BlockVector2D(first.getX(), first.getZ()));
            cuboidPoints.add(new BlockVector2D(second.getX(), first.getZ()));
            cuboidPoints.add(new BlockVector2D(second.getX(), second.getZ()));
            cuboidPoints.add(new BlockVector2D(first.getX(), second.getZ()));

            this.region = new Polygonal2DRegion((World) new BukkitWorld(mainWorld), cuboidPoints, maxY, minY);
        } else {
            this.region = (Polygonal2DRegion) region;
        }
        this.grid = new BlockVector2D[60000000][60000000];
        // TODO Fix coordinate shit.
        this.player = player;
        this.trees = trees;
        int n = 2;
        this.cellsize = floor(radius/Math.sqrt(n));
        this.flatRegion = new Polygonal2DRegion(region.getWorld(), this.region.getPoints(), this.region.getMaximumY(), this.region.getMaximumY());
        this.editSession = getEditSession(this.player);
    }

    public void generate() {
        int rndm = random.nextInt(this.flatRegion.getArea());
        int i = 0;
        BlockVector2D startingPoint = new BlockVector2D(0, 0);
        for (BlockVector vector : flatRegion) {
            if (i == rndm) {
                startingPoint = new BlockVector2D(vector.getX(), vector.getZ());
            }
            i++;
        }

        insertPoint(startingPoint);
        tryToPlace(startingPoint);
        active.add(startingPoint);

        while (active.size() > 0) {
            BlockVector2D p = active.get(random.nextInt(active.size()));
            boolean found = false;

            int k = 30;
            for (int tries = 0; tries < k; tries++) {
                double theta = random.nextInt(360);
                double newRadius = random.nextInt((int) ((int) (2 * radius) - radius)) + radius;

                double newX = p.getX() + newRadius * cos(toRadians(theta));
                double newZ = p.getZ() + newRadius * sin(toRadians(theta));
                BlockVector2D newPoint = new BlockVector2D(newX, newZ);

                if (isValid(newPoint)) {
                    tryToPlace(newPoint);
                    active.add(newPoint);
                    found = true;
                    insertPoint(newPoint);
                }
            }

            if (!(found)) {
                active.remove(p);
            }
        }

        getLocalSession(this.player).remember(editSession);
    }

    private boolean isValid(BlockVector2D point) {
        int xGrid = (int) floor(point.getX() / cellsize);
        int zGrid = (int) floor(point.getZ() / cellsize);

        for (int i = xGrid - 1; i <= xGrid + 1; i++) {
            for (int j = zGrid - 1; j <= zGrid + 1; j++) {
                if (i == xGrid && j == zGrid) {
                    continue;
                }
                if (grid[i][j] != null && flatRegion.contains(new Vector(point.getBlockX(), region.getMaximumY(), point.getBlockZ()))) {
                    if (grid[i][j].distance(point) < radius) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void tryToPlace(BlockVector2D point) {
        Tree tree = trees.get(random.nextInt(trees.size()));
        for (int y = region.getMaximumY(); y >= region.getMinimumY(); y = y - 1) {
            if (mainWorld.getBlockAt(point.getBlockX(), y, point.getBlockZ()).getType().isSolid() && mainWorld.getBlockAt(point.getBlockX(), y + 1, point.getBlockZ()).isEmpty()) {
                editSession = tree.place(new Vector(point.getBlockX(), y + 1, point.getBlockZ()), this.player, editSession);
                mainWorld.getBlockAt(point.getBlockX(), y, point.getBlockZ()).setType(Material.BEDROCK);
                break;
            }
        }
    }

    private void insertPoint(BlockVector2D point) {
        int xIndex = (int) floor(point.getX() / cellsize);
        int zIndex = (int) floor(point.getZ() / cellsize);
        grid[xIndex][zIndex] = point;
    }

}
