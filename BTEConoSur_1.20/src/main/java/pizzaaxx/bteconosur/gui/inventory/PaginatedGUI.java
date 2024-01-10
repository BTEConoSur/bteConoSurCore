package pizzaaxx.bteconosur.gui.inventory;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import org.apache.commons.math3.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pizzaaxx.bteconosur.gui.Heads;
import pizzaaxx.bteconosur.gui.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PaginatedGUI implements InventoryGUI {

    private final List<Pair<ItemStack, InventoryClickAction>> items = new ArrayList<>();
    private final Map<Integer, Pair<ItemStack, InventoryClickAction>> staticItems = new HashMap<>();
    private final Component title;
    private final boolean draggable;
    private final int[] slots;
    private final int backButtonSlot;
    private final int nextButtonSlot;
    private int currentPage = 0;

    public PaginatedGUI(Component title, boolean draggable, int[] slots, int backButtonSlot, int nextButtonSlot) {
        this.title = title;
        this.draggable = draggable;
        this.slots = slots;
        this.backButtonSlot = backButtonSlot;
        this.nextButtonSlot = nextButtonSlot;
    }

    @Override
    public @Nullable Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(
                null,
                54,
                title
        );
        // fill everyithing but slots with background
        for (int i = 0; i < 54; i++) {
            boolean isSlot = false;
            for (int slot : slots) {
                if (i == slot) {
                    isSlot = true;
                    break;
                }
            }
            if (!isSlot) {
                inventory.setItem(i, InventoryGUI.BACKGROUND);
            }
        }

        // add static items
        for (Map.Entry<Integer, Pair<ItemStack, InventoryClickAction>> entry : staticItems.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getFirst());
        }

        if (items.isEmpty()) return inventory;

        // partition items into slots size
        List<List<Pair<ItemStack, InventoryClickAction>>> partitions = Lists.partition(items, slots.length);
        // add items of first partition to inventory
        for (int i = 0; i < slots.length; i++) {
            if (partitions.get(0).size() > i) {
                Pair<ItemStack, InventoryClickAction> pair = partitions.get(0).get(i);
                inventory.setItem(slots[i], pair.getFirst());
            }
        }

        // add next button if there is more than one partition
        if (partitions.size() > 1) {
            inventory.setItem(
                    nextButtonSlot,
                    ItemBuilder.head(
                            Heads.NEXT_VALUE,
                            "§aSiguiente (Página " + (currentPage + 2) + "/" + partitions.size() + ")",
                            null
                    )
            );
        }

        return inventory;
    }

    @Override
    public @NotNull InventoryClickAction getAction(int slot) {
        // create partition and get current page
        List<List<Pair<ItemStack, InventoryClickAction>>> partitions = Lists.partition(items, slots.length);

        if (slot == backButtonSlot) {

            Consumer<InventoryClickEvent> consumer = event -> {
                // check if current page is first page
                if (currentPage == 0) return;
                // set current page to previous page
                currentPage--;

                // get previous partition
                List<Pair<ItemStack, InventoryClickAction>> previousPartition = partitions.get(currentPage);
                // clear slots in inventory
                for (int j : slots) {
                    event.getInventory().clear(j);
                }

                // add items of previous partition to inventory
                for (int i = 0; i < slots.length; i++) {
                    if (previousPartition.size() > i) {
                        Pair<ItemStack, InventoryClickAction> pair = previousPartition.get(i);
                        event.getInventory().setItem(slots[i], pair.getFirst());
                    }
                }

                // if new page is first page hide back button, else update the page number
                if (currentPage == 0) {
                    event.getInventory().setItem(backButtonSlot, InventoryGUI.BACKGROUND);
                } else {
                    event.getInventory().setItem(
                            nextButtonSlot,
                            ItemBuilder.head(
                                    Heads.BACK_VALUE,
                                    "§aAnterior (Página " + (currentPage) + "/" + partitions.size() + ")",
                                    null
                            )
                    );
                }

                event.getInventory().setItem(
                        nextButtonSlot,
                        ItemBuilder.head(
                                Heads.NEXT_VALUE,
                                "§aSiguiente (Página " + (currentPage + 2) + "/" + partitions.size() + ")",
                                null
                        )
                );

            };

            return InventoryClickAction.of(
                    consumer,
                    consumer,
                    consumer,
                    consumer
            );

        } else if (slot == nextButtonSlot) {

            Consumer<InventoryClickEvent> consumer = event -> {
                // check if current page is last page
                if (currentPage == partitions.size() - 1) return;
                // set current page to next page
                currentPage++;

                // get next partition
                List<Pair<ItemStack, InventoryClickAction>> nextPartition = partitions.get(currentPage);
                // clear slots in inventory
                for (int j : slots) {
                    event.getInventory().clear(j);
                }

                // add items of next partition to inventory
                for (int i = 0; i < slots.length; i++) {
                    if (nextPartition.size() > i) {
                        Pair<ItemStack, InventoryClickAction> pair = nextPartition.get(i);
                        event.getInventory().setItem(slots[i], pair.getFirst());
                    }
                }

                // if new page is last page hide next button
                if (currentPage == partitions.size() - 1) {
                    event.getInventory().setItem(nextButtonSlot, InventoryGUI.BACKGROUND);
                } else {
                    event.getInventory().setItem(
                            nextButtonSlot,
                            ItemBuilder.head(
                                    Heads.NEXT_VALUE,
                                    "§aSiguiente (Página " + (currentPage + 2) + "/" + partitions.size() + ")",
                                    null
                            )
                    );
                }

                event.getInventory().setItem(
                        backButtonSlot,
                        ItemBuilder.head(
                                Heads.BACK_VALUE,
                                "§aAnterior (Página " + (currentPage) + "/" + partitions.size() + ")",
                                null
                        )
                );

            };

            return InventoryClickAction.of(
                    consumer,
                    consumer,
                    consumer,
                    consumer
            );

        } else if (staticItems.containsKey(slot)) {
            Pair<ItemStack, InventoryClickAction> pair = staticItems.get(slot);
            return pair.getSecond();
        } else {
            // get index of slot in slots
            int index = -1;
            for (int i = 0; i < slots.length; i++) {
                if (slots[i] == slot) {
                    index = i;
                    break;
                }
            }

            List<Pair<ItemStack, InventoryClickAction>> partition;
            if (partitions.isEmpty()) partition = new ArrayList<>();
            else partition = partitions.get(currentPage);

            // get item from partition
            if (index == -1) return InventoryClickAction.EMPTY;
            if (partition.size() <= index) return InventoryClickAction.EMPTY;

            Pair<ItemStack, InventoryClickAction> pair = partition.get(index);
            return pair.getSecond();
        }
    }

    @Override
    public boolean isDraggable(int slot) {
        return draggable;
    }

    public void addItem(ItemStack item, InventoryClickAction action) {
        items.add(new Pair<>(item, action));
    }

    public void addStaticItem(int slot, ItemStack item, InventoryClickAction action) {
        staticItems.put(slot, new Pair<>(item, action));
    }

    public static @NotNull PaginatedGUI fullscreen(Component name, boolean draggable) {
        return new PaginatedGUI(
                name,
                draggable,
                new int[] {
                        10,11,12,13,14,15,16,
                        19,20,21,22,23,24,25,
                        28,29,30,31,32,33,34,
                        37,38,39,40,41,42,43,
                },
                45,
                53
        );
    }
}
