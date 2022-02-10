package pizzaaxx.bteconosur.projects;


import com.sk89q.worldedit.BlockVector2D;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.misc.misc;

import java.util.*;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.misc.misc.getLogsChannel;
import static pizzaaxx.bteconosur.projects.command.projectsPrefix;

public class requestResponse extends ListenerAdapter {
    public Set<String> requestsClicks = new HashSet<>();

    @Override
    public void onButtonClick(ButtonClickEvent e) {
        if (e.getMessage().getEmbeds().size() > 0) {
            MessageEmbed embed = e.getMessage().getEmbeds().get(0);
            if (embed.getTitle().contains("quiere crear un proyecto")) {
                if (requestsClicks.contains(e.getMessage().getId())) {
                    return;
                }

                requestsClicks.add(e.getMessage().getId());
                if (e.getComponent().getId().equals("rechazar")) {
                    String title = embed.getTitle();
                    OfflinePlayer target = Bukkit.getOfflinePlayer(title.replace(" quiere crear un proyecto.", ""));

                    new ServerPlayer(target).sendNotification(projectsPrefix + "Tu solicitud de proyecto ha sido rechazada.");

                    e.getMessage().delete().queue();
                } else {
                    String title = embed.getTitle();
                    OfflinePlayer target = Bukkit.getOfflinePlayer(title.replace(" quiere crear un proyecto.", ""));
                    ServerPlayer s = new ServerPlayer(target);

                    MessageEmbed.Field field = embed.getFields().get(0);
                    String value = field.getValue();
                    String[] coordsRaw = value.replace("\n> ", "~").replace("> ", "").split("~");

                    List<BlockVector2D> points = new ArrayList<>();
                    for (String coord : coordsRaw) {
                        points.add(new BlockVector2D(Double.parseDouble(coord.split(" ")[0]), Double.parseDouble(coord.split(" ")[2])));
                    }

                    Project project = new Project(misc.getCountryAtLocation(points.get(0)), e.getComponentId(), points);
                    project.setOwner(target);
                    project.upload();

                    new ServerPlayer(target).sendNotification(projectsPrefix + "Tu solicitud de proyecto ha sido aceptada con dificultad **§a" + e.getComponentId().toUpperCase() + "§f**.");

                    String dscMessage = ":clipboard: **" + s.getName() + "** ha creado el proyecto `" + project.getId() + "` con dificultad `" + e.getComponentId().toUpperCase() + "` en las coordenadas: \n";
                    for (BlockVector2D point : project.getPoints()) {
                        dscMessage = dscMessage + "> " + Math.floor(point.getX()) + " " + Math.floor(mainWorld.getHighestBlockAt(point.getBlockX(), point.getBlockZ()).getY()) + " " + Math.floor(point.getZ()) + "\n";
                    }
                    dscMessage = dscMessage.replace(".0", "");

                    getLogsChannel(project.getCountry()).sendMessage(dscMessage).queue();
                    getLogsChannel(project.getCountry()).sendMessage(":inbox_tray: **" + s.getName() + "** ha reclamado el proyecto `" + project.getId() + "`.").queue();


                    requestsClicks.remove(e.getMessage().getId());

                    e.getMessage().delete().queue();
                }
            }

            if (embed.getTitle().contains("quiere redefinir el proyecto")) {
                if (requestsClicks.contains(e.getMessage().getId())) {
                    return;
                }

                requestsClicks.add(e.getMessage().getId());
                if (e.getComponent().getId().equals("rechazar")) {
                    String title = embed.getTitle();
                    OfflinePlayer target = Bukkit.getOfflinePlayer(title.split(" quiere redefinir el proyecto")[0]);

                    try {
                        Project project = new Project(title.replace(".", "").split(" quiere redefinir el proyecto ")[1].toLowerCase());

                        new ServerPlayer(target).sendNotification(projectsPrefix + "Tu solicitud para redefinir el proyecto `§a" + project.getId() + "§f` ha sido rechazada.");
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    e.getMessage().delete().queue();
                } else {
                    String title = embed.getTitle();
                    OfflinePlayer target = Bukkit.getOfflinePlayer(title.split(" quiere redefinir el proyecto")[0]);
                    ServerPlayer s = new ServerPlayer(target);

                    MessageEmbed.Field field = embed.getFields().get(1);
                    String value = field.getValue();
                    String[] coordsRaw = value.replace("\n> ", "~").replace("> ", "").split("~");

                    List<BlockVector2D> points = new ArrayList<>();
                    for (String coord : coordsRaw) {
                        points.add(new BlockVector2D(Double.parseDouble(coord.split(" ")[0]), Double.parseDouble(coord.split(" ")[2])));
                    }

                    try {
                        Project project = new Project(title.replace(".", "").split(" quiere redefinir el proyecto ")[1].toLowerCase());
                        project.setPoints(points);
                        project.setDifficulty(e.getComponentId());
                        project.upload();

                        new ServerPlayer(target).sendNotification(projectsPrefix + "Tu solicitud para redefinir el proyecto **§a" + project.getName(true) + "§f** ha sido aceptada con dificultad **§a" + e.getComponentId().toUpperCase() + "§f**.");

                        String dscMessage = ":pencil: **" + s.getName() + "** ha redefinido el proyecto `" + project.getId() + "` con dificultad `" + e.getComponentId().toUpperCase() + "` en las coordenadas: \n";
                        for (BlockVector2D point : project.getPoints()) {
                            dscMessage = dscMessage + "> " + Math.floor(point.getX()) + " " + Math.floor(mainWorld.getHighestBlockAt(point.getBlockX(), point.getBlockZ()).getY()) + " " + Math.floor(point.getZ()) + "\n";
                        }
                        dscMessage = dscMessage.replace(".0", "");

                        getLogsChannel(project.getCountry()).sendMessage(dscMessage).queue();

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    requestsClicks.remove(e.getMessage().getId());

                    e.getMessage().delete().queue();
                }
            }
        }
    }
}
