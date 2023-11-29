package pizzaaxx.bteconosur.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryGUIClickEvent {

    private final Player player;
    private final InventoryGUI gui;
    private final int slot;
    private final InventoryDataSet data;
    private final Inventory inventory;

    public InventoryGUIClickEvent(Player player, @NotNull InventoryGUI gui, int slot, Inventory inventory) {
        this.player = player;
        this.gui = gui;
        this.slot = slot;
        this.inventory = inventory;
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

    public InventoryGUI getGUI() {
        return gui;
    }

    public int getSlot() {
        return slot;
    }

    /**
     * Close the current GUI.
     */
    public void closeGUI() {
        player.closeInventory();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void updateSlot(ItemStack stack) {
        this.updateSlot(stack, slot);
    }

    public void updateSlot(String name){
        this.updateSlot(name, slot);
    }

    public void updateSlot(List<String> lore) {
        this.updateSlot(lore, slot);
    }

    public void updateSlot(ItemStack stack, int slot) {
        inventory.setItem(slot, stack);
        gui.setItem(stack, slot);
    }

    public void updateSlot(String name, int slot) {
        ItemStack stack = inventory.getItem(slot);
        assert stack != null;
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text(name));
        stack.setItemMeta(meta);
        inventory.setItem(slot, stack);
        gui.setItem(stack, slot);
    }

    public void updateSlot(@NotNull List<String> lore, int slot) {
        ItemStack stack = inventory.getItem(slot);
        assert stack != null;
        ItemMeta meta = stack.getItemMeta();
        meta.lore(lore.stream().map(Component::text).collect(Collectors.toList()));
        stack.setItemMeta(meta);
        inventory.setItem(slot, stack);
        gui.setItem(stack, slot);
    }
}
