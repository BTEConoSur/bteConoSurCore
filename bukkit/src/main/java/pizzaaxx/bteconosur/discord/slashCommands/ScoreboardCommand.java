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

import static pizzaaxx.bteconosur.country.OldCountry.countryNames;
import static pizzaaxx.bteconosur.discord.HelpMethods.errorEmbed;

public class ScoreboardCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("scoreboard")) {
            OldCountry country = null;
            if (event.getOption("país") == null) {
                if (event.getGuild().getId().equals("696154248593014815")) {
                    event.replyEmbeds(errorEmbed("En este servidor debes especificar el país.")).queue(
                            msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                    );
                    return;
                } else {
                    country = new OldCountry(event.getGuild());
                }
            } else {
                String c = event.getOption("país").getAsString().replace("perú", "peru").toLowerCase();
                if (countryNames.contains(c)) {
                    country = new OldCountry(c);
                } else {
                    event.replyEmbeds(errorEmbed("El país introducido no es válido.")).queue(
                            msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                    );
                    return;
                }
            }

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.GREEN);
            builder.setTitle("Jugadores con mayor puntaje de " + StringUtils.capitalize(country.getName().replace("peru", "perú")));
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
                Bukkit.getConsoleSender().sendMessage("c");

                builder.addField(
                        "#" + i + " " + emoji + " " + s.getName() + " " + (dsc.isLinked() ? "- " + dsc.getName() + "#" + dsc.getDiscriminator() : ""),
                        "Puntos: `" + manager.getPoints(country) + "`\nProyectos terminados: `" + s.getProjectsManager().getFinishedProjects(country) + "`",
                        false);
                i++;
                Bukkit.getConsoleSender().sendMessage("d");
            }

            event.replyEmbeds(builder.build()).queue(
                    msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
            );
        }
    }
}
