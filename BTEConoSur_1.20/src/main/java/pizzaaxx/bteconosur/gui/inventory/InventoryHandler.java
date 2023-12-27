package pizzaaxx.bteconosur.gui.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryHandler implements Listener {

    private final BTEConoSurPlugin plugin;
    private final Map<UUID, InventoryGUI> guis = new HashMap<>();
    private final Map<UUID, Integer> currentPage = new HashMap<>();

    public InventoryHandler(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    public void openInventory(UUID uuid, InventoryGUI gui) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        player.openInventory(gui.getInventory());
        guis.put(uuid, gui);
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        if (guis.containsKey(uuid)) {
            InventoryGUI gui = guis.get(uuid);
            InventoryClickAction action = gui.getAction(event.getSlot());
            boolean draggable = gui.isDraggable(event.getSlot());
            if (!draggable) {
                event.setCancelled(true);
            }
            action.execute(event);
        }
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();
        guis.remove(uuid);
    }
}
