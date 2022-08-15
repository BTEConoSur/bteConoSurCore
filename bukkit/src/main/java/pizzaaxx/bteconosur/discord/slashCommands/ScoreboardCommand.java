package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.Points.PointsContainer;
import pizzaaxx.bteconosur.ServerPlayer.Managers.DiscordManager;
import pizzaaxx.bteconosur.ServerPlayer.Managers.PointsManager;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;
import pizzaaxx.bteconosur.country.Country;

import java.awt.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.discord.HelpMethods.errorEmbed;

public class ScoreboardCommand extends ListenerAdapter {

    private final BteConoSur plugin;

    public ScoreboardCommand(BteConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("scoreboard")) {
            String subcommand = event.getSubcommandName();
            if (subcommand == null) {
                event.replyEmbeds(errorEmbed("Ha ocurrido un error.")).queue(
                        msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                );
                return;
            }

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.GREEN);

            PointsContainer container;
            if (subcommand.equals("global")) {
                container = plugin;
                builder.setTitle("Jugadores con mayor puntaje global");
            } else {
                Country country = plugin.getCountryManager().get(subcommand.replace("perú", "peru"));
                container = country;
                builder.setTitle("Jugadores con mayor puntaje de " + StringUtils.capitalize(country.getName().replace("peru", "perú")));
            }

            int i = 1;
            for (UUID uuid : container.getMaxPoints()) {

                String emoji;
                ServerPlayer s = plugin.getPlayerRegistry().get(uuid);

                PointsManager manager;
                manager = s.getPointsManager(); // <--- PROBLEM
                int points = manager.getPoints(container);
                if (points >= 1000) {
                    emoji = ":gem:";
                } else if (points >= 500) {
                    emoji = ":crossed_swords:";
                } else if (points >= 150) {
                    emoji = ":tools:";
                } else {
                    emoji = ":hammer:";
                }
                DiscordManager dsc = s.getDiscordManager();

                int finished = s.getProjectsManager().getFinishedProjects(container);

                builder.addField(
                        "#" + i + " " + emoji + " " + s.getName() + " " + (dsc.isLinked() ? "- " + dsc.getName() + "#" + dsc.getDiscriminator() : ""),
                        "Puntos: `" + points + "`\nProyectos terminados: `" + finished + "`",
                        false);
                i++;

            }


            event.replyEmbeds(builder.build()).queue(
                    msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
            );
        }
    }
}
