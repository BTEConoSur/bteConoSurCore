package pizzaaxx.bteconosur.building;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;
import static pizzaaxx.bteconosur.BTEConoSurPlugin.WORLDEDIT_CONNECTOR;

public class DivideCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public DivideCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(PREFIX + "Introduce la cantidad de divisiones.");
            return true;
        }

        int divisions = Integer.parseInt(args[0]);
        if (divisions < 1) {
            sender.sendMessage(PREFIX + "La cantidad de divisiones debe ser mayor a 0.");
            return true;
        }

        RegionSelector selector = WORLDEDIT_CONNECTOR.getLocalSession(player).getRegionSelector(new BukkitWorld(player.getWorld()));
        if (!(selector instanceof CuboidRegionSelector cuboidRegionSelector) || !selector.isDefined()) {
            sender.sendMessage(PREFIX + "Selecciona un área cúbica primero.");
            return true;
        }

        BlockVector3 primary;
        BlockVector3 secondary;
        try {
            primary = cuboidRegionSelector.getRegion().getPos1();
            secondary = cuboidRegionSelector.getRegion().getPos2();
        } catch (IncompleteRegionException e) {
            sender.sendMessage(PREFIX + "Selecciona un área cúbica primero.");
            return true;
        }

        List<BlockVector3> blocks = new ArrayList<>(WORLDEDIT_CONNECTOR.getBlocksInLine(primary, secondary));
        blocks.sort(Comparator.comparingDouble(v -> v.distance(primary)));

        if (blocks.size() < divisions) {
            sender.sendMessage(PREFIX + "La cantidad de bloques es menor a la cantidad de divisiones.");
            return true;
        }

        int baseSize = blocks.size() / divisions;
        int remaining = blocks.size() % divisions;

        int[] sizes = new int[divisions];
        Arrays.fill(sizes, baseSize);

        for (int i = 0; i < remaining; i++) {

            int target = i % 2 == 0 ? i / 2 : divisions - (i / 2) - 1;
            sizes[target]++;

        }

        LocalSession localSession = WORLDEDIT_CONNECTOR.getLocalSession(player);
        EditSession session = WORLDEDIT_CONNECTOR.getEditSession(player);

        Mask mask = session.getMask();

        int counter = 0;
        int switcher = 0;
        for (int amount : sizes) {

            BaseBlock block;
            if (switcher % 2 == 0) {
                assert BlockTypes.DIAMOND_BLOCK != null;
                block = BlockTypes.DIAMOND_BLOCK.getDefaultState().toBaseBlock();
            } else {
                assert BlockTypes.GOLD_BLOCK != null;
                block = BlockTypes.GOLD_BLOCK.getDefaultState().toBaseBlock();
            }

            for (BlockVector3 vector : blocks.subList(counter, counter + amount)) {

                if (!plugin.canBuild(player.getUniqueId(), vector.getX(), vector.getZ())) continue;
                if (mask != null && !mask.test(vector)) continue;

                try {
                    session.setBlock(vector, block);
                } catch (MaxChangedBlocksException e) {
                    localSession.remember(session);
                    player.sendMessage(PREFIX + "Se ha alcanzado el límite de bloques modificados.");
                    return true;
                }
            }
            counter += amount;
            switcher++;
        }

        localSession.remember(session);
        session.close();

        player.sendMessage(PREFIX + "Operación completada. (Bloques cambiados: " + blocks.size() + ")");

        return true;
    }
}
