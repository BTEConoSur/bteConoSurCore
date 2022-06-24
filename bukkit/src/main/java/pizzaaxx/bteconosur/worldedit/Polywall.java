package pizzaaxx.bteconosur.worldedit;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.factory.PatternFactory;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.worldedit.WorldEditHelper.*;

public class Polywall implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equals("/polywalls")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                //

                // GET REGION

                Region region = null;
                try {
                    region = getSelection(p);
                } catch (IncompleteRegionException e) {
                    p.sendMessage(WORLD_EDIT_PREFIX + "Selecciona un área primero.");
                    return true;
                }

                if (args.length > 0) {

                    // PARSE PATTERN

                    com.sk89q.worldedit.entity.Player actor = new BukkitPlayer((WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit"), ((WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")).getServerInterface(), p);

                    LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(actor);
                    Mask mask = localSession.getMask();

                    ParserContext parserContext = new ParserContext();
                    parserContext.setActor(actor);
                    Extent extent = actor.getExtent();
                    if (extent instanceof World) {
                        parserContext.setWorld((World) extent);
                    }
                    parserContext.setSession(WorldEdit.getInstance().getSessionManager().get(actor));

                    Pattern pattern;
                    try {
                        pattern = WorldEdit.getInstance().getPatternFactory().parseFromInput(args[0], parserContext);
                    } catch (InputParseException e) {
                        p.sendMessage(WORLD_EDIT_PREFIX + "Patrón inválido.");
                        return true;
                    }

                    // GET POINTS

                    List<BlockVector2D> points = new ArrayList<>();
                    int maxY;
                    int minY;
                    if (region instanceof CuboidRegion) {
                        CuboidRegion cuboidRegion = (CuboidRegion) region;
                        Vector first = cuboidRegion.getPos1();
                        Vector second = cuboidRegion.getPos2();

                        maxY = cuboidRegion.getMaximumY();
                        minY = cuboidRegion.getMinimumY();

                        points.add(new BlockVector2D(first.getX(), first.getZ()));
                        points.add(new BlockVector2D(second.getX(), first.getZ()));
                        points.add(new BlockVector2D(second.getX(), second.getZ()));
                        points.add(new BlockVector2D(first.getX(), second.getZ()));
                    } else if (region instanceof Polygonal2DRegion) {
                        maxY = ((Polygonal2DRegion) region).getMaximumY();
                        minY = ((Polygonal2DRegion) region).getMinimumY();
                        points = ((Polygonal2DRegion) region).getPoints();
                    } else {
                        p.sendMessage(WORLD_EDIT_PREFIX + "Debes seleccionar una region cúbica o poligonal.");
                        return true;
                    }

                    if (points.size() < 3) {
                        p.sendMessage(WORLD_EDIT_PREFIX + "Selecciona un área primero.");
                        return true;
                    }

                    List<BlockVector2D> pointsFinal = new ArrayList<>(points);
                    pointsFinal.add(points.get(0));

                    EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession((World) new BukkitWorld(mainWorld), WorldEdit.getInstance().getSessionManager().get(actor).getBlockChangeLimit());

                    for (int i = minY; i <= maxY; i++) {
                        for (int j = 0; j < pointsFinal.size() - 1; j++) {
                            BlockVector2D v1 = pointsFinal.get(j);
                            BlockVector2D v2 = pointsFinal.get(j + 1);

                            editSession = setBlocksInLine(p, editSession, pattern, mask, v1.toVector(i), v2.toVector(i));
                        }
                    }

                    if (editSession != null) {

                        WorldEdit.getInstance().getSessionManager().get(actor).remember(editSession);
                    }

                    p.sendMessage(WORLD_EDIT_PREFIX + "Paredes de la selección creadas.");
                } else {
                    p.sendMessage(WORLD_EDIT_PREFIX + "Introduce un patrón de bloques.");
                }
            }
        }

        return true;
    }
}
