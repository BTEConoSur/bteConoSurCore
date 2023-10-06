package pizzaaxx.bteconosur.WorldEdit.GridLock;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Geo.Coords2D;
import pizzaaxx.bteconosur.Inventory.ItemBuilder;
import pizzaaxx.bteconosur.Utils.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class GridLock implements CommandExecutor, Listener {

    private final Map<UUID, Pair<Coords2D, Coords2D>> userCoordinates = new HashMap<>();
    private final Map<UUID, Location> firstLocations = new HashMap<>();
    private final BTEConoSur plugin;
    private final Map<UUID, Set<Vector>> changedBlocks = new HashMap<>();
    private final Map<UUID, Long> lastUpdated = new HashMap<>();

    public GridLock(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, @NotNull String[] args) {
        // /gl set [lat] [lon]
        // /gl <t|tool>

        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player p = (Player) commandSender;

        if (args.length < 1) {
            p.sendMessage(plugin.getWorldEdit().getPrefix() + "Introduce un subcomando.");
            return true;
        }

        switch (args[0]) {
            case "t":
            case "tool": {
                p.getInventory().addItem(
                        ItemBuilder.of(Material.IRON_AXE)
                                .name("§dGridLock Tool")
                                .build()
                );
                break;
            }
            case "set": {
                if (args.length < 2) {

                    Region region;
                    try {
                        region = plugin.getWorldEdit().getSelection(p);
                    } catch (IncompleteRegionException e) {
                        p.sendMessage(plugin.getWorldEdit().getPrefix() + "Selecciona una región cúbica completa.");
                        return true;
                    }

                    if (!(region instanceof CuboidRegion)) {
                        p.sendMessage(plugin.getWorldEdit().getPrefix() + "Selecciona una región cúbica.");
                        return true;
                    }

                    CuboidRegion cuboidRegion = (CuboidRegion) region;
                    userCoordinates.put(
                            p.getUniqueId(),
                            new Pair<>(
                                    new Coords2D(plugin, cuboidRegion.getMinimumPoint().toVector2D().toBlockVector2D()),
                                    new Coords2D(plugin, cuboidRegion.getMaximumPoint().toVector2D().toBlockVector2D())
                            )
                    );

                    p.sendMessage(plugin.getWorldEdit().getPrefix() + "Coordenadas de GridLock establecidas correctamente.");

                } else {
                    if (args.length < 5) {
                        p.sendMessage(plugin.getWorldEdit().getPrefix() + "Introduce dos pares de coordenadas.");
                        return true;
                    }

                    String coordinatesString = Arrays.stream(Arrays.copyOfRange(args, 1, args.length))
                            .map(arg -> arg.charAt(arg.length() - 1) == ',' ? arg.substring(0, arg.length() - 2) : arg)
                            .collect(Collectors.joining(","));

                    if (!coordinatesString.matches(
                            "^-?[1-9]\\d{0,2}\\.\\d+,-?[1-9]\\d{0,2}\\.\\d+,-?[1-9]\\d{0,2}\\.\\d+,-?[1-9]\\d{0,2}\\.\\d+$"
                    )) {
                        p.sendMessage(plugin.getWorldEdit().getPrefix() + "Introduce coordenadas válidas.");
                        return true;
                    }

                    String[] coords = coordinatesString.split(",");
                    float lon1 = Float.parseFloat(coords[0]);
                    float lat1 = Float.parseFloat(coords[1]);
                    float lon2 = Float.parseFloat(coords[2]);
                    float lat2 = Float.parseFloat(coords[3]);

                    userCoordinates.put(
                            p.getUniqueId(),
                            new Pair<>(
                                    new Coords2D(plugin, lat1, lon1),
                                    new Coords2D(plugin, lat2, lon2)
                            )
                    );

                    p.sendMessage(plugin.getWorldEdit().getPrefix() + "Coordenadas de GridLock establecidas correctamente.");
                }
                break;
            }
        }
        return true;
    }

    @EventHandler
    public void onClick(@NotNull PlayerInteractEvent event) {
        if (
                event.getItem() != null
                && event.getItem().getType() == Material.IRON_AXE
                && event.getItem().hasItemMeta()
                && event.getItem().getItemMeta().hasDisplayName()
                && ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).equals("GridLock Tool")
        ) {
            event.setCancelled(true);
            Player p = event.getPlayer();
            Block targetBlock = p.getTargetBlock(null, 50);
            if (p.getTargetBlock(null, 50) != null) {

                if (!userCoordinates.containsKey(p.getUniqueId())) {
                    p.sendMessage(plugin.getWorldEdit().getPrefix() + "No has definido coordenadas de referencia.");
                }

                if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
                    this.firstLocations.put(p.getUniqueId(), targetBlock.getLocation());
                } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {

                    if (!firstLocations.containsKey(event.getPlayer().getUniqueId())) {
                        p.sendMessage(plugin.getWorldEdit().getPrefix() + "No has definido un punto de inicio.");
                        return;
                    }

                    Vector[] targetPoints = this.calculateRay(p, p.getLocation());

                    com.sk89q.worldedit.Vector v1 = new com.sk89q.worldedit.Vector(
                            targetPoints[0].getX(),
                            targetPoints[0].getY(),
                            targetPoints[0].getZ()
                    );

                    com.sk89q.worldedit.Vector v2 = new com.sk89q.worldedit.Vector(
                            targetPoints[1].getX(),
                            targetPoints[1].getY(),
                            targetPoints[1].getZ()
                    );

                    plugin.getWorldEdit().setSelection(
                            p,
                            new CuboidSelection(
                                    plugin.getWorld(),
                                    v1,
                                    v2
                            )
                    );
                }
            } else {
                p.sendMessage(plugin.getWorldEdit().getPrefix() + "No se ha encontrado un bloque objetivo.");
            }
        }
    }

    public Vector[] calculateRay(Player p, Location loc) {
        Vector u0 = firstLocations.get(p.getUniqueId()).toVector();

        Pair<Coords2D, Coords2D> userCoords = userCoordinates.get(p.getUniqueId());
        Vector u = new Vector(
                userCoords.getKey().getX() - userCoords.getValue().getX(),
                u0.getY(),
                userCoords.getKey().getZ() - userCoords.getValue().getZ()
        ).normalize();

        List<Vector> rotatedVectors = Arrays.asList(
                u,
                this.rotate(u, 45),
                this.rotate(u, 90),
                this.rotate(u, 135),
                this.rotate(u, 180),
                this.rotate(u, 225),
                this.rotate(u, 270),
                this.rotate(u, 315)
        );

        Vector v = loc.getDirection();
        Vector v0 = loc.toVector();

        List<Vector> sortedVectors = rotatedVectors.stream().sorted((o1, o2) -> Double.compare(
                getMinimumDistance(v0, v, u0, o1),
                getMinimumDistance(v0, v, u0, o2)
        )).collect(Collectors.toList());

        Vector[] pointsInRays = this.findPointsOfMinimumDistance(v0, v, u0, sortedVectors.get(0));
        return new Vector[] {
                u0,
                pointsInRays[1]
        };
    }

    @EventHandler
    public void onMoveOrRotation(@NotNull PlayerMoveEvent event) {
        if (userCoordinates.get(event.getPlayer().getUniqueId()) == null) {
            return;
        }

        if (firstLocations.get(event.getPlayer().getUniqueId()) == null) {
            return;
        }

        if (lastUpdated.get(event.getPlayer().getUniqueId()) != null && System.currentTimeMillis() - lastUpdated.get(event.getPlayer().getUniqueId()) < 1000) {
            return;
        }

        Vector[] targetPoints = this.calculateRay(event.getPlayer(), event.getTo());

        this.lastUpdated.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());

        if (changedBlocks.containsKey(event.getPlayer().getUniqueId())) {
            for (Vector oldChangedVector : changedBlocks.get(event.getPlayer().getUniqueId())) {
                Location loc = new Location(
                        plugin.getWorld(),
                        oldChangedVector.getX(),
                        oldChangedVector.getY(),
                        oldChangedVector.getZ()
                );
                event.getPlayer().sendBlockChange(
                        loc,
                        loc.getBlock().getType(),
                        loc.getBlock().getData()
                );
            }
        }

        for (com.sk89q.worldedit.Vector weVector : plugin.getWorldEdit().getBlocksInLine(
                new com.sk89q.worldedit.Vector(
                        targetPoints[0].getX(),
                        targetPoints[0].getY(),
                        targetPoints[0].getZ()
                ),
                new com.sk89q.worldedit.Vector(
                        targetPoints[1].getX(),
                        targetPoints[1].getY(),
                        targetPoints[1].getZ()
                )
        )) {
            Set<Vector> changedBlocks = this.changedBlocks.getOrDefault(event.getPlayer().getUniqueId(), new HashSet<>());
            changedBlocks.add(
                    new Vector(
                            weVector.getX(),
                            weVector.getY(),
                            weVector.getZ()
                    )
            );
            this.changedBlocks.put(event.getPlayer().getUniqueId(), changedBlocks);
            event.getPlayer().sendBlockChange(
                    new Location(
                            plugin.getWorld(),
                            weVector.getX(),
                            weVector.getY(),
                            weVector.getZ()
                    ),
                    Material.WOOL,
                    (byte) 1
            );
        }
    }

    public double getMinimumDistance(@NotNull Vector v0, @NotNull Vector v, Vector u0, Vector u) {
        Vector deltaV = v0.subtract(u0);
        double a = v.dot(v);
        double b = v.dot(u);
        double c = u.dot(u);
        double d = v.dot(deltaV);
        double e = u.dot(deltaV);

        double denominator = a * c - b * b;

        if (denominator == 0) {
            return deltaV.length();
        }

        double t1 = (d * c - b * e) / denominator;
        double t2 = (a * e - b * d) / denominator;

        // Calculate the points where the minimum distance is achieved
        Vector pointOnRay1 = v0.add(v.multiply(t1));
        Vector pointOnRay2 = u0.add(u.multiply(t2));

        // Calculate and return the minimum distance
        return pointOnRay1.distance(pointOnRay2);
    }

    public Vector[] findPointsOfMinimumDistance(@NotNull Vector v0, @NotNull Vector v, Vector u0, Vector u) {
        Vector deltaV = v0.subtract(u0);
        double a = v.dot(v);
        double b = v.dot(u);
        double c = u.dot(u);
        double d = v.dot(deltaV);
        double e = u.dot(deltaV);

        double denominator = a * c - b * b;

        if (denominator == 0) {
            // Rays are parallel, return initial points
            return new Vector[] { v0, u0 };
        }

        double t1 = (d * c - b * e) / denominator;
        double t2 = (a * e - b * d) / denominator;

        // Calculate the points where the minimum distance is achieved
        Vector pointOnRay1 = v0.add(v.multiply(t1));
        Vector pointOnRay2 = u0.add(u.multiply(t2));

        return new Vector[] { pointOnRay1, pointOnRay2 };
    }

    public Vector rotate(@NotNull Vector v, int angle) {
        double rad = Math.toRadians(angle);
        return new Vector(
                v.getX() * Math.cos(rad) - v.getZ() * Math.sin(rad),
                v.getY(),
                v.getX() * Math.sin(rad) + v.getZ() * Math.cos(rad)
        );
    }
}
