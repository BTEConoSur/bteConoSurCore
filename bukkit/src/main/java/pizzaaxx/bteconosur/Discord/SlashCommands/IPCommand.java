package pizzaaxx.bteconosur.Discord.SlashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class IPCommand extends ListenerAdapter implements SlashCommandContainer {

    private final BTEConoSur plugin;

    public IPCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("ip")) {
            event.replyEmbeds(
                            new EmbedBuilder()
                                    .setColor(Color.GREEN)
                                    .setTitle("IP del servidor")
                                    .addField(
                                            "Java 1.7-1.16.5:", "`bteconosur.com`", false
                                    )
                                    .addField("Bedrock:", "No disponible por el momento.", false)
                                    .setThumbnail("https://media.discordapp.net/attachments/807694452214333482/845857288609988628/conosur.png")
                                    .build()
                    )
                    .addActionRow(
                            plugin.getDiscordHandler().getDeleteButton(event.getUser())
                    )
                    .queue(
                            msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
                    );
        }

    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash("ip", "Obtén la IP del servidor.");
    }

    @Override
    public JDA getJDA() {
        return plugin.getBot();
    }
}
