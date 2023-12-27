package pizzaaxx.bteconosur.gui.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.gui.ItemBuilder;

public interface InventoryGUI {

    Inventory getInventory();

    @NotNull
    InventoryClickAction getAction(int slot);

    boolean isDraggable(int slot);

    ItemStack BACKGROUND = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE)
            .name("")
            .build();

}
