package pizzaaxx.bteconosur.Chat.Components;

/**
 * An action performed when the player clicks the text.
 */
public class ClickAction {

    private final String action;

    /**
     *
     * @param action The action to perform. It can be a URL or a command.
     */
    public ClickAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
