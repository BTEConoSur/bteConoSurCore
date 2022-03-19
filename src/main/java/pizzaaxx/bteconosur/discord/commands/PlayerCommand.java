package pizzaaxx.bteconosur.discord.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.serverPlayer.*;
import pizzaaxx.bteconosur.coords.Coords2D;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.country.CountryPlayer;
import pizzaaxx.bteconosur.helper.DataTime;
import pizzaaxx.bteconosur.helper.DateHelper;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.yaml.Configuration;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static pizzaaxx.bteconosur.BteConoSur.key;
import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.discord.Bot.conoSurBot;

public class PlayerCommand extends ListenerAdapter {

    private static final String COMMAND_PREFIX = "/";
    private static final String COMMAND = "player";

    private static final Color ERROR_COLOR = new Color(255, 0, 0);

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        CompletableFuture.runAsync(() -> {
            Message message = event.getMessage();
            TextChannel textChannel = message.getTextChannel();
            String contentRaw = message.getContentRaw();

            if (contentRaw.startsWith(COMMAND_PREFIX)) {
                String[] args = contentRaw.replaceFirst("/", "")
                        .split(" ");

                if (args.length > 0 && args[0].equals(COMMAND)) {

                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
                    ServerPlayer serverPlayer = null;

                    if (offlinePlayer.hasPlayedBefore()) {
                        serverPlayer = new ServerPlayer(offlinePlayer);
                    } else {
                        String id;

                        if (args[1].startsWith("<@!")) {
                            id = args[1].replace("<@!", "")
                                    .replace(">", "");
                        } else {
                            id = args[1];
                        }

                        User user = conoSurBot.retrieveUserById(id)
                                .complete();

                        if (user != null) {
                            try {
                                serverPlayer = new ServerPlayer(user);
                            } catch (Exception e) {
                                textChannel.sendMessageEmbeds(
                                                new EmbedBuilder()
                                                        .setColor(ERROR_COLOR)
                                                        .setAuthor("El usuario introducido no tiene una cuenta de Minecraft conectada.")
                                                        .build())
                                        .reference(message)
                                        .mentionRepliedUser(false).queue();
                            }
                        }

                    }

                    final boolean[] hasFile = {false};
                    final InputStream[] file = {null};
                    EmbedBuilder embedBuilder = new EmbedBuilder();

                    if (serverPlayer != null) {
                        embedBuilder.setTitle(serverPlayer.getName());
                        offlinePlayer = serverPlayer.getPlayer();

                        if (offlinePlayer.isOnline()) {
                            org.bukkit.entity.Player player = offlinePlayer.getPlayer();
                            Country country = new Country(player.getLocation());
                            Coords2D coords = new Coords2D(player.getLocation());

                            embedBuilder
                                    .setColor(new Color(0, 255, 42))
                                    .addField("Status:", ":green_circle: Online", false)
                                    .addField("Coordenadas:", (country.getName().equals("global") ? ":globe_with_meridians" : ":flag_" + country.getAbbreviation() + ":") + " " + coords.getX() + ", " + coords.getZ(), false)
                                    .addField("Coordenadas geográficas:", (country.getName().equals("global") ? ":globe_with_meridians" : ":flag_" + country.getAbbreviation() + ":") + " [" + coords.getLat() + ", " + coords.getLon() + "](" + "https://www.google.com/maps/@" + coords.getLat() + "," + coords.getLon() + ",19z" + ")", false);

                            String chatType;
                            String chatName = serverPlayer.getChatManager().getChat().getName();

                            if (chatName.startsWith("project_")) {
                                chatType = ":tools:";
                            } else {
                                chatType = (chatName.equals("global") ? ":earth_americas:" : ":flag_" + new Country(chatName).getAbbreviation() + ":");
                            }
                            embedBuilder.addField("Chat:", chatType + " " + serverPlayer.getChatManager().getChat().getFormattedName(), false);

                            try {
                                file[0] = new URL("https://open.mapquestapi.com/staticmap/v4/getmap?key=" + key + "&size=1280,720&type=sat&scalebar=false&imagetype=png&center=" + coords.getLat() + "," + coords.getLon() + "&zoom=18&xis=https://cravatar.eu/helmavatar/" + player.getName() + "/64.png,1,c," + coords.getLat() + "," + coords.getLon()).openStream();
                                hasFile[0] = true;
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                return;
                            }
                            embedBuilder.setImage("attachment://map.png");

                        } else {

                            embedBuilder.setColor(ERROR_COLOR)
                                    .addField("Status:", ":red_circle: Offline", false);

                            DataTime time = DateHelper.differenceToData(
                                            new Date(offlinePlayer.getLastPlayed()),
                                            new Date()
                                    );

                            embedBuilder.addField("Ultima vez conectado: ",
                                    (time.get(DateHelper.Type.MINUTE) > 0 ? "Hace " + (time.get(DateHelper.Type.HOUR) > 0 ? time.get(DateHelper.Type.HOUR) + " hora(s) y " : "") + time.get(DateHelper.Type.MINUTE) + " minuto(s)" : "Recién"), false);

                        }

                        if (serverPlayer.getDiscordManager().isLinked()) {
                            DiscordManager dscManager = serverPlayer.getDiscordManager();
                            dscManager.loadUser();
                            embedBuilder.addField("Discord:", serverPlayer.getDiscordManager().getUser().getAsMention(), false);
                        } else {
                            embedBuilder.addField("Discord:", "No conectado.", false);
                        }

                        GroupsManager groups = serverPlayer.getGroupsManager();

                        embedBuilder.addField("Rango:",
                                new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "discord/groupEmojis").getString(groups.getPrimaryGroup().toString()) + " " + groups.getPrimaryGroup().toString().replace("default", "visita").toUpperCase(), false);

                        if (groups.getSecondaryGroups().size() > 0) {
                            List<String> ranks = new ArrayList<>();
                            for (GroupsManager.SecondaryGroup secondaryGroup : groups.getSecondaryGroups()) {
                                ranks.add(new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "discord/groupEmojis").getString(secondaryGroup.toString()) + " " + secondaryGroup.toString().toUpperCase());
                            }
                            embedBuilder.addField("Rangos secundarios:", String.join("\n", ranks), false);
                        }

                        PointsManager pointsManager = serverPlayer.getPointsManager();
                        if (pointsManager.getMaxPoints().getValue() > 0) {
                            List<String> points = new ArrayList<>();


                            for (Map.Entry<Country, Integer> entry : pointsManager.getSorted().entrySet()) {
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

                        ProjectsManager projects = serverPlayer.getProjectsManager();

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
                                    if (project.getOwner() != serverPlayer.getPlayer()) {
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

                        embedBuilder.setThumbnail("https://mc-heads.net/head/" + serverPlayer.getPlayer().getUniqueId());

                        if (hasFile[0]) {
                            textChannel.sendFile(file[0], "map.png").setEmbeds(embedBuilder.build()).reference(message).mentionRepliedUser(false).queue();
                        } else {
                            textChannel.sendMessageEmbeds(embedBuilder.build()).reference(message).mentionRepliedUser(false).queue();
                        }

                    } else {
                        errorInsertAPlayer(textChannel, message);
                    }

                    return;
                }
                errorInsertAPlayer(textChannel, message);
            }
        });
    }

    public void errorInsertAPlayer(TextChannel textChannel, Message message) {
        textChannel.sendMessageEmbeds(
                        new EmbedBuilder()
                                .setColor(ERROR_COLOR)
                                .setAuthor("Introduce un jugador, menciona a un usuario o introduce su ID.")
                                .build()
                ).reference(message.getReferencedMessage())
                .mentionRepliedUser(false).queue();
    }

}
