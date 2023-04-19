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
import pizzaaxx.bteconosur.BuildEvents.BuildEvent;
import pizzaaxx.bteconosur.Posts.Post;
import pizzaaxx.bteconosur.Projects.ProjectWrapper;
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

        if (event.getAuthor().isBot()) {
            if (!event.getAuthor().getId().equals(plugin.getBot().getSelfUser().getId())) {
                event.getMessage().delete().queue();
            }
            return;
        }

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
                return;
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

        } else if (plugin.getBuildEventsRegistry().channelIDToEventID.containsKey(event.getChannel().getId())) {

            if (!plugin.getLinksRegistry().isLinked(event.getAuthor().getId())) {
                event.getMessage().delete().queue();
                event.getAuthor().openPrivateChannel().queue(
                        privateChannel -> privateChannel.sendMessageEmbeds(
                                DiscordUtils.fastEmbed(
                                        Color.RED,
                                        "Conecta tu cuenta para poder publicar aquí.",
                                        "Usa `/link` en Discord para conectar tu cuenta."
                                )
                        ).queue()
                );
                return;
            }

            UUID uuid = plugin.getLinksRegistry().get(event.getAuthor().getId());

            String eventID = plugin.getBuildEventsRegistry().channelIDToEventID.get(event.getChannel().getId());

            BuildEvent buildEvent = plugin.getBuildEventsRegistry().get(eventID);

            if (!buildEvent.getMembers().contains(uuid)) {
                event.getMessage().delete().queue();
                event.getAuthor().openPrivateChannel().queue(
                        privateChannel -> privateChannel.sendMessageEmbeds(
                                DiscordUtils.fastEmbed(
                                        Color.RED,
                                        "Debes ser un miembro de este evento para poder publicar aquí."
                                )
                        ).queue()
                );
            }

        } else {
            event.getMessage().delete().queue();
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
