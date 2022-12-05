package pizzaaxx.bteconosur.Inventory;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InventoryGUIClickEvent {

    private final Player player;
    private final InventoryGUI gui;
    private final int slot;
    private final InventoryDataSet data;

    public InventoryGUIClickEvent(Player player, @NotNull InventoryGUI gui, int slot) {
        this.player = player;
        this.gui = gui;
        this.slot = slot;
        this.data = gui.getData(slot);
    }

    public boolean hasData() {
        return data != null;
    }

    public InventoryDataSet getData() {
        return data;
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

    public void closeGUI() {
        player.closeInventory();
    }
}
