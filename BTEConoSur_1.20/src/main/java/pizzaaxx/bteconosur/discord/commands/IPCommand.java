package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class IPCommand extends ListenerAdapter implements DiscordCommandHolder {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("ip")) {
            event.replyEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("IP del servidor")
                            .addField(
                                    "Java (1.9 - 1.20.4)",
                                    "`bteconosur.com`",
                                    false
                            )
                            .addField(
                                    "Bedrock",
                                    "No disponible.",
                                    false
                            )
                            .setThumbnail("https://media.discordapp.net/attachments/807694452214333482/845857288609988628/conosur.png")
                            .build()
            ).queue();
        }

    }

    @Override
    public CommandData[] getCommandData() {
        return new CommandData[] {
                Commands.slash("ip", "Obtén la IP del servidor.")
        };
    }
}
