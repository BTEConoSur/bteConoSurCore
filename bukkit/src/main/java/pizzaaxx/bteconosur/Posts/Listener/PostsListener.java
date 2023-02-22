package pizzaaxx.bteconosur.Posts.Listener;

import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.Managers.DiscordManager;
import pizzaaxx.bteconosur.Posts.ProjectWrapper;
import pizzaaxx.bteconosur.Utils.DiscordUtils;

import java.awt.*;
import java.util.UUID;

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

        if (parentChannel.getType() != ChannelType.FORUM) {
            return;
        }

        if (!plugin.getCountryManager().projectForumChannels.contains(parentChannel.getId())) {
            return;
        }

        if (plugin.getPostsRegistry().idsFromChannelID.containsKey(event.getChannel().getId())) {

            if (!plugin.getLinksRegistry().isLinked(event.getAuthor().getId())) {
                event.getMessage().delete().queue();
                event.getAuthor().openPrivateChannel().queue(
                        privateChannel -> {
                            privateChannel.sendMessageEmbeds(
                                    DiscordUtils.fastEmbed(
                                            Color.RED,
                                            "¿Eres miembro de este proyecto? Conecta tu cuenta para poder publicar en la publicación del proyecto.",
                                            "Usa `/link` en Discord para conectar tu cuenta."
                                    )
                            ).queue();
                        }
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
                        privateChannel -> {
                            privateChannel.sendMessageEmbeds(
                                    DiscordUtils.fastEmbed(
                                            Color.RED,
                                            "Debes ser un miembro de este proyecto para poder publicar aquí."
                                    )
                            ).queue();
                        }
                );
            }

        } else {

            event.getMessage().delete().queue();
            event.getAuthor().openPrivateChannel().queue(
                    privateChannel -> {
                        privateChannel.sendMessageEmbeds(
                                DiscordUtils.fastEmbed(
                                        Color.RED,
                                        "Esta publicación está cerrada."
                                        )
                        ).queue();
                    }
            );

        }
    }
}
