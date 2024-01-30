package pizzaaxx.bteconosur.building;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import java.util.ArrayList;
import java.util.List;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;
import static pizzaaxx.bteconosur.BTEConoSurPlugin.WORLDEDIT_CONNECTOR;

public class PolywallsCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public PolywallsCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        // //polywalls <pattern> (-l)

        if (args.length < 1) {
            player.sendMessage("§cIntroduce un patrón.");
            return true;
        }

        boolean connectLast = !(args.length == 2 && args[1].equals("-l"));

        Region selection;
        try {
            selection = WORLDEDIT_CONNECTOR.getSelection(player);
        } catch (IncompleteRegionException e) {
            player.sendMessage(PREFIX + "Selecciona una región poligonal.");
            return true;
        }

        if (!(selection instanceof Polygonal2DRegion polygonalRegion)) {
            player.sendMessage(PREFIX + "Selecciona una región poligonal.");
            return true;
        }

        String patternString = args[0];
        Pattern pattern;
        try {
            pattern = WORLDEDIT_CONNECTOR.getPattern(player, patternString);
        } catch (InputParseException e) {
            player.sendMessage(PREFIX + "Patrón inválido.");
            return true;
        }

        LocalSession localSession = WORLDEDIT_CONNECTOR.getLocalSession(player);
        EditSession session = WORLDEDIT_CONNECTOR.getEditSession(player);

        List<BlockVector2> points = new ArrayList<>(polygonalRegion.getPoints());

        if (connectLast) {
            points.add(points.get(0));
        }

        Mask mask = localSession.getMask();

        int minY = polygonalRegion.getMinimumPoint().getBlockY();
        int maxY = polygonalRegion.getMaximumPoint().getBlockY();
        for (int y = minY; y <= maxY; y++) {
            for (int i = 1; i < points.size(); i++) {
                BlockVector2 from = points.get(i - 1);
                BlockVector2 to = points.get(i);

                BlockVector3 from3 = BlockVector3.at(from.getBlockX(), y, from.getBlockZ());
                BlockVector3 to3 = BlockVector3.at(to.getBlockX(), y, to.getBlockZ());

                for (BlockVector3 vector : WORLDEDIT_CONNECTOR.getBlocksInLine(from3, to3)) {
                    plugin.log("a");
                    if (mask != null && !mask.test(vector)) {
                        continue;
                    }
                    try {
                        session.setBlock(vector, pattern);
                        plugin.log("d");
                    } catch (MaxChangedBlocksException e) {
                        localSession.remember(session);
                        player.sendMessage(PREFIX + "Se ha alcanzado el límite de bloques modificados.");
                        session.close();
                        return true;
                    }
                }
            }
        }

        localSession.remember(session);
        player.sendMessage(PREFIX + "Paredes creadas.");
        session.close();

        return true;
    }
}
