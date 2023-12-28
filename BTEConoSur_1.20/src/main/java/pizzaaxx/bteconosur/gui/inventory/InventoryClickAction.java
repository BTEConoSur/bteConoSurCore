package pizzaaxx.bteconosur.gui.inventory;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class InventoryClickAction {

    public static final InventoryClickAction EMPTY = new InventoryClickAction();

    private Consumer<InventoryClickEvent> leftClickAction;
    private Consumer<InventoryClickEvent> rightClickAction;
    private Consumer<InventoryClickEvent> shiftLeftClickAction;
    private Consumer<InventoryClickEvent> shiftRightClickAction;

    public static @NotNull InventoryClickAction of(
            @Nullable Consumer<InventoryClickEvent> leftClickAction,
            @Nullable Consumer<InventoryClickEvent> rightClickAction,
            @Nullable Consumer<InventoryClickEvent> shiftLeftClickAction,
            @Nullable Consumer<InventoryClickEvent> shiftRightClickAction
    ) {
        InventoryClickAction action = new InventoryClickAction();
        action.setLeftClickAction(leftClickAction);
        action.setRightClickAction(rightClickAction);
        action.setShiftLeftClickAction(shiftLeftClickAction);
        action.setShiftRightClickAction(shiftRightClickAction);
        return action;
    }

    public static @NotNull InventoryClickAction of(
            @NotNull Consumer<InventoryClickEvent> action
    ) {
        return InventoryClickAction.of(
                action,
                action,
                action,
                action
        );
    }

    public void setLeftClickAction(Consumer<InventoryClickEvent> leftClickAction) {
        this.leftClickAction = leftClickAction;
    }

    public void setRightClickAction(Consumer<InventoryClickEvent> rightClickAction) {
        this.rightClickAction = rightClickAction;
    }

    public void setShiftLeftClickAction(Consumer<InventoryClickEvent> shiftLeftClickAction) {
        this.shiftLeftClickAction = shiftLeftClickAction;
    }

    public void setShiftRightClickAction(Consumer<InventoryClickEvent> shiftRightClickAction) {
        this.shiftRightClickAction = shiftRightClickAction;
    }

    public void execute(@NotNull InventoryClickEvent event) {
        if (event.isLeftClick()) {
            if (event.isShiftClick()) {
                if (shiftLeftClickAction != null) {
                    shiftLeftClickAction.accept(event);
                }
            } else {
                if (leftClickAction != null) {
                    leftClickAction.accept(event);
                }
            }
        } else if (event.isRightClick()) {
            if (event.isShiftClick()) {
                if (shiftRightClickAction != null) {
                    shiftRightClickAction.accept(event);
                }
            } else {
                if (rightClickAction != null) {
                    rightClickAction.accept(event);
                }
            }
        }
    }
}
