package pizzaaxx.bteconosur.WorldEdit.Assets.Commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.WorldEdit.Assets.Asset;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AssetsCommand implements CommandExecutor {

    private final BTEConoSur plugin;
    private final String prefix;
    private final Map<UUID, Vector> origins = new HashMap<>();

    public AssetsCommand(@NotNull BTEConoSur plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getWorldEdit().getPrefix();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores.");
            return true;
        }

        Player p = (Player) sender;
        ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());

        if (args.length < 1) {
            p.sendMessage(prefix + "Introduce un subcomando.");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "setorigin": {

                if (!s.isBuilder()) {
                    p.sendMessage(prefix + "Debes haber terminado un proyecto para poder crear §oassets§r.");
                    return true;
                }

                RegionSelector selector = plugin.getWorldEdit().getLocalSession(p).getRegionSelector(plugin.getWorldEditWorld());

                if (!(selector instanceof CuboidRegionSelector)) {
                    p.sendMessage(prefix + "Selecciona la posición 1 (click izquierdo) de un área cúbica.");
                    return true;
                }

                CuboidRegionSelector cuboidSelector = (CuboidRegionSelector) selector;

                Vector pos1 = cuboidSelector.getIncompleteRegion().getPos1();

                if (pos1 == null) {
                    p.sendMessage(prefix + "Selecciona la posición 1 (click izquierdo) de un área cúbica.");
                    return true;
                }

                origins.put(p.getUniqueId(), new Vector(pos1));
                p.sendMessage(prefix + "Punto de origen establecido en las coordenadas §a" + pos1.getBlockX() + " " + pos1.getBlockY() + " " + pos1.getBlockZ() + "§f.");

                break;
            }
            case "create": {

                if (!s.isBuilder()) {
                    p.sendMessage(prefix + "Deber haber terminado un proyecto para poder crear §oassets§r.");
                    return true;
                }

                if (!origins.containsKey(p.getUniqueId())) {
                    p.sendMessage(prefix + "Debes seleccionar un punto de origen para el §oasset§r. Usa §a/asset setOrigin§f.");
                    return true;
                }


                if (args.length < 2) {
                    p.sendMessage(prefix + "Introduce un nombre para el §oassets§r.");
                    return true;
                }

                String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                if (!name.matches("[a-zA-Z0-9_ñÑ]{1,32}")) {
                    p.sendMessage(prefix + "Introduce un nombre válido.");
                    return true;
                }

                try {
                    Region region = plugin.getWorldEdit().getSelection(p);
                    if (!(region instanceof CuboidRegion)) {
                        p.sendMessage(prefix + "Selecciona una región cúbica.");
                        return true;
                    }
                    CuboidRegion cuboidRegion = (CuboidRegion) region;

                    if (!cuboidRegion.contains(origins.get(p.getUniqueId()))) {
                        p.sendMessage(prefix + "El punto de origen seleccionado no está dentro del área seleccionada.");
                        return true;
                    }

                    Vector max = cuboidRegion.getMaximumPoint();
                    Vector min = cuboidRegion.getMinimumPoint();

                    int xDif = max.getBlockX() - min.getBlockX();
                    int yDif = max.getBlockY() - min.getBlockY();
                    int zDif = max.getBlockZ() - min.getBlockZ();
                    if (xDif > 20 || yDif > 40 || zDif > 20) {
                        p.sendMessage(prefix + "Los §oassets§r tienen un tamaño máximo de §a20x20x40§f bloques. Actual: §a" + xDif + "x" + zDif + "x" + yDif + "§f.");
                        return true;
                    }

                    BlockArrayClipboard clipboard = new BlockArrayClipboard(cuboidRegion);

                    ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                            plugin.getWorldEditWorld(), cuboidRegion, clipboard, region.getMaximumPoint()
                    );
                    Operations.complete(forwardExtentCopy);

                    try {
                        String id = plugin.getAssetsRegistry().create(name, clipboard, origins.get(p.getUniqueId()), p.getUniqueId());
                        p.sendMessage(prefix + "§oAsset§r §a" + name + "§f creado correctamente con la ID §a" + id + "§f.");
                    } catch (SQLException e) {
                        p.sendMessage(prefix + "Ha ocurrido un error en la base de datos.");
                    } catch (IOException e) {
                        p.sendMessage(prefix + "Ha ocurrido un error al escribir el archivo.");
                    }
                } catch (IncompleteRegionException e) {
                    p.sendMessage(prefix + "Selecciona una región cúbica.");
                } catch (WorldEditException e) {
                    p.sendMessage(prefix + "Algo ha salido mal.");
                }

                break;
            }
            case "editname": {

                if (!s.isBuilder()) {
                    p.sendMessage(prefix + "Deber haber terminado un proyecto para poder crear §oassets§r.");
                    return true;
                }

                if (args.length < 2) {
                    p.sendMessage(prefix + "Introduce una ID.");
                    return true;
                }

                String id = args[1];

                if (args.length < 3) {
                    p.sendMessage(prefix + "Introduce un nuevo nombre.");
                    return true;
                }

                String name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                if (!name.matches("[a-zA-Z0-9_ñÑ]{1,32}")) {
                    p.sendMessage(prefix + "Introduce un nuevo nombre válido.");
                    return true;
                }

                if (!plugin.getAssetsRegistry().exists(id)) {
                    p.sendMessage(prefix + "No existe un §oasset§r con esa ID.");
                    return true;
                }

                Asset asset = plugin.getAssetsRegistry().get(id);
                String oldName = asset.getName();

                if (asset.getCreator() != p.getUniqueId()) {
                    p.sendMessage(prefix + "No eres el creador de este §oasset§r por lo que no puedes editarlo.");
                    return true;
                }

                try {
                    asset.setName(name);
                    p.sendMessage(prefix + "Nombre del §oasset§r cambiado de §a" + oldName + "§f a §a" + name + "§f.");
                } catch (SQLException e) {
                    p.sendMessage(prefix + "Ha ocurrido un error en la base de datos.");
                }

                break;
            }
            case "editautorotate": {

                if (!s.isBuilder()) {
                    p.sendMessage(prefix + "Deber haber terminado un proyecto para poder crear §oassets§r.");
                    return true;
                }

                if (args.length < 2) {
                    p.sendMessage(prefix + "Introduce una ID.");
                    return true;
                }

                String id = args[1];

                if (args.length < 3) {
                    p.sendMessage(prefix + "Introduce §atrue§f o §cfalse§f.");
                    return true;
                }

                if (!args[2].equals("true") && !args[2].equals("false")) {
                    p.sendMessage(prefix + "Introduce §atrue§f o §cfalse§f.");
                    return true;
                }

                boolean autoRotate = args[2].equals("true");

                if (!plugin.getAssetsRegistry().exists(id)) {
                    p.sendMessage(prefix + "No existe un §oasset§r con esa ID.");
                    return true;
                }

                Asset asset = plugin.getAssetsRegistry().get(id);

                if (asset.getCreator() != p.getUniqueId()) {
                    p.sendMessage(prefix + "No eres el creador de este §oasset§r por lo que no puedes editarlo.");
                    return true;
                }

                try {
                    asset.setAutoRotate(autoRotate);
                    p.sendMessage(prefix + "Rotación automática del §oasset§r establecida en " + (autoRotate ? "§averdadero" : "§cfalso") + "§f.");
                } catch (SQLException e) {
                    p.sendMessage(prefix + "Ha ocurrido un error en la base de datos.");
                }

                break;
            }
            case "delete": {

            }
            case "search": {

                if (args.length < 2) {



                } else {
                    String input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                }

            }
        }

        return true;
    }
}
