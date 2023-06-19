package pizzaaxx.bteconosur.Posts.Commands;

import com.sk89q.worldguard.util.net.HttpRequest;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Discord.SlashCommands.SlashCommandContainer;
import pizzaaxx.bteconosur.Projects.ProjectWrapper;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Utils.DiscordUtils;
import pizzaaxx.bteconosur.Utils.SatMapHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ProjectPostCommand extends ListenerAdapter implements SlashCommandContainer {

    private final BTEConoSur plugin;
    private final Set<String> ALLOWED_IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList("png", "jpg", "jpeg", "gif"));

    public ProjectPostCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public CommandData[] getCommandData() {
        return new CommandData[] {Commands.slash(
                "post",
                "Maneja la publicación tu proyecto en Discord."
        ).addSubcommands(
                new SubcommandData(
                        "edit",
                        "Edita la publicación de un proyecto. Debe usarse en el canal de la publicación."
                ),
                new SubcommandData(
                        "image",
                        "Agrega una imagen de portada a la publicación. Debe usarse en el canal de la publicación."
                )
                        .addOption(
                                OptionType.ATTACHMENT,
                                "imagen",
                                "La imagen a agregar a la portada"
                        )
        )};
    }

    @Override
    public JDA getJDA() {
        return plugin.getBot();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

        if (event.getName().equals("post")) {

            try {
                ResultSet set = plugin.getSqlManager().select(
                        "posts",
                        new SQLColumnSet(
                                "*"
                        ),
                        new SQLANDConditionSet(
                                new SQLOperatorCondition(
                                        "channel_id", "=", event.getChannel().getId()
                                )
                        )
                ).retrieve();

                if (!set.next()) {
                    DiscordUtils.respondError(event, "Debes usar este comando dentro de una publicación.");
                    return;
                }

                if (set.getString("target_type").equals("event")) {
                    DiscordUtils.respondError(event, "No puedes editar publicaciones de eventos.");
                    return;
                }

                if (!plugin.getLinksRegistry().isLinked(event.getUser().getId())) {
                    DiscordUtils.respondError(event, "Conecta tu cuenta de Discord para usar este comando.");
                    return;
                }

                ProjectWrapper project;
                if (set.getString("target_type").equals("project")) {
                    project = plugin.getProjectRegistry().get(set.getString("target_id"));
                } else {
                    project = plugin.getFinishedProjectsRegistry().get(set.getString("target_id"));
                }

                if (project == null) {
                    DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
                    return;
                }

                UUID userUUID = plugin.getLinksRegistry().get(event.getUser().getId());

                if (!project.getOwner().equals(userUUID)) {
                    DiscordUtils.respondError(event, "Solo el líder del proyecto puede editar la publicación.");
                    return;
                }

                String subcommand = event.getSubcommandName();
                assert subcommand != null;
                switch (subcommand) {
                    case "edit": {

                        Modal modal = Modal.create("editPostModal", "Editar publicación")
                                .addActionRows(
                                        ActionRow.of(
                                                TextInput.create(
                                                        "name", "Nombre", TextInputStyle.SHORT
                                                )
                                                        .setRequired(true)
                                                        .setMaxLength(32)
                                                        .setPlaceholder("Nombre de la publicación")
                                                        .setValue(set.getString("name"))
                                                        .build()
                                        ),
                                        ActionRow.of(
                                                TextInput.create(
                                                                "description", "Descripción", TextInputStyle.PARAGRAPH
                                                        )
                                                        .setRequired(true)
                                                        .setMaxLength(2000)
                                                        .setPlaceholder("Descripción de la publicación")
                                                        .setValue(set.getString("description"))
                                                        .build()
                                        )
                                ).build();

                        event.replyModal(modal).queue();

                        break;
                    }
                    case "image": {

                        OptionMapping mapping = event.getOption("imagen");

                        InputStream is;
                        String extension;
                        if (mapping != null) {

                            Message.Attachment attachment = mapping.getAsAttachment();
                            if (!ALLOWED_IMAGE_EXTENSIONS.contains(attachment.getFileExtension())) {
                                DiscordUtils.respondError(event, "Debes subir una imagen válida.");
                                return;
                            }

                            extension = attachment.getFileExtension();

                            if (attachment.getSize() > 2.5e+7) {
                                DiscordUtils.respondError(event, "Solo se pueden subir imágenes de menos de 25mb de tamaño.");
                                return;
                            }

                            is = HttpRequest.get(new URL(attachment.getUrl())).execute().getInputStream();

                        } else {

                            is = plugin.getSatMapHandler().getMapStream(
                                    new SatMapHandler.SatMapPolygon(
                                            plugin,
                                            project.getRegionPoints()
                                    )
                            );
                            extension = "png";
                        }

                        event.getChannel().retrieveMessageById(set.getString("message_id")).queue(
                                message -> message.editMessageAttachments(
                                        FileUpload.fromData(is,  "image." + extension)
                                ).queue()
                        );

                        DiscordUtils.respondSuccessEphemeral(event, "Imagen modificada con éxito.");

                        break;
                    }
                }

            } catch (SQLException e) {
                DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
            } catch (IOException e) {
                DiscordUtils.respondError(event, "Ha ocurrido un error con la imagen.");
            }
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {

        if (event.getModalId().equals("editPostModal")) {

            try {
                ResultSet set = plugin.getSqlManager().select(
                        "posts",
                        new SQLColumnSet(
                                "*"
                        ),
                        new SQLANDConditionSet(
                                new SQLOperatorCondition(
                                        "channel_id", "=", event.getChannel().getId()
                                )
                        )
                ).retrieve();

                ThreadChannel channel = event.getChannel().asThreadChannel();

                if (!set.next()) {
                    DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
                    return;
                }

                if (!plugin.getLinksRegistry().isLinked(event.getUser().getId())) {
                    DiscordUtils.respondError(event, "Conecta tu cuenta de Discord para usar esto.");
                    return;
                }

                ProjectWrapper project;
                if (set.getString("target_type").equals("project")) {
                    project = plugin.getProjectRegistry().get(set.getString("target_id"));
                } else {
                    project = plugin.getFinishedProjectsRegistry().get(set.getString("target_id"));
                }

                if (project == null) {
                    DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
                    return;
                }

                UUID userUUID = plugin.getLinksRegistry().get(event.getUser().getId());

                if (!project.getOwner().equals(userUUID)) {
                    DiscordUtils.respondError(event, "Solo el líder del proyecto puede editar la publicación.");
                    return;
                }

                ModalMapping nameMapping = event.getValue("name");
                assert nameMapping != null;
                String name = nameMapping.getAsString();

                ModalMapping descriptionMapping = event.getValue("description");
                assert descriptionMapping != null;
                String description = descriptionMapping.getAsString();

                if (!name.equals(set.getString("name"))) {

                    List<String> cities = new ArrayList<>();
                    for (String cityName : project.getCities()) {
                        cities.add(plugin.getCityManager().getDisplayName(cityName));
                    }

                    channel.getManager().setName(
                            name + (cities.isEmpty() ? "" : " - " + String.join(", ", cities))
                    ).queue();
                }

                if (!description.equals(set.getString("description"))) {
                    channel.retrieveMessageById(set.getString("message_id")).queue(
                            message -> message.editMessage(":speech_balloon: **Descripción:** " + description).queue()
                    );
                }

                plugin.getSqlManager().update(
                        "posts",
                        new SQLValuesSet(
                                new SQLValue(
                                        "name", name
                                ),
                                new SQLValue(
                                        "description", description
                                )
                        ),
                        new SQLANDConditionSet(
                                new SQLOperatorCondition(
                                        "channel_id", "=", event.getChannel().getId()
                                )
                        )
                ).execute();

                DiscordUtils.respondSuccessEphemeral(event, "Publicación editada con éxito.");

            } catch (SQLException e) {
                e.printStackTrace();
                DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
            }

        }

    }
}
