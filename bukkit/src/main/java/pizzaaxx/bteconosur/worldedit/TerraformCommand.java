package pizzaaxx.bteconosur.worldedit;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.BteConoSur;

import java.util.*;

public class TerraformCommand implements CommandExecutor {

    private final BteConoSur plugin;

    public TerraformCommand(BteConoSur plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, ConvexPolyhedralRegion> borderRegions = new HashMap<>();
    private final Map<UUID, ConvexPolyhedralRegion> innerRegions = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;

        String PREFIX = "§f[§2TERRAFORM§f] §7>>§r ";
        if (args.length > 0) {
            if (args[0].equals("border")) {

                Region selection;
                try {
                    selection = plugin.getWorldEditHelper().getSelection(p);
                } catch (IncompleteRegionException e) {
                    p.sendMessage(PREFIX + "Selecciona un área primero.");
                    return true;
                }

                if (selection instanceof ConvexPolyhedralRegion) {

                    ConvexPolyhedralRegion convexSelection = (ConvexPolyhedralRegion) selection;

                    borderRegions.put(p.getUniqueId(), (ConvexPolyhedralRegion) convexSelection.clone());

                    p.sendMessage(PREFIX + "Se ha guardado tu selección de bordes.");

                } else {
                    p.sendMessage(PREFIX + "Selecciona un área con §a/sel convex§f.");
                }

            } else if (args[0].equals("inner")) {

                Region selection = plugin.getWorldEditHelper().getIncompleteSelection(p);

                if (selection instanceof ConvexPolyhedralRegion) {

                    ConvexPolyhedralRegion convexSelection = (ConvexPolyhedralRegion) selection;

                    innerRegions.put(p.getUniqueId(), (ConvexPolyhedralRegion) convexSelection.clone());

                    p.sendMessage(PREFIX + "Se ha guardado tu selección de puntos interiores.");

                } else {
                    p.sendMessage(PREFIX + "Selecciona un área con §a/sel convex§f.");
                }

            } else if (args[0].equals("desel")) {

                borderRegions.remove(p.getUniqueId());
                innerRegions.remove(p.getUniqueId());

                p.sendMessage(PREFIX + "Se han eliminado tus selecciones.");

            } else {

                if (borderRegions.containsKey(p.getUniqueId())) {

                    World world = plugin.getWEWorld(); // probably useless

                    // CREATE POLYGONAL REGION FOR LOOPING

                    ConvexPolyhedralRegion borderRegion = borderRegions.get(p.getUniqueId());

                    List<BlockVector2D> points = new ArrayList<>();

                    for (Vector vector : borderRegion.getVertices()) {
                        points.add(new BlockVector2D(vector.getX(), vector.getZ()));
                    }

                    Polygonal2DRegion region = new Polygonal2DRegion(world, points, 100, 100);

                    // DEFINE HEIGHT POINTS

                    List<Vector> hPoints = new ArrayList<>(borderRegion.getVertices());

                    if (innerRegions.containsKey(p.getUniqueId())) {
                        hPoints.addAll(innerRegions.get(p.getUniqueId()).getVertices());
                    }

                    // GET WORLDEDIT STUFF

                    WorldEdit worldEdit = WorldEdit.getInstance();
                    WorldEditPlugin wePlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

                    com.sk89q.worldedit.entity.Player actor = new BukkitPlayer(wePlugin, wePlugin.getServerInterface(), p);
                    EditSession editSession = worldEdit.getEditSessionFactory().getEditSession(world, worldEdit.getSessionManager().get(actor).getBlockChangeLimit());

                    LocalSession localSession = worldEdit.getSessionManager().get(actor);
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
                        pattern = worldEdit.getPatternFactory().parseFromInput(args[0], parserContext);
                    } catch (InputParseException e) {
                        p.sendMessage(PREFIX + "Patrón de bloques inválido.");
                        return true;
                    }


                    int steep = 2;
                    if (args.length > 1) {
                        try {
                            steep = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            p.sendMessage(PREFIX + "Introduce una inclinación válida.");
                            return true;
                        }
                    }

                    Map<Integer, Map<Integer, Double>> calculated = new HashMap<>();


                    for (BlockVector vctr : region) {

                        Vector vector = new Vector(vctr.getX(), 100, vctr.getZ());

                        double v;
                        if (calculated.containsKey(vector.getBlockX()) && calculated.get(vector.getBlockX()).containsKey(vector.getBlockZ())) {
                            v = calculated.get(vector.getBlockX()).get(vector.getBlockZ());
                        } else {
                            double num = 0;
                            double den = 0;

                            for (Vector point : hPoints) {
                                Vector newPoint = new Vector(point).setY(100);
                                double w = 1/Math.pow(vector.distance(newPoint), steep);
                                num = num + (w * (point.getY() + 0.5));
                                den = den + w;
                            }

                            v = num/den;
                            Map<Integer, Double> temp = calculated.getOrDefault(vector.getBlockX(), new HashMap<>());
                            temp.put(vector.getBlockZ(), v);
                            calculated.put(vector.getBlockX(), temp);
                        }

                        double vMin;

                        // MINS
                        List<Double> possibleMins = new ArrayList<>();

                        for (double x = vector.getX() - 1; x <= vector.getX() + 1; x++) {
                            for (double z = vector.getZ() - 1; z <= vector.getZ() + 1; z++) {
                                Vector newVector = new Vector(x, 100, z);
                                double v2;
                                if (calculated.containsKey(newVector.getBlockX()) && calculated.get(newVector.getBlockX()).containsKey(vector.getBlockZ())) {
                                    v2 = calculated.get(newVector.getBlockX()).get(vector.getBlockZ());
                                } else {
                                    double num = 0;
                                    double den = 0;

                                    for (Vector point : hPoints) {
                                        Vector newPoint = new Vector(point).setY(100);
                                        double w = 1/Math.pow(newVector.distance(newPoint), steep);
                                        num = num + (w * (point.getY() + 0.5));
                                        den = den + w;
                                    }

                                    v2 = num/den;
                                    Map<Integer, Double> temp = calculated.getOrDefault(newVector.getBlockX(), new HashMap<>());
                                    temp.put(newVector.getBlockZ(), v2);
                                    calculated.put(newVector.getBlockX(), temp);
                                }
                                possibleMins.add(v2);
                            }
                        }
                        vMin = Collections.min(possibleMins);

                        for (double y = vMin; y <= v; y++) {
                            Vector finalVector = new Vector(vector.getX(), y, vector.getZ());

                            if (plugin.getWorldGuard().canBuild(p, new Location(plugin.getWorld(), vector.getX(), y, vector.getZ()))) {
                                if (mask != null && !mask.test(finalVector)) {
                                    continue;
                                }
                                try {
                                    editSession.setBlock(finalVector, pattern.apply(finalVector));
                                } catch (MaxChangedBlocksException e) {
                                    localSession.remember(editSession);
                                    p.sendMessage(PREFIX + "Límite de bloques alcanzado.");
                                    return true;
                                }
                            }

                        }
                    }
                    localSession.remember(editSession);

                    p.sendMessage(PREFIX + "Operación completada con éxito.");

                } else {
                    p.sendMessage(PREFIX + "Define al menos una selección de borde.");
                }
            }
        } else {
            p.sendMessage(PREFIX + "Introduce una acción o una ID.");
        }

        return true;
    }
}
