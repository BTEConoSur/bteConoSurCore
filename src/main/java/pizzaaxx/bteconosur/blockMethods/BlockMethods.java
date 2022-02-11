package pizzaaxx.bteconosur.blockMethods;

import com.sk89q.worldedit.BlockVector;

import java.util.ArrayList;
import java.util.List;

public class BlockMethods {

    public List<BlockVector> getLineBlocks(BlockVector loc1, BlockVector loc2) {
        List<BlockVector> blocks = new ArrayList<>();

        Double xMax = Math.max(loc1.getX(), loc2.getX());
        Double xMin = Math.min(loc1.getX(), loc2.getX());
        Double yMax = Math.max(loc1.getY(), loc2.getY());
        Double yMin = Math.min(loc1.getY(), loc2.getY());
        Double zMax = Math.max(loc1.getZ(), loc2.getZ());
        Double zMin = Math.min(loc1.getZ(), loc2.getZ());



        return blocks;
    }
}
