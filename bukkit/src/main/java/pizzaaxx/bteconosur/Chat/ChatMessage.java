package pizzaaxx.bteconosur.Chat;

import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.Chat.Components.ChatMessageComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatMessage {

    private final List<ChatMessageComponent> components = new ArrayList<>();

    public ChatMessage(String message) {
        this.append(
                new ChatMessageComponent(
                        message
                )
        );
    }

    public ChatMessage(ChatMessageComponent @NotNull ... components) {
        for (ChatMessageComponent component : components) {
            this.append(
                    component
            );
        }
    }

    public void append(@NotNull ChatMessageComponent ... components) {
        this.components.addAll(Arrays.asList(components));
    }

    public ChatMessageComponent[] getChatComponents() {
        return components.toArray(new ChatMessageComponent[0]);
    }
    public BaseComponent[] getBaseComponents() {
        List<BaseComponent> baseComponents = new ArrayList<>();
        for (ChatMessageComponent chatComponent : components) {
            baseComponents.add(chatComponent.getBaseComponent());
        }
        return baseComponents.toArray(new BaseComponent[0]);
    }

}
