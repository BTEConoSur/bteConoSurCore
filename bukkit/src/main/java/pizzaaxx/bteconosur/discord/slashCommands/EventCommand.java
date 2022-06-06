package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.events.ServerEvent;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class EventCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        if (e.getName().equals("event")) {
            String c = e.getSubcommandName();

            OldCountry country = new OldCountry(c);

            ServerEvent event = new ServerEvent(country);

            EmbedBuilder builder = new EmbedBuilder();

            ServerEvent.Status status = event.getStatus();

            if (status == ServerEvent.Status.OFF) {
                builder.setColor(Color.RED);
                builder.setTitle("El evento " + (c.equals("global") ? c : "de " + StringUtils.capitalize(c.replace("peru", "perú"))) + " está inactivo.");
            } else {
                builder.setTitle("Evento " + (c.equals("global") ? c : "de " + StringUtils.capitalize(c.replace("peru", "perú"))) + " \"" + event.getName() + "\"");

                if (status == ServerEvent.Status.READY) {
                    builder.setColor(Color.YELLOW);
                    builder.addField("Estado:", ":yellow_circle: Preparado", false);
                } else {
                    builder.setColor(Color.GREEN);
                    builder.addField("Estado:", ":green_circle: Activo", false);
                }


            }

            e.replyEmbeds(builder.build()).queue(
                    msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
            );

        }
    }

}
