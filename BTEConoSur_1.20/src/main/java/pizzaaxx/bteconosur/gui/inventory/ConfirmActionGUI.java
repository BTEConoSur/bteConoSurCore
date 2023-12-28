package pizzaaxx.bteconosur.gui.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.gui.Heads;

import java.util.ArrayList;
import java.util.List;

public class ConfirmActionGUI implements InventoryGUI {

    private final Component title;
    private final Runnable onConfirm;
    private final Runnable onCancel;
    private List<Component> confirmLore = new ArrayList<>();
    private List<Component> cancelLore = new ArrayList<>();

    public ConfirmActionGUI(Component title, Runnable onConfirm, Runnable onCancel) {
        this.title = title;
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(null, 9, title);
        // fill with background
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, InventoryGUI.BACKGROUND);
        }
        // confirm button
        ItemStack confirmHead = Heads.CONFIRM.clone();
        confirmHead.editMeta(meta -> {
            List<Component> lore = meta.lore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.addAll(confirmLore);
            meta.lore(lore);
        });
        inventory.setItem(5, confirmHead);

        // cancel button
        ItemStack cancelHead = Heads.CANCEL.clone();
        cancelHead.editMeta(meta -> {
            List<Component> lore = meta.lore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.addAll(cancelLore);
            meta.lore(lore);
        });
        inventory.setItem(3, cancelHead);
        return inventory;
    }

    public void setConfirmLore(List<Component> confirmLore) {
        this.confirmLore = confirmLore;
    }

    public void setCancelLore(List<Component> cancelLore) {
        this.cancelLore = cancelLore;
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
