package pizzaaxx.bteconosur.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InventoryGUI {
    private int rows;
    private String title;
    private final Map<Integer, ItemStack> items;
    private final Set<Integer> draggableSlots = new HashSet<>();
    private ItemStack background;
    private final Map<Integer, InventoryAction> actions;

    /**
     * Creates a new InventoryGUI with a non-default background.
     * @param rows The amount of rows of this inventory. Can be set to a maximum of 6.
     * @param title The title of this inventory.
     * @param background A custom item for all the empty slots. Can be set to null.
     */
    public InventoryGUI(int rows, @NotNull String title, @Nullable ItemStack background) {
        this.rows = rows;
        this.title = title;
        this.items = new HashMap<>();
        this.actions = new HashMap<>();
        this.background = background;
    }

    /**
     * Creates a new InventoryGUI with a default background (Black Stained Glass Panes).
     * @param rows The amount of rows of this inventory. Can be set to a maximum of 6.
     * @param title The title of this inventory.
     */
    public InventoryGUI(int rows, @NotNull String title) {
        this.rows = rows;
        this.title = title;
        this.items = new HashMap<>();
        this.actions = new HashMap<>();
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta meta = background.getItemMeta();
        meta.setDisplayName(" ");
        background.setItemMeta(meta);
        this.background = background;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setTitle(@NotNull String title) {
        this.title = title;
    }

    public void setBackground(@Nullable ItemStack background) {
        this.background = background;
    }

    public void setEmptySlot(int slot) {
        items.put(slot, null);
    }

    public void setEmptySlot(@NotNull int... slots) {
        for (int slot : slots) {
            this.setEmptySlot(slot);
        }
    }

    public void setDraggable(int slot) {
        this.draggableSlots.add(slot);
    }

    public void setItem(ItemStack item, int slot) {
        items.put(slot, item);
    }

    public void setItems(ItemStack itemStack, @NotNull int... slots) {
        for (int slot : slots) {
            this.setItem(itemStack, slot);
        }
    }

    public void setItem(Material material, int slot) {
        items.put(slot, new ItemStack(material));
    }

    public void setItems(Material material, @NotNull int... slots) {
        for (int slot : slots) {
            this.setItem(material, slot);
        }
    }

    public void setAction(InventoryAction action, int slot) {
        actions.put(slot, action);
    }

    public void setCommand(@NotNull String command, int slot) {
        this.setAction(
                event -> event.getPlayer().performCommand(command),
                slot
        );
    }

    public void setTP(Location loc, int slot) {
        this.setAction(
                event -> event.getPlayer().teleport(loc),
                slot
        );
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @Nullable
    public InventoryAction getAction(int slot) {
        return actions.get(slot);
    }

    public boolean isDraggable(int slot) {
        return this.draggableSlots.contains(slot);
    }

    @NotNull
    public Inventory buildInventory() {
        Inventory inventory = Bukkit.createInventory(
                null,
                rows * 9,
                title
        );

        if (this.background != null) {
            for (int i = 0; i < rows * 9; i++) {
                if (!items.containsKey(i)) {
                    inventory.setItem(i, this.background);
                }
            }
        }

        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue());
        }

        return inventory;
    }
}
