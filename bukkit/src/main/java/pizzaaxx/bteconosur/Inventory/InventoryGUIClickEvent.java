package pizzaaxx.bteconosur.Inventory;

import org.bukkit.entity.Player;

public class InventoryGUIClickEvent {

    private final Player player;
    private final InventoryGUI gui;
    private final int slot;

    public InventoryGUIClickEvent(Player player, InventoryGUI gui, int slot) {
        this.player = player;
        this.gui = gui;
        this.slot = slot;
    }

    public Player getPlayer() {
        return player;
    }

    public InventoryGUI getGui() {
        return gui;
    }

    public int getSlot() {
        return slot;
    }
}
