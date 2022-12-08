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

public class AssetListener implements Listener {

    private final BTEConoSur plugin;

    public AssetListener(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(@NotNull PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item != null && item.getType() == Material.SKULL_ITEM) {
                ItemMeta meta = item.getItemMeta();
                if (meta.hasLore()) {
                    String lore = String.join("~", meta.getLore());
                    if (lore.contains("ID:") && lore.contains("Creador:") && lore.contains("Rotación")) {
                        event.setCancelled(true);
                        String id = ChatColor.stripColor(meta.getLore().get(0)).replace("ID: ", "");
                        Asset asset = plugin.getAssetsRegistry().get(id);
                        try {
                            BlockFace face = event.getBlockFace();
                            Location location = event.getClickedBlock().getLocation().add(face.getModX(), face.getModY(), face.getModZ());
                            asset.loadSchematic();
                            asset.paste(event.getPlayer(), new Vector(location.getX(), location.getY(), location.getZ()));
                        } catch (IOException | WorldEditException e) {
                            e.printStackTrace();
                            event.getPlayer().sendActionBar("§cHa ocurrido un error.");
                        }
                    }
                }
            }
        }
    }
}
