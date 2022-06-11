package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.events.ServerEvent;
import pizzaaxx.bteconosur.helper.Pair;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.discord.HelpMethods.errorEmbed;
import static pizzaaxx.bteconosur.misc.Misc.getMapURL;

public class EventCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        if (e.getName().equals("evento")) {
            String c = e.getSubcommandName();

            if (c == null) {
                e.replyEmbeds(errorEmbed("Ha ocurrido un error.")).queue(
                        msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                );
                return;
            }

            OldCountry country = new OldCountry(c);

            ServerEvent event = new ServerEvent(country);

            EmbedBuilder builder = new EmbedBuilder();

            ServerEvent.Status status = event.getStatus();

            if (status == ServerEvent.Status.OFF) {
                builder.setColor(Color.RED);
                builder.setTitle("El evento " + (c.equals("global") ? c : "de " + StringUtils.capitalize(c.replace("peru", "perú"))) + " está inactivo.");
                e.replyEmbeds(builder.build()).queue(
                        msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
                );
            } else {
                builder.setTitle("Evento " + (c.equals("global") ? c : "de " + StringUtils.capitalize(c.replace("peru", "perú"))) + (event.getName().equals("notSet") ? "" : " \"" + event.getName() + "\""));

                if (status == ServerEvent.Status.READY) {
                    builder.setColor(Color.YELLOW);
                    builder.addField("Estado:", ":yellow_circle: Preparado", false);
                } else {
                    builder.setColor(Color.GREEN);
                    builder.addField("Estado:", ":green_circle: Activo", false);
                }

                if (!event.getDate().equals("notSet")) {
                    builder.addField(":calendar_spiral: Fecha:", event.getDate(), false);
                }

                builder.addField(":chart_with_upwards_trend: Puntos mínimos:", "`" + event.getMinPoints() + "`", false);
                Location loc = event.getTp();
                builder.addField(":round_pushpin: Coordenadas:", "> " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ(), false);
                List<String> names = new ArrayList<>();
                event.getParticipants().forEach(
                        player -> {
                            ServerPlayer s = new ServerPlayer(player);
                            names.add(s.getName().replace("_", "\\_"));
                        }
                );
                builder.addField(":busts_in_silhouette: Participantes actuales:", (names.isEmpty() ? "No hay." : String.join(", ", names)), false);
                if (event.getImage().equals("notSet")) {
                    try {
                        builder.setImage("attachment://map.png");
                        e.replyFile(new URL(getMapURL(new Pair<>(event.getRegion().getPoints(), "7434eb"))).openStream(), "map.png").addEmbeds(builder.build()).queue();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                } else {
                    builder.setImage(event.getImage());
                    e.replyEmbeds(builder.build()).queue();
                }
            }

        }
    }

}
