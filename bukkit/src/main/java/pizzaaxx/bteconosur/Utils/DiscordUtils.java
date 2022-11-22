package pizzaaxx.bteconosur.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class DiscordUtils {

    public static void respondError(@NotNull SlashCommandInteractionEvent event, String error) {
        event.replyEmbeds(
                new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle(error)
                        .build()
        ).queue(
                msg -> msg.deleteOriginal().queueAfter(20, TimeUnit.SECONDS)
        );
    }

    public static void respondSuccess(@NotNull SlashCommandInteractionEvent event, String success) {
        event.replyEmbeds(
                new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle(success)
                        .build()
        ).queue();
    }

    public static void respondSuccess(@NotNull SlashCommandInteractionEvent event, String success, int deleteAfter) {
        event.replyEmbeds(
                new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle(success)
                        .build()
        ).queue(
                msg -> msg.deleteOriginal().queueAfter(deleteAfter, TimeUnit.SECONDS)
        );
    }

}
