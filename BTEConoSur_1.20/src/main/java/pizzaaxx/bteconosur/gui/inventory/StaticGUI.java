package pizzaaxx.bteconosur.gui.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class StaticGUI implements InventoryGUI {

    private final int rows;
    private final Component title;
    private final Map<Integer, Pair<ItemStack, InventoryClickAction>> slots = new HashMap<>();

    public StaticGUI(int rows, Component title) {
        this.rows = rows;
        this.title = title;
    }

    public void addItem(int slot, ItemStack item, InventoryClickAction action) {
        slots.put(slot, new Pair<>(item, action));
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(
                null,
                rows * 9,
                title
        );
        // fill with background
        for (int i = 0; i < rows * 9; i++) {
            inventory.setItem(i, InventoryGUI.BACKGROUND);
        }

        // add items
        for (Map.Entry<Integer, Pair<ItemStack, InventoryClickAction>> entry : slots.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getKey());
        }
        return inventory;
    }

    @Override
    public @NotNull InventoryClickAction getAction(int slot) {
        return slots.containsKey(slot) ? slots.get(slot).getValue() : InventoryClickAction.EMPTY;
    }

    @Override
    public boolean isDraggable(int slot) {
        return false;
    }
}
