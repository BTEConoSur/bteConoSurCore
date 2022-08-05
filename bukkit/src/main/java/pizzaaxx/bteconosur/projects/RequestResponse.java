package pizzaaxx.bteconosur.projects;


import com.sk89q.worldedit.BlockVector2D;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import pizzaaxx.bteconosur.misc.Misc;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import java.awt.*;
import java.util.*;
import java.util.List;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.discord.HelpMethods.errorEmbed;
import static pizzaaxx.bteconosur.projects.ProjectsCommand.projectsPrefix;

public class RequestResponse extends ListenerAdapter {
    public Map<String, String> requestsClicks = new HashMap<>();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {
        if (e.getMessage().getEmbeds().size() > 0) {
            MessageEmbed embed = e.getMessage().getEmbeds().get(0);
            if (embed.getTitle().contains("quiere crear un proyecto")) {

                if (requestsClicks.containsKey(e.getMessage().getId()) && !requestsClicks.get(e.getMessage().getId()).equals(e.getUser().getId())) {
                    User user = e.getJDA().retrieveUserById(requestsClicks.get(e.getMessage().getId())).complete();
                    e.replyEmbeds(errorEmbed(user.getName() + " ya ha iniciado una respuesta a esta solicitud.")).queue();
                    return;
                }

                requestsClicks.put(e.getMessage().getId(), e.getUser().getId());
                if (e.getComponent().getId().equals("rechazar")) {

                    Modal.Builder modal = Modal.create("rejectCreation~" + e.getMessage().getId(), "Rechazar solicitud de creación");
                    ActionRow row = ActionRow.of(
                            TextInput.create("reason", "Razón", TextInputStyle.SHORT)
                                    .setRequired(true)
                                    .setPlaceholder("Introduce una razón")
                                    .build()
                    );
                    modal.addActionRows(row);

                    e.replyModal(modal.build()).queue();
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

                    OldProject project = new OldProject(Misc.getCountryAtLocation(points.get(0)), OldProject.Difficulty.valueOf(e.getComponentId().toUpperCase()), points);
                    project.setOwner(target);
                    project.save();

                    new ServerPlayer(target).sendNotification(projectsPrefix + "Tu solicitud de proyecto ha sido aceptada con dificultad **§a" + e.getComponentId().toUpperCase() + "§f**.");

                    StringBuilder dscMessage = new StringBuilder(":clipboard: **" + s.getName() + "** ha creado el proyecto `" + project.getId() + "` con dificultad `" + e.getComponentId().toUpperCase() + "` en las coordenadas: \n");
                    for (BlockVector2D point : project.getPoints()) {
                        dscMessage.append("> ").append(Math.floor(point.getX())).append(" ").append(Math.floor(mainWorld.getHighestBlockAt(point.getBlockX(), point.getBlockZ()).getY())).append(" ").append(Math.floor(point.getZ())).append("\n");
                    }
                    dscMessage = new StringBuilder(dscMessage.toString().replace(".0", ""));

                    TextChannel logs = project.getCountry().getLogs();
                    logs.sendMessage(dscMessage.toString()).queue();
                    logs.sendMessage(":inbox_tray: **" + s.getName() + "** ha reclamado el proyecto `" + project.getId() + "`.").queue();


                    requestsClicks.remove(e.getMessage().getId());

                    e.getMessage().delete().queue();
                }
            }

            if (embed.getTitle().contains("quiere redefinir el proyecto")) {

                if (requestsClicks.containsKey(e.getMessage().getId()) && !requestsClicks.get(e.getMessage().getId()).equals(e.getUser().getId())) {
                    User user = e.getJDA().retrieveUserById(requestsClicks.get(e.getMessage().getId())).complete();
                    e.replyEmbeds(errorEmbed(user.getName() + " ya ha iniciado una respuesta a esta solicitud.")).queue();
                    return;
                }

                requestsClicks.put(e.getMessage().getId(), e.getUser().getId());
                if (e.getComponent().getId().equals("rechazar")) {

                    Modal.Builder modal = Modal.create("rejectRedefine~" + e.getMessage().getId(), "Rechazar solicitud de redefinición");
                    ActionRow row = ActionRow.of(
                            TextInput.create("reason", "Razón", TextInputStyle.SHORT)
                                    .setRequired(true)
                                    .setPlaceholder("Introduce una razón")
                                    .build()
                    );
                    modal.addActionRows(row);

                    e.replyModal(modal.build()).queue();
                } else {
                    String title = embed.getTitle();
                    OfflinePlayer target = Bukkit.getOfflinePlayer(title.split(" quiere redefinir el proyecto")[0]);
                    ServerPlayer s = new ServerPlayer(target);

                    MessageEmbed.Field field = embed.getFields().get(1);
                    String value = field.getValue();
                    Bukkit.getConsoleSender().sendMessage(value);
                    String[] coordsRaw = value.replace("\n> ", "~").replace("> ", "").split("~");
                    Bukkit.getConsoleSender().sendMessage(String.join("\n", coordsRaw));

                    List<BlockVector2D> points = new ArrayList<>();
                    for (String coord : coordsRaw) {
                        points.add(new BlockVector2D(Double.parseDouble(coord.split(" ")[0]), Double.parseDouble(coord.split(" ")[2])));
                    }

                    try {
                        OldProject project = new OldProject(title.replace(".", "").split(" quiere redefinir el proyecto ")[1].toLowerCase());
                        project.setPoints(points);
                        project.setDifficulty(OldProject.Difficulty.valueOf(e.getComponentId().toUpperCase()));
                        project.save();

                        new ServerPlayer(target).sendNotification(projectsPrefix + "Tu solicitud para redefinir el proyecto **§a" + project.getName(true) + "§f** ha sido aceptada con dificultad **§a" + e.getComponentId().toUpperCase() + "§f**.");

                        StringBuilder dscMessage = new StringBuilder(":pencil: **" + s.getName() + "** ha redefinido el proyecto `" + project.getId() + "` con dificultad `" + e.getComponentId().toUpperCase() + "` en las coordenadas: \n");
                        for (BlockVector2D point : project.getPoints()) {
                            dscMessage.append("> ").append(Math.floor(point.getX())).append(" ").append(Math.floor(mainWorld.getHighestBlockAt(point.getBlockX(), point.getBlockZ()).getY())).append(" ").append(Math.floor(point.getZ())).append("\n");
                        }
                        dscMessage = new StringBuilder(dscMessage.toString().replace(".0", ""));

                        project.getCountry().getLogs().sendMessage(dscMessage.toString()).queue();

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    requestsClicks.remove(e.getMessage().getId());

                    e.getMessage().delete().queue();
                }
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {

        if (event.getModalId().startsWith("rejectCreation")) {

            String embedID = event.getModalId().replace("rejectCreation~", "");

            Message message = event.getTextChannel().retrieveMessageById(embedID).complete();

            MessageEmbed embed = message.getEmbeds().get(0);

            String title = embed.getTitle();
            OfflinePlayer target = Bukkit.getOfflinePlayer(title.replace(" quiere crear un proyecto.", ""));

            new ServerPlayer(target).sendNotification(projectsPrefix + "Tu solicitud de proyecto ha sido rechazada. Razón: " + event.getValue("reason").getAsString());

            message.delete().queue();

            event.replyEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("Solicitud rechazada con éxito.")
                            .build()
            ).setEphemeral(true).queue();

            requestsClicks.remove(embedID);

        }

        if (event.getModalId().startsWith("rejectRedefine")) {

            String embedID = event.getModalId().replace("rejectRedefine~", "");

            Message message = event.getTextChannel().retrieveMessageById(embedID).complete();

            MessageEmbed embed = message.getEmbeds().get(0);

            String title = embed.getTitle();
            OfflinePlayer target = Bukkit.getOfflinePlayer(title.split(" quiere redefinir el proyecto")[0]);

            try {
                OldProject project = new OldProject(title.replace(".", "").split(" quiere redefinir el proyecto ")[1].toLowerCase());

                new ServerPlayer(target).sendNotification(projectsPrefix + "Tu solicitud para redefinir el proyecto `§a" + project.getId() + "§f` ha sido rechazada.");
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            message.delete().queue();

            event.replyEmbeds(
                    new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("Solicitud rechazada con éxito.")
                            .build()
            ).setEphemeral(true).queue();

            requestsClicks.remove(embedID);

        }

    }
}
