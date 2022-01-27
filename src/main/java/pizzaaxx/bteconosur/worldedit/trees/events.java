package pizzaaxx.bteconosur.worldedit.trees;

import com.sk89q.worldedit.Vector;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class events implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (Objects.equals(e.getItem(), new ItemStack(Material.SNOW))) {
            try {
                new Tree("test").place(new Vector(e.getClickedBlock().getX(), e.getClickedBlock().getY() + 1, e.getClickedBlock().getY()), e.getPlayer());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
