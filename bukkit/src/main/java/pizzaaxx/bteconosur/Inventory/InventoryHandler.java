package pizzaaxx.bteconosur.Inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryHandler implements Listener {

    private final BTEConoSur plugin;

    private final Map<UUID, InventoryGUI> openedInventories = new HashMap<>();

    public InventoryHandler(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public void open(@NotNull Player p, InventoryGUI inventoryGUI) {
        openedInventories.put(p.getUniqueId(), inventoryGUI);
        p.openInventory(inventoryGUI.buildInventory());
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (openedInventories.containsKey(event.getWhoClicked().getUniqueId())) {
            InventoryGUI gui = openedInventories.get(event.getWhoClicked().getUniqueId());
            if (event.getClickedInventory().getName().equals(gui.getTitle())) {
                InventoryAction action;
                if (event.isLeftClick()) {
                    if (!gui.isDraggable(event.getSlot())) {
                        event.setCancelled(true);
                    }
                    action = gui.getLCAction(event.getSlot());
                } else {
                    event.setCancelled(true);
                    action = gui.getRCAction(event.getSlot());
                }
                if (action != null) {
                    InventoryGUIClickEvent clickEvent = new InventoryGUIClickEvent(
                            (Player) event.getWhoClicked(),
                            gui,
                            event.getSlot(),
                            event.getClickedInventory());
                    action.exec(clickEvent);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        if (openedInventories.containsKey(event.getPlayer().getUniqueId())) {
            InventoryGUI gui = openedInventories.get(event.getPlayer().getUniqueId());
            if (event.getInventory().getName().equals(gui.getTitle())) {
                openedInventories.remove(event.getPlayer().getUniqueId());
            }
        }
    }

}
