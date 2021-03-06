package pizzaaxx.bteconosur.worldedit;

import com.google.common.collect.Lists;
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
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.worldedit.WorldEditHelper.*;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;

public class DivideCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;

        if (args.length == 0) {
            p.sendMessage(WORLD_EDIT_PREFIX + "Introduce el número de subdivisiones.");
            return true;
        }

        int subdivisions;
        try {
            subdivisions = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            p.sendMessage(WORLD_EDIT_PREFIX + "Introduce un número de subdivisiones válido.");
            return true;
        }

        WorldEditPlugin we = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        com.sk89q.worldedit.entity.Player actor = new BukkitPlayer(we, we.getServerInterface(), p);

        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(actor);
        Mask mask = localSession.getMask();

        ParserContext parserContext = new ParserContext();
        parserContext.setActor(actor);
        Extent extent = actor.getExtent();
        if (extent instanceof World) {
            parserContext.setWorld((World) extent);
        }
        parserContext.setSession(localSession);

        Pattern pattern1;
        Pattern pattern2;

        PatternFactory factory = WorldEdit.getInstance().getPatternFactory();

        try {
            if (args.length > 1) {
                pattern1 = factory.parseFromInput(args[1], parserContext);
            } else {
                pattern1 = factory.parseFromInput("41", parserContext);
            }
        } catch (InputParseException e) {
            p.sendMessage(WORLD_EDIT_PREFIX + "Patrón 1 inválido.");
            return true;
        }

        try {
            if (args.length > 2) {
                pattern2 = factory.parseFromInput(args[2], parserContext);
            } else {
                pattern2 = factory.parseFromInput("57", parserContext);
            }
        } catch (InputParseException e) {
            p.sendMessage(WORLD_EDIT_PREFIX + "Patrón 2 inválido.");
            return true;
        }

        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession((World) new BukkitWorld(mainWorld), localSession.getBlockChangeLimit());

        Region selection;
        try {
            selection =  WorldEditHelper.getSelection(p);
        } catch (IncompleteRegionException e) {
            p.sendMessage(WORLD_EDIT_PREFIX + "Selecciona una región válida.");
            return true;
        }

        if (selection instanceof CuboidRegion) {
            CuboidRegion cuboid = (CuboidRegion) selection;
            List<Vector> allVectors = getBlocksInLine(cuboid.getPos1(), cuboid.getPos2());

            allVectors.sort(new VectorDistanceComparator(cuboid.getPos1()));

            if (allVectors.size() < subdivisions) {
                p.sendMessage(WORLD_EDIT_PREFIX + "La línea seleccionada tiene menos bloques que el número de subdivisiones.");
                return true;
            } else {


                int listSize = (int) Math.floor(allVectors.size() / subdivisions);

                List<List<Vector>> slicedLists = Lists.partition(allVectors, listSize);
                List<Integer> listsLengths = new ArrayList<>();
                slicedLists.forEach(
                        list -> listsLengths.add(list.size())
                );

                if (listsLengths.get(listsLengths.size() - 1) < listSize) {

                    for (int i = 1; i <= listsLengths.get(listsLengths.size() - 1); i++) {

                        int actual = listsLengths.get((i % 2 == 0 ? (i / 2) - 1 : listsLengths.size() - 1 - ((i + 1) / 2)));

                        listsLengths.set((i % 2 == 0 ? (i / 2) - 1 : listsLengths.size() - 1 - ((i + 1) / 2)), actual + 1);

                    }

                    listsLengths.remove(listsLengths.size() - 1);


                }

                int j = 0;
                int k = 0;
                for (Integer length : listsLengths) {

                    for (Vector vector : allVectors.subList(j, j + length)) {
                        try {
                            if (mask != null && !(mask.test(vector))) {
                                continue;
                            }
                            if (getWorldGuard().canBuild(p, mainWorld.getBlockAt(new Location(mainWorld, vector.getX(), vector.getY(), vector.getZ())))) {
                                editSession.setBlock(vector, (k % 2 == 0 ? pattern1 : pattern2).apply(vector));
                            }
                        } catch (MaxChangedBlocksException e) {
                            p.sendMessage(WORLD_EDIT_PREFIX + "Haz alcanzado el límite de bloques.");

                            localSession.remember(editSession);

                            return true;
                        }
                    }

                    k++;
                    j += length;

                }

            }

            p.sendMessage(WORLD_EDIT_PREFIX + "Operación completada.");

            localSession.remember(editSession);
        }



        return true;
    }
}
