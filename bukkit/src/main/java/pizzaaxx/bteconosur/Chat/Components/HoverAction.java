package pizzaaxx.bteconosur.Chat.Components;

import org.bukkit.ChatColor;

/**
 * A piece of text that is shown when the cursor is placed on top of the text.
 */
public class HoverAction {

    private final String message;
    private final ChatColor color;

    public HoverAction(String message) {
        this(message, null);
    }

    public HoverAction(String message, ChatColor color) {
        this.message = message;
        this.color = color;
    }

    public String getString() {
        return color.toString() + message;
    }

}
