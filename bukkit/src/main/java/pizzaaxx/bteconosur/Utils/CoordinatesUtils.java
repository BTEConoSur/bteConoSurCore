package pizzaaxx.bteconosur.Utils;

import com.sk89q.worldedit.BlockVector2D;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;

public class CoordinatesUtils {

    public static Location blockVector2DtoLocation(@NotNull BTEConoSur plugin, @NotNull BlockVector2D vector) {

        return plugin.getWorld().getHighestBlockAt(vector.getBlockX(), vector.getBlockZ()).getLocation();

    }

}
