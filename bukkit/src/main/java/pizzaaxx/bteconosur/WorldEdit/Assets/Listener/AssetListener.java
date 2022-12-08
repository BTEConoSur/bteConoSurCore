package pizzaaxx.bteconosur.WorldEdit.Assets.Listener;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.WorldEdit.Assets.Asset;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class AssetListener implements Listener {

    private final BTEConoSur plugin;
    private final Random random = new Random();

    public AssetListener(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, Map<String, Double>> rotations = new HashMap<>();

    @EventHandler
    public void onClick(@NotNull PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.getType() == Material.SKULL_ITEM) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                String lore = String.join("~", meta.getLore());
                UUID uuid = event.getPlayer().getUniqueId();
                if (lore.contains("ID:") && lore.contains("Creador:") && lore.contains("Rotación")) {
                    event.setCancelled(true);
                    String id = ChatColor.stripColor(meta.getLore().get(0)).replace("ID: ", "");
                    Asset asset = plugin.getAssetsRegistry().get(id);
                    switch (event.getAction()) {
                        case RIGHT_CLICK_BLOCK: {
                            try {
                                BlockFace face = event.getBlockFace();
                                Location location = event.getClickedBlock().getLocation().add(face.getModX(), face.getModY(), face.getModZ());
                                asset.loadSchematic();
                                double rotation;
                                if (asset.isAutoRotate()) {
                                    rotation = random.nextInt(4) * 90;
                                } else {
                                    rotation = this.rotations.getOrDefault(uuid, new HashMap<>()).getOrDefault(id, 0.0);
                                }
                                asset.paste(event.getPlayer(), new Vector(location.getX(), location.getY(), location.getZ()), rotation);
                            } catch (IOException | WorldEditException e) {
                                e.printStackTrace();
                                event.getPlayer().sendActionBar("§cHa ocurrido un error.");
                            }
                            break;
                        }
                        case LEFT_CLICK_AIR: {
                            if (asset.isAutoRotate()) {
                                event.getPlayer().sendActionBar("§eEste §e§oasset§e tiene rotación automática.");
                                return;
                            }

                            Map<String, Double> rotations = this.rotations.getOrDefault(uuid, new HashMap<>());
                            double rotation = rotations.getOrDefault(id, 0.0);
                            if (rotation == 270) {
                                rotation = 0;
                            } else {
                                rotation += 90;
                            }
                            rotations.put(id, rotation);
                            this.rotations.put(uuid, rotations);

                            event.getPlayer().sendActionBar("§a§oAsset§a rotado 90° en sentido antihorario.");
                            break;
                        }
                        case RIGHT_CLICK_AIR: {
                            if (asset.isAutoRotate()) {
                                event.getPlayer().sendActionBar("§eEste §e§oasset§e tiene rotación automática.");
                                return;
                            }

                            Map<String, Double> rotations = this.rotations.getOrDefault(uuid, new HashMap<>());
                            double rotation = rotations.getOrDefault(id, 0.0);
                            if (rotation == 0) {
                                rotation = 270;
                            } else {
                                rotation -= 90;
                            }
                            rotations.put(id, rotation);
                            this.rotations.put(uuid, rotations);

                            event.getPlayer().sendActionBar("§a§oAsset§a rotado 90° en sentido horario.");
                            break;
                        }
                    }
                }
            }
        }
    }
}
