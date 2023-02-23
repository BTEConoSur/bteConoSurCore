package pizzaaxx.bteconosur.Posts.Listener;

import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Posts.Post;
import pizzaaxx.bteconosur.Posts.ProjectWrapper;
import pizzaaxx.bteconosur.Projects.Finished.FinishedProject;
import pizzaaxx.bteconosur.Utils.DiscordUtils;
import pizzaaxx.bteconosur.Utils.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PostsListener extends ListenerAdapter {

    private final BTEConoSur plugin;

    public PostsListener(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (event.getChannelType() !=  ChannelType.GUILD_PUBLIC_THREAD) {
            return;
        }

        ThreadChannel channel = (ThreadChannel) event.getChannel();
        Channel parentChannel = channel.getParentChannel();

        if (!plugin.getCountryManager().projectForumChannels.contains(parentChannel.getId())) {
            return;
        }

        if (plugin.getPostsRegistry().idsFromChannelID.containsKey(event.getChannel().getId())) {

            if (!plugin.getLinksRegistry().isLinked(event.getAuthor().getId())) {
                event.getMessage().delete().queue();
                event.getAuthor().openPrivateChannel().queue(
                        privateChannel -> privateChannel.sendMessageEmbeds(
                                DiscordUtils.fastEmbed(
                                        Color.RED,
                                        "¿Eres miembro de este proyecto? Conecta tu cuenta para poder publicar en la publicación del proyecto.",
                                        "Usa `/link` en Discord para conectar tu cuenta."
                                )
                        ).queue()
                );
            }

            UUID uuid = plugin.getLinksRegistry().get(event.getAuthor().getId());

            String id = plugin.getPostsRegistry().idsFromChannelID.get(event.getChannel().getId());

            ProjectWrapper project;
            if (id.length() == 6) {
                project = plugin.getProjectRegistry().get(id);
            } else {
                project = plugin.getFinishedProjectsRegistry().get(id);
            }

            if (!project.getOwner().equals(uuid) && !project.getMembers().contains(uuid)) {
                event.getMessage().delete().queue();
                event.getAuthor().openPrivateChannel().queue(
                        privateChannel -> privateChannel.sendMessageEmbeds(
                                DiscordUtils.fastEmbed(
                                        Color.RED,
                                        "Debes ser un miembro de este proyecto para poder publicar aquí."
                                )
                        ).queue()
                );
            }

        } else {

            event.getMessage().delete().queue();
            event.getAuthor().openPrivateChannel().queue(
                    privateChannel -> privateChannel.sendMessageEmbeds(
                            DiscordUtils.fastEmbed(
                                    Color.RED,
                                    "Esta publicación está cerrada."
                                    )
                    ).queue()
            );

        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getModalId().startsWith("editPostForm") || event.getModalId().startsWith("createPostForm")) {

            Map<String, String> query = StringUtils.getQuery(event.getModalId().split("\\?")[1]);
            String id = query.get("id");

            ModalMapping nameMapping = event.getValue("name");
            assert nameMapping != null;
            String name = nameMapping.getAsString();

            ModalMapping descriptionMapping = event.getValue("description");
            assert descriptionMapping != null;
            String description = descriptionMapping.getAsString();

            ProjectWrapper project;
            if (id.length() == 6) {
                project = plugin.getProjectRegistry().get(id);
            } else {
                project = plugin.getFinishedProjectsRegistry().get(id);
            }

            if (event.getModalId().equals("createPostForm")) {

                try {
                    Post.createPost(
                            plugin,
                            project,
                            name,
                            description
                    );

                    event.replyEmbeds(
                            DiscordUtils.fastEmbed(
                                    Color.GREEN,
                                    "Proyecto " + project.getDisplayName() + " publicado.",
                                    "Ve la publicación en <#" + project.getCountry().getProjectsForumChannelID() + ">."
                            )
                    ).queue(
                            msg -> msg.deleteOriginal().queueAfter(1, TimeUnit.MINUTES)
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                    DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
                }

            } else {

                try {
                    Post post = project.getPost();
                    post.setName(name);
                    post.setDescription(description);

                    DiscordUtils.respondSuccessEphemeral(event, "Publicación editada con éxito.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    DiscordUtils.respondError(event, "Ha ocurrido un error en la base de datos.");
                }

            }

        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        if (event.getButton().getId() == null) {
            return;
        }

        if (event.getButton().getId().startsWith("projectForm")) {

            Map<String, String> query = StringUtils.getQuery(event.getButton().getId().split("\\?")[1]);
            String id = query.get("id");

            FinishedProject project = plugin.getFinishedProjectsRegistry().get(id);

            Modal modal = Modal.create(
                    "createPostForm?id=" + id,
                    "Publicar proyecto " + project.getName()
            )
                    .addActionRows(
                            ActionRow.of(
                                    TextInput.create(
                                            "name",
                                            "Nombre",
                                            TextInputStyle.SHORT
                                    ).setPlaceholder("Un nombre representativo del proyecto (EJ: Palacio de la Moneda)").setRequired(true).build()
                            ),
                            ActionRow.of(
                                    TextInput.create(
                                            "description",
                                            "Descripción",
                                            TextInputStyle.PARAGRAPH
                                    ).setMaxLength(1000).setRequired(true).build()
                            )
                    )
                    .build();

            event.replyModal(modal).queue();

        }
    }
}
