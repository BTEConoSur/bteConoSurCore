package pizzaaxx.bteconosur.WorldEdit.Assets.Commands;

import com.google.common.collect.Lists;
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
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Inventory.InventoryGUI;
import pizzaaxx.bteconosur.Inventory.ItemBuilder;
import pizzaaxx.bteconosur.Player.Managers.WorldEditManager;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.WorldEdit.Assets.Asset;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

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
                    p.sendMessage(prefix + "Introduce un nombre para el §oasset§r.");
                    return true;
                }

                String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                if (!name.matches("[a-zA-Z0-9_ñÑ\\s]{1,32}")) {
                    p.sendMessage(prefix + "Introduce un nombre válido.");
                    return true;
                }

                if (plugin.getAssetsRegistry().getNames().contains(name)) {
                    p.sendMessage(prefix + "Ya existe un §oasset§r con ese nombre.");
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
                            plugin.getWorldEditWorld(), cuboidRegion, clipboard, region.getMinimumPoint()
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
            case "setname": {

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

                if (!name.matches("[a-zA-Z0-9_ñÑ\\s]{1,32}")) {
                    p.sendMessage(prefix + "Introduce un nuevo nombre válido.");
                    return true;
                }

                if (plugin.getAssetsRegistry().getNames().contains(name)) {
                    p.sendMessage(prefix + "Ya existe un §oasset§r con ese nombre.");
                    return true;
                }

                if (!plugin.getAssetsRegistry().exists(id)) {
                    p.sendMessage(prefix + "No existe un §oasset§r con esa ID.");
                    return true;
                }

                Asset asset = plugin.getAssetsRegistry().get(id);
                String oldName = asset.getName();

                if (!asset.getCreator().equals(p.getUniqueId())) {
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
            case "setautorotate": {

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

                if (!asset.getCreator().equals(p.getUniqueId())) {
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
            case "settags": {
                if (!s.isBuilder()) {
                    p.sendMessage(prefix + "Deber haber terminado un proyecto para poder crear §oassets§r.");
                    return true;
                }

                if (args.length < 2) {
                    p.sendMessage(prefix + "Introduce una ID.");
                    return true;
                }

                String id = args[1];

                Set<String> tags = new HashSet<>(
                        Arrays.asList(
                                Arrays.copyOfRange(args, 2, args.length)
                        )
                );

                if (!plugin.getAssetsRegistry().exists(id)) {
                    p.sendMessage(prefix + "No existe un §oasset§r con esa ID.");
                    return true;
                }

                Asset asset = plugin.getAssetsRegistry().get(id);

                if (!asset.getCreator().equals(p.getUniqueId())) {
                    p.sendMessage(prefix + "No eres el creador de este §oasset§r por lo que no puedes editarlo.");
                    return true;
                }

                try {
                    asset.setTags(tags);
                    if (tags.isEmpty()) {
                        p.sendMessage(prefix + "Has eliminado las etiquetas del §oasset§r han sido eliminadas.");
                    } else {
                        p.sendMessage(prefix + "Has establecido las etiquetas del §oasset§r en §a#" + String.join("§f, §a#", tags) + "§f.");
                    }
                } catch (SQLException e) {
                    p.sendMessage(prefix + "Ha ocurrido un error en la base de datos.");
                }

                break;
            }
            case "delete": {

            }
            case "search": {
                String input;
                if (args.length < 2) {
                    input = null;
                } else {
                    input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                }

                List<String> ids = plugin.getAssetsRegistry().getSearch(input);

                List<List<String>> idsLists = Lists.partition(ids, 45);
                int totalPages = idsLists.size();
                List<InventoryGUI> pages = new ArrayList<>();
                for (int i = 0; i < totalPages; i++) {
                    InventoryGUI gui = new InventoryGUI(
                            6,
                            "Assets (" + (i + 1) + "/" + totalPages + ")",
                            null
                    );
                    gui.setItems(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15).name(" ").build(), 0, 1, 2, 3, 4, 5, 6, 7, 8);

                    List<String> pageIDs = idsLists.get(i);
                    int j = 9;
                    for (String id : pageIDs) {
                        Asset asset = plugin.getAssetsRegistry().get(id);
                        WorldEditManager manager = s.getWorldEditManager();
                        ItemStack head = ItemBuilder.head(
                                this.getHeadValue(asset.getId()),
                                (s.getWorldEditManager().isFavourite(asset.getId()) ? "§6" + asset.getName() + " ⭐" : "§a" + asset.getName()),
                                new ArrayList<>(
                                        Arrays.asList(
                                                "§fID: §7" + asset.getId(),
                                                "§fCreador: §7" + plugin.getPlayerRegistry().get(asset.getCreator()).getName(),
                                                "§fRotación: §7" + (asset.isAutoRotate() ? "Automática" : "Manual"),
                                                (!asset.getTags().isEmpty() ? "§7#" + String.join(" #", asset.getTags()):""),
                                                "",
                                                "§7Haz click derecho para " + (manager.isFavourite(asset.getId()) ? "§celiminar§7 de" : "§aagregar§f a") + " favoritos."
                                        )
                                )
                        );
                        gui.setItem(head, j);
                        gui.setDraggable(j);
                        int finalI1 = i;
                        gui.setRCAction(
                                event -> {
                                    try {
                                        if (manager.isFavourite(asset.getId())) {
                                            manager.removeFavAsset(asset.getId());
                                            event.updateSlot("§a" + asset.getName());
                                            event.updateSlot(
                                                    new ArrayList<>(
                                                            Arrays.asList(
                                                                    "§fID: §7" + asset.getId(),
                                                                    "§fCreador: §7" + plugin.getPlayerRegistry().get(asset.getCreator()).getName(),
                                                                    "§fRotación: §7" + (asset.isAutoRotate() ? "Automática" : "Manual"),
                                                                    (!asset.getTags().isEmpty() ? "§7#" + String.join(" #", asset.getTags()):""),
                                                                    "",
                                                                    "§7Haz click derecho para §aagregar§7 a favoritos."
                                                            )
                                                    )
                                            );
                                            pages.set(finalI1, event.getGui());
                                        } else {
                                            manager.addFavAsset(asset.getId());
                                            event.updateSlot("§6" + asset.getName() + " ⭐");
                                            event.updateSlot(
                                                    new ArrayList<>(
                                                            Arrays.asList(
                                                                    "§fID: §7" + asset.getId(),
                                                                    "§fCreador: §7" + plugin.getPlayerRegistry().get(asset.getCreator()).getName(),
                                                                    "§fRotación: §7" + (asset.isAutoRotate() ? "Automática" : "Manual"),
                                                                    (!asset.getTags().isEmpty() ? "§7#" + String.join(" #", asset.getTags()):""),
                                                                    "",
                                                                    "§7Haz click derecho para §celiminar§7 de favoritos."
                                                            )
                                                    )
                                            );
                                            pages.set(finalI1, event.getGui());
                                        }
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                },
                                j
                        );
                        j++;
                    }
                    if (i > 0) {
                        gui.setItem(
                                ItemBuilder.head(
                                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzNlOTE5MTlkYjBhY2VmZGMyNzJkNjdmZDg3YjRiZTg4ZGM0NGE5NTg5NTg4MjQ0NzRlMjFlMDZkNTNlNiJ9fX0=",
                                        "Anterior (" + i + "/" + totalPages + ")",
                                        null
                                ),
                                0
                        );
                        int finalI = i;
                        gui.setLCAction(
                                event -> plugin.getInventoryHandler().open(p, pages.get(finalI - 1)),
                                0
                        );
                    }
                    if (i + 1 < totalPages) {
                        gui.setItem(
                                ItemBuilder.head(
                                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNmYzUyMjY0ZDhhZDllNjU0ZjQxNWJlZjAxYTIzOTQ3ZWRiY2NjY2Y2NDkzNzMyODliZWE0ZDE0OTU0MWY3MCJ9fX0=",
                                        "Siguiente (" + (i + 2) + "/" + totalPages + ")",
                                        null
                                ),
                                8
                        );
                        int finalI = i;
                        gui.setLCAction(
                                event -> plugin.getInventoryHandler().open(p, pages.get(finalI + 1)),
                                8
                        );
                    }
                    pages.add(gui);
                }
                plugin.getInventoryHandler().open(p, pages.get(0));
                break;
            }
            case "fav": {
                List<String> ids = new ArrayList<>(s.getWorldEditManager().getFavAssets());
                if (ids.isEmpty()) {
                    p.sendMessage(prefix + "No tienes assets favoritos. Selecciona algunos en §a/asset search§f.");
                    return true;
                }

                List<List<String>> idsLists = Lists.partition(ids, 45);
                int totalPages = idsLists.size();
                List<InventoryGUI> pages = new ArrayList<>();
                for (int i = 0; i < totalPages; i++) {
                    InventoryGUI gui = new InventoryGUI(
                            6,
                            "Assets favoritos (" + (i + 1) + "/" + totalPages + ")",
                            null
                    );
                    gui.setItems(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15).name(" ").build(), 0, 1, 2, 3, 4, 5, 6, 7, 8);

                    List<String> pageIDs = idsLists.get(i);
                    int j = 9;
                    for (String id : pageIDs) {
                        Asset asset = plugin.getAssetsRegistry().get(id);
                        WorldEditManager manager = s.getWorldEditManager();
                        ItemStack head = ItemBuilder.head(
                                this.getHeadValue(asset.getId()),
                                (s.getWorldEditManager().isFavourite(asset.getId()) ? "§6" + asset.getName() + " ⭐" : "§a" + asset.getName()),
                                new ArrayList<>(
                                        Arrays.asList(
                                                "§fID: §7" + asset.getId(),
                                                "§fCreador: §7" + plugin.getPlayerRegistry().get(asset.getCreator()).getName(),
                                                "§fRotación: §7" + (asset.isAutoRotate() ? "Automática" : "Manual"),
                                                (!asset.getTags().isEmpty() ? "§7#" + String.join(" #", asset.getTags()):""),
                                                "",
                                                "§7Haz click derecho para " + (manager.isFavourite(asset.getId()) ? "§celiminar§7 de" : "§aagregar§f a") + " favoritos."
                                        )
                                )
                        );
                        gui.setItem(head, j);
                        gui.setDraggable(j);
                        int finalI1 = i;
                        gui.setRCAction(
                                event -> {
                                    try {
                                        if (manager.isFavourite(asset.getId())) {
                                            manager.removeFavAsset(asset.getId());
                                            event.updateSlot("§a" + asset.getName());
                                            event.updateSlot(
                                                    new ArrayList<>(
                                                            Arrays.asList(
                                                                    "§fID: §7" + asset.getId(),
                                                                    "§fCreador: §7" + plugin.getPlayerRegistry().get(asset.getCreator()).getName(),
                                                                    "§fRotación: §7" + (asset.isAutoRotate() ? "Automática" : "Manual"),
                                                                    (!asset.getTags().isEmpty() ? "§7#" + String.join(" #", asset.getTags()):""),
                                                                    "",
                                                                    "§7Haz click derecho para §aagregar§7 a favoritos."
                                                            )
                                                    )
                                            );
                                            pages.set(finalI1, event.getGui());
                                        } else {
                                            manager.addFavAsset(asset.getId());
                                            event.updateSlot("§6" + asset.getName() + " ⭐");
                                            event.updateSlot(
                                                    new ArrayList<>(
                                                            Arrays.asList(
                                                                    "§fID: §7" + asset.getId(),
                                                                    "§fCreador: §7" + plugin.getPlayerRegistry().get(asset.getCreator()).getName(),
                                                                    "§fRotación: §7" + (asset.isAutoRotate() ? "Automática" : "Manual"),
                                                                    (!asset.getTags().isEmpty() ? "§7#" + String.join(" #", asset.getTags()):""),
                                                                    "",
                                                                    "§7Haz click derecho para §celiminar§7 de favoritos."
                                                            )
                                                    )
                                            );
                                            pages.set(finalI1, event.getGui());
                                        }
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                },
                                j
                        );
                        j++;
                    }
                    if (i > 0) {
                        gui.setItem(
                                ItemBuilder.head(
                                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzNlOTE5MTlkYjBhY2VmZGMyNzJkNjdmZDg3YjRiZTg4ZGM0NGE5NTg5NTg4MjQ0NzRlMjFlMDZkNTNlNiJ9fX0=",
                                        "Anterior (" + i + "/" + totalPages + ")",
                                        null
                                ),
                                0
                        );
                        int finalI = i;
                        gui.setLCAction(
                                event -> plugin.getInventoryHandler().open(p, pages.get(finalI - 1)),
                                0
                        );
                    }
                    if (i + 1 < totalPages) {
                        gui.setItem(
                                ItemBuilder.head(
                                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNmYzUyMjY0ZDhhZDllNjU0ZjQxNWJlZjAxYTIzOTQ3ZWRiY2NjY2Y2NDkzNzMyODliZWE0ZDE0OTU0MWY3MCJ9fX0=",
                                        "Siguiente (" + (i + 2) + "/" + totalPages + ")",
                                        null
                                ),
                                8
                        );
                        int finalI = i;
                        gui.setLCAction(
                                event -> plugin.getInventoryHandler().open(p, pages.get(finalI + 1)),
                                8
                        );
                    }
                    pages.add(gui);
                }
                plugin.getInventoryHandler().open(p, pages.get(0));
                break;
            }
        }
        return true;
    }

    private final List<String> headOptions = new ArrayList<>(
            Arrays.asList(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODAzNzYyNTRkNTczNWFiMTU2N2IzZjg3YzdmYmRlNDFkZmM0MTYyMmI2NTEwYjY2N2Q4MmM5ZWZlOGE1Y2VkMSJ9fX0=",
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGYwZWYxNjc0YjcxZjhkYWQ3ZjU4ODJmMTNjZjI0MTE2NTNmNmEyZjllOTBlNDViMTgxZjQ0YjllZWYyNzhmZiJ9fX0=",
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTNjMjQ3YjBkODJkZDg3MTQ4Yzk2NTZhNDJlMDI0MDcwYzQ1OTcwZTExNDlmZGM1NTNlZjYzNjBmMjc5OWM2YyJ9fX0=",
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDhlZGYwYzMxODYyNDM2M2MzMzI2YTY3NzQ1MmY5ZTk0MGNiNjk3NjMwNWY3NTg3NWFlMzMwZDE1ZDE1N2FlNCJ9fX0=",
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODBjNDUyOGU2MjJiZDMxODcyMGQzOGUwZTQ1OTllNjliZjIzMzA4Zjg5NjkzOTIwZTBlNGVjYjU1ZDFjMGJhYyJ9fX0="
            )
    );

    private String getHeadValue(@NotNull String id) {
        int sum = 0;
        for (char character : id.toCharArray()) {
            sum += character;
        }
        int option = sum % 5;
        return headOptions.get(option);
    }
}
