package pizzaaxx.bteconosur.Discord;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.Utils.DiscordUtils;
import pizzaaxx.bteconosur.Utils.StringUtils;

import java.util.Map;

public class DiscordHandler extends ListenerAdapter {

    public Button getDeleteButton(@NotNull User user) {
        return Button.of(
                ButtonStyle.DANGER,
                "deleteButton?user=" + user.getId(),
                "Eliminar",
                Emoji.fromUnicode("U+1F5D1")
        );
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String id = event.getButton().getId();
        assert id != null;
        if (id.startsWith("deleteButton")) {
            Map<String, String> query = StringUtils.getQuery(id.split("\\?")[1]);
            if (!query.get("user").equals(event.getUser().getId())) {
                DiscordUtils.respondError(event, "No puedes usar esto.");
                return;
            }
            event.getMessage().delete().queue();
        }
    }
}
