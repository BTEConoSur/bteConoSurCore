package pizzaaxx.bteconosur.worldedit;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.worldedit.trees.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;
import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.worldedit.Methods.getEditSession;
import static pizzaaxx.bteconosur.worldedit.Methods.getLocalSession;

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
    private int minX;
    private int minZ;
    private int maxX;
    private int maxZ;
    private int cellsWidth;
    private int cellsHeight;

    // CONSTRUCTOR
    public PoissonDiskSampling(int radius, Region region, Player player, List<Tree> trees){
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
        this.player = player;
        this.trees = trees;
        int n = 2;
        this.cellsize = floor(radius/Math.sqrt(n));
        this.flatRegion = new Polygonal2DRegion(region.getWorld(), this.region.getPoints(), this.region.getMaximumY(), this.region.getMaximumY());
        this.editSession = getEditSession(this.player);

        // POINTS

        minX = this.region.getPoints().get(0).getBlockX();
        minZ = this.region.getPoints().get(0).getBlockZ();
        maxX = this.region.getPoints().get(0).getBlockX();
        maxZ = this.region.getPoints().get(0).getBlockZ();

        for (BlockVector2D b : this.region.getPoints()) {
            if (b.getBlockX() < minX) {
                minX = b.getBlockX();
            }

            if (b.getBlockZ() < minZ) {
                minZ = b.getBlockZ();
            }

            if (b.getBlockX() > maxX) {
                maxX = b.getBlockX();
            }

            if (b.getBlockZ() > maxZ) {
                maxZ = b.getBlockZ();
            }
        }

        // GRID
        this.cellsWidth = (int) Math.ceil(Math.abs(maxX - minX) / cellsize) + 1;
        this.cellsHeight = (int) Math.ceil(Math.abs(maxZ - minZ) / cellsize) + 1;


        this.grid = new BlockVector2D[cellsWidth][cellsHeight];
    }

    public void generate() throws MaxChangedBlocksException {
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
        try {
            tryToPlace(startingPoint);
        } catch (MaxChangedBlocksException e) {
            throw e;
        }

        active.add(startingPoint);

        while (active.size() > 0) {
            BlockVector2D p = active.get(random.nextInt(active.size()));
            boolean found = false;

            int k = 30;
            for (int tries = 0; tries < k; tries++) {
                double theta = random.nextInt(360);
                double newRadius = random.nextInt((int) radius) + radius;

                double newX = p.getX() + (newRadius * cos(toRadians(theta)));
                double newZ = p.getZ() + (newRadius * sin(toRadians(theta)));
                BlockVector2D newPoint = new BlockVector2D(newX, newZ);

                if (isValid(newPoint)) {
                    try {
                        tryToPlace(newPoint);
                    } catch (MaxChangedBlocksException e) {
                        throw e;
                    }
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
        if (flatRegion.contains(new Vector(point.getBlockX(), region.getMaximumY(), point.getBlockZ()))) {
            int xGrid = (int) floor(Math.abs((point.getX() - minX)) / cellsize);
            int zGrid = (int) floor(Math.abs((point.getZ() - minZ)) / cellsize);

            int i0 = Math.max(xGrid - 1, 0);
            int i1 = Math.min(xGrid + 1, cellsWidth - 1);
            int j0 = Math.max(zGrid - 1, 0);
            int j1 = Math.min(zGrid + 1, cellsHeight - 1);
            for (int i = i0; i <= i1; i++) {
                for (int j = j0; j <= j1; j++) {
                    if (grid[i][j] != null) {
                        if (grid[i][j].distance(point) < radius) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void tryToPlace(BlockVector2D point) throws MaxChangedBlocksException {
        Tree tree = trees.get(random.nextInt(trees.size()));
        for (int y = region.getMaximumY(); y >= region.getMinimumY(); y = y - 1) {
            if (mainWorld.getBlockAt(point.getBlockX(), y, point.getBlockZ()).getType().isSolid() && mainWorld.getBlockAt(point.getBlockX(), y + 1, point.getBlockZ()).isEmpty()) {
                editSession = tree.place(new Vector(point.getBlockX(), y + 1, point.getBlockZ()), this.player, editSession);
                break;
            }
        }
    }

    private void insertPoint(BlockVector2D point) {
        int xIndex = (int) floor(Math.abs((point.getX() - minX)) / cellsize);
        int zIndex = (int) floor(Math.abs((point.getZ() - minZ)) / cellsize);
        grid[xIndex][zIndex] = point;
    }

}
