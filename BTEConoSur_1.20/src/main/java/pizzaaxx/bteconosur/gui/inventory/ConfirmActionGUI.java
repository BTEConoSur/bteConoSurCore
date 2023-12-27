package pizzaaxx.bteconosur.gui.inventory;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.gui.Heads;

public class ConfirmActionGUI implements InventoryGUI {

    private final Runnable onConfirm;
    private final Runnable onCancel;

    public ConfirmActionGUI(Runnable onConfirm, Runnable onCancel) {
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9);
        // fill with background
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, InventoryGUI.BACKGROUND);
        }
        // confirm button
        inventory.setItem(5, Heads.CONFIRM);
        // cancel button
        inventory.setItem(3, Heads.CANCEL);
        return inventory;
    }

    @Override
    public @NotNull InventoryClickAction getAction(int slot) {
        if (slot == 5) {
            InventoryClickAction action = new InventoryClickAction();
            action.setLeftClickAction(event -> {
                onConfirm.run();
                event.getWhoClicked().closeInventory();
            });
            return action;
        } else if (slot == 3) {
            InventoryClickAction action = new InventoryClickAction();
            action.setLeftClickAction(event -> {
                onCancel.run();
                event.getWhoClicked().closeInventory();
            });
            return action;
        } else {
            return InventoryClickAction.EMPTY;
        }
    }

    @Override
    public boolean isDraggable(int slot) {
        return false;
    }

}
