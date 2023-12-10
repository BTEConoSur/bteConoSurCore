package pizzaaxx.bteconosur.protection;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import java.util.List;
import java.util.UUID;

public class PlayerExtent implements Extent {

    private final BTEConoSurPlugin plugin;
    private final Extent extent;
    private final UUID uuid;

    public PlayerExtent(BTEConoSurPlugin plugin, Extent extent, UUID uuid) {
        this.plugin = plugin;
        this.extent = extent;
        this.uuid = uuid;
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(@NotNull BlockVector3 position, T block) throws WorldEditException {
        if (plugin.canBuild(uuid, position.getX(), position.getZ())) {
            return extent.setBlock(position, block);
        }
        return false;
    }

    @Nullable
    @Override
    public Operation commit() {
        return extent.commit();
    }

    @Override
    public BlockVector3 getMinimumPoint() {
        return extent.getMinimumPoint();
    }

    @Override
    public BlockVector3 getMaximumPoint() {
        return extent.getMaximumPoint();
    }

    @Override
    public List<? extends Entity> getEntities(Region region) {
        return extent.getEntities(region);
    }

    @Override
    public List<? extends Entity> getEntities() {
        return extent.getEntities();
    }

    @Nullable
    @Override
    public Entity createEntity(@NotNull Location location, BaseEntity entity) {
        return extent.createEntity(location, entity);
    }

    @Override
    public BlockState getBlock(@NotNull BlockVector3 position) {
        return extent.getBlock(position);
    }

    @Override
    public BaseBlock getFullBlock(@NotNull BlockVector3 position) {
        return extent.getFullBlock(position);
    }
}
