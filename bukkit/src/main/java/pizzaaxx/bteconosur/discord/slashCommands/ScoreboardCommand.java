package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.server.player.DiscordManager;
import pizzaaxx.bteconosur.server.player.PointsManager;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import java.awt.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.discord.HelpMethods.errorEmbed;

public class ScoreboardCommand extends ListenerAdapter {

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
            OldCountry country = new OldCountry(subcommand.replace("perú", "peru"));

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.GREEN);
            builder.setTitle("Jugadores con mayor puntaje " + (country.getName().equals("global") ? "global" : "de " + StringUtils.capitalize(country.getName().replace("peru", "perú"))));
            int i = 1;

            for (UUID uuid : country.getScoreboardUUIDs()) {

                String emoji;
                ServerPlayer s = new ServerPlayer(uuid);

                PointsManager manager;
                manager = s.getPointsManager(); // <--- PROBLEM
                int points = manager.getPoints(country);
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


                builder.addField(
                        "#" + i + " " + emoji + " " + s.getName() + " " + (dsc.isLinked() ? "- " + dsc.getName() + "#" + dsc.getDiscriminator() : ""),
                        "Puntos: `" + points + "`\nProyectos terminados: `" + s.getProjectsManager().getFinishedProjects(country) + "`",
                        false);
                i++;

            }


            event.replyEmbeds(builder.build()).queue(
                    msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
            );
        }
    }
}
