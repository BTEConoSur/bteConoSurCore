package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.server.player.DiscordManager;
import pizzaaxx.bteconosur.server.player.PointsManager;

import java.awt.*;

public class ScoreboardCommand implements EventListener {

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent e = (MessageReceivedEvent) event;
            if (e.getMessage().getContentRaw().startsWith("/")) {
                String fullCommand = e.getMessage().getContentRaw();
                String[] args = fullCommand.replaceFirst("/", "").split(" ");
                if (args.length > 0) {
                    if (args[0].equals("scoreboard")) {
                        OldCountry country;
                        if (args.length > 1) {
                            if (new OldCountry(args[1]).getName() != null) {
                                country = new OldCountry(args[1]);
                            } else {
                                EmbedBuilder error = new EmbedBuilder();
                                error.setColor(new Color(255,0,0));
                                error.setAuthor("El país introducido no existe.");
                                e.getTextChannel().sendMessageEmbeds(error.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                                return;
                            }
                        } else {
                            if (e.getGuild().getId().equals("696154248593014815")) {
                                EmbedBuilder uruguay = new EmbedBuilder();
                                uruguay.setColor(new Color(255,0,0));
                                uruguay.setAuthor("En este servidor debes introducir explícitamente el país.");
                                e.getTextChannel().sendMessageEmbeds(uruguay.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                                return;
                            }
                            country = new OldCountry(e.getGuild());
                        }

                        if (country.getName().equals("argentina")) {
                            EmbedBuilder error = new EmbedBuilder();
                            error.setColor(new Color(255,0,0));
                            error.setAuthor("Argentina no trabaja con puntos.");
                            e.getTextChannel().sendMessageEmbeds(error.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                            return;
                        }

                        EmbedBuilder top = new EmbedBuilder();
                        top.setColor(new Color(0, 255, 42));
                        top.setTitle("Jugadores con el mayor puntaje de " + country.getName().replace("peru", "perú").toUpperCase());
                        int i = 1;
                        for (PointsManager pointsManager : country.getScoreboard()) {
                            String emoji;
                            int points = pointsManager.getPoints(country);
                            if (points >= 1000) {
                                emoji = ":gem:";
                            } else if (points >= 500) {
                                emoji = ":crossed_swords:";
                            } else if (points >= 150) {
                                emoji = ":tools:";
                            } else {
                                emoji = ":hammer:";
                            }
                            DiscordManager dsc = pointsManager.getServerPlayer().getDiscordManager();
                            top.addField("#" + i + " " + emoji + " " + pointsManager.getServerPlayer().getName() + " " + (dsc.isLinked() ? "- " + pointsManager.getServerPlayer().getDiscordManager().isLinked() + "#" + dsc.getDiscriminator() : ""),
                                   "Puntos: `" + pointsManager.getPoints(country) + "`\nProyectos terminados: `" + pointsManager.getServerPlayer().getProjectsManager().getFinishedProjects(country) + "`", false);
                            i++;
                        }
                        e.getTextChannel().sendMessageEmbeds(top.build()).reference(e.getMessage()).mentionRepliedUser(false).queue();
                    }
                }
            }
        }
    }
}
