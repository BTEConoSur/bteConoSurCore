package pizzaaxx.bteconosur.discord.slashCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.helper.DataTime;
import pizzaaxx.bteconosur.helper.DateHelper;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.server.player.*;
import pizzaaxx.bteconosur.yaml.Configuration;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static pizzaaxx.bteconosur.BteConoSur.key;
import static pizzaaxx.bteconosur.discord.HelpMethods.errorEmbed;
public class PlayerCommand extends ListenerAdapter {

    private final Configuration groupEmojis;

    public PlayerCommand(Configuration groupEmojis) {
        this.groupEmojis = groupEmojis;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("player")) {

            ServerPlayer s = null;
            if (event.getSubcommandName().equals("user")) {


                User user = null;
                OptionMapping option = event.getOption("usuario");
                if (option != null) {
                    user = option.getAsUser();
                }


                try {

                    s = new ServerPlayer(user);

                } catch (Exception e) {
                    event.replyEmbeds(errorEmbed("El usuario introducido no tiene una cuenta de Minecraft conectada.")).queue(
                            msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                    );
                }

            } else {

                OfflinePlayer player = Bukkit.getOfflinePlayer(event.getOption("nombre").getAsString());

                if (!player.hasPlayedBefore()) {
                    event.replyEmbeds(errorEmbed("El jugador introducido nunca ha entrado al servidor.")).queue(
                            msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                    );
                    return;
                }

                s = new ServerPlayer(player);

            }

            if (s != null) {
                boolean hasFile = false;
                InputStream file = null;

                EmbedBuilder embedBuilder = new EmbedBuilder();

                embedBuilder.setTitle(s.getName());
                OfflinePlayer offlinePlayer = s.getPlayer();

                if (offlinePlayer.isOnline()) {
                    Player player = offlinePlayer.getPlayer();
                    OldCountry country = new OldCountry(player.getLocation());
                    Coords2D coords = new Coords2D(player.getLocation());

                    embedBuilder
                            .setColor(new Color(0, 255, 42))
                            .addField("Status:", ":green_circle: Online", false)
                            .addField("Coordenadas:", (country.getName().equals("global") ? ":globe_with_meridians" : ":flag_" + country.getAbbreviation() + ":") + " " + Double.toString(Math.floor(coords.getX())).replace(".0", "") + " " + player.getLocation().getBlockY() + " " + Double.toString(Math.floor(coords.getZ())).replace(".0", ""), false)
                            .addField("Coordenadas geográficas:", (country.getName().equals("global") ? ":globe_with_meridians" : ":flag_" + country.getAbbreviation() + ":") + " [" + coords.getLat() + ", " + coords.getLon() + "](" + "https://www.google.com/maps/@" + coords.getLat() + "," + coords.getLon() + ",19z" + ")", false);

                    String chatType;
                    String chatName = s.getChatManager().getChat().getName();

                    if (chatName.startsWith("project_")) {
                        chatType = ":tools:";
                    } else {
                        chatType = (chatName.equals("global") ? ":earth_americas:" : ":flag_" + new OldCountry(chatName).getAbbreviation() + ":");
                    }
                    embedBuilder.addField("Chat:", chatType + " " + s.getChatManager().getChat().getFormattedName(), false);

                    try {
                        file = new URL("https://open.mapquestapi.com/staticmap/v4/getmap?key=" + key + "&size=1280,720&type=sat&scalebar=false&imagetype=png&center=" + coords.getLat() + "," + coords.getLon() + "&zoom=18&xis=https://cravatar.eu/helmavatar/" + player.getName() + "/64.png,1,c," + coords.getLat() + "," + coords.getLon()).openStream();
                        hasFile = true;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        return;
                    }
                    embedBuilder.setImage("attachment://map.png");

                } else {

                    embedBuilder.setColor(Color.RED)
                            .addField("Status:", ":red_circle: Offline", false);

                    DataTime time = DateHelper.differenceToData(
                            new Date(offlinePlayer.getLastPlayed()),
                            new Date()
                    );

                    embedBuilder.addField("Última vez conectado: ",
                            (time.get(DateHelper.Type.MINUTE) > 0 ? "Hace " + (time.get(DateHelper.Type.HOUR) > 0 ? time.get(DateHelper.Type.HOUR) + " hora(s) y " : "") + time.get(DateHelper.Type.MINUTE) + " minuto(s)" : "Recién"), false);

                }

                if (s.getDiscordManager().isLinked()) {
                    DiscordManager dscManager = s.getDiscordManager();
                    dscManager.loadUser();
                    embedBuilder.addField("Discord:", s.getDiscordManager().getUser().getAsMention(), false);
                } else {
                    embedBuilder.addField("Discord:", "No conectado.", false);
                }

                GroupsManager groups = s.getGroupsManager();

                embedBuilder.addField("Rango:",
                        groupEmojis.getString(groups.getPrimaryGroup().toString()) + " " + groups.getPrimaryGroup().toString().replace("default", "visita").toUpperCase(), false);

                if (groups.getSecondaryGroups().size() > 0) {
                    List<String> ranks = new ArrayList<>();
                    for (GroupsManager.SecondaryGroup secondaryGroup : groups.getSecondaryGroups()) {
                        ranks.add(groupEmojis.getString(secondaryGroup.toString()) + " " + secondaryGroup.toString().toUpperCase());
                    }
                    embedBuilder.addField("Rangos secundarios:", String.join("\n", ranks), false);
                }

                PointsManager pointsManager = s.getPointsManager();
                if (pointsManager.getMaxPoints() != null && pointsManager.getMaxPoints().getValue() > 0) {
                    List<String> points = new ArrayList<>();


                    for (Map.Entry<OldCountry, Integer> entry : pointsManager.getSorted().entrySet()) {
                        String bRank;
                        if (entry.getValue() >= 1000) {
                            bRank = ":gem:";
                        } else if (entry.getValue() >= 500) {
                            bRank = ":crossed_swords:";
                        } else if (entry.getValue() >= 150) {
                            bRank = ":hammer_pick:";
                        } else {
                            bRank = ":hammer:";
                        }
                        points.add("• :flag_" + entry.getKey().getAbbreviation() + ": " + bRank + " " + StringUtils.capitalize(entry.getKey().getName().replace("peru", "perú")) + ": " + entry.getValue());
                    }

                    embedBuilder.addField("Puntos:", String.join("\n", points), false);
                }

                ProjectsManager projects = s.getProjectsManager();

                embedBuilder.addField("Proyectos terminados:", Integer.toString(projects.getTotalFinishedProjects()), false);

                List<String> allProjects = projects.getAllProjects();
                if (allProjects.size() > 0) {
                    List<String> projectsLines = new ArrayList<>();
                    boolean max = false;

                    for (String id : projects.getAllOwnedProjects()) {
                        Project project = new Project(id);
                        projectsLines.add("• :flag_" + project.getCountry().getAbbreviation() + ": " + project.getDifficulty().toString().toLowerCase().replace("facil", ":green_circle:").replace("intermedio", ":yellow_circle:").replace("dificil", ":red_circle:") + " :crown: `" + project.getId() + "`" + ((!project.getName().equals(project.getId())) ? " - " + project.getName() : ""));
                        if (projectsLines.size() >= 15) {
                            projectsLines.add("y " + (allProjects.size() - 15) + " más...");
                            max = true;
                            break;
                        }
                    }


                    if (!max) {
                        for (String id : allProjects) {
                            Project project = new Project(id);
                            if (project.getOwner() != s.getPlayer()) {
                                projectsLines.add("• :flag_" + project.getCountry().getAbbreviation() + ": " + project.getDifficulty().toString().toLowerCase().replace("facil", ":green_circle:").replace("intermedio", ":yellow_circle:").replace("dificil", ":red_circle:") + " `" + project.getId() + "`" + ((!project.getName().equals(project.getId())) ? " - " + project.getName() : ""));
                            }
                            if (projectsLines.size() >= 15) {
                                projectsLines.add("y " + (allProjects.size() - 15) + " más...");
                                break;
                            }
                        }
                    }
                    embedBuilder.addField("Proyectos activos (Total: " + allProjects.size() + "):", String.join("\n", projectsLines), false);
                }

                embedBuilder.setThumbnail("https://mc-heads.net/head/" + s.getPlayer().getUniqueId());

                if (hasFile) {
                    event.replyFile(file, "map.png").addEmbeds(embedBuilder.build()).queue(
                            msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
                    );
                } else {
                    event.replyEmbeds(embedBuilder.build()).queue(
                            msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.MINUTES)
                    );
                }


            } else {
                event.replyEmbeds(errorEmbed("Algo ha salido mal...")).queue(
                        msg -> msg.deleteOriginal().queueAfter(10, TimeUnit.SECONDS)
                );
            }

        }
    }
}
