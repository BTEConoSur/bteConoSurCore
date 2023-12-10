package pizzaaxx.bteconosur.test;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.OutputExtent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import org.jetbrains.annotations.Nullable;

public class TestExtent implements OutputExtent {
    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 position, T block) throws WorldEditException {
        return false;
    }

    @Nullable
    @Override
    public Operation commit() {
        return null;
    }
}
