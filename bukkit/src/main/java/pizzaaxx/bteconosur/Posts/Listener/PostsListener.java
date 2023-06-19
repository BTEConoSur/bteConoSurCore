package pizzaaxx.bteconosur.Posts.Listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.Utils.DiscordUtils;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

        try {
            ResultSet set = plugin.getSqlManager().select(
                    "posts",
                    new SQLColumnSet(
                            "members"
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "channel_id", "=", channel.getId()
                            )
                    )
            ).retrieve();

            if (set.next()) {

                if (!plugin.getLinksRegistry().isLinked(event.getAuthor().getId())) {
                    event.getMessage().delete().queue();
                    event.getAuthor().openPrivateChannel().queue(
                            privateChannel -> privateChannel.sendMessageEmbeds(
                                    DiscordUtils.fastEmbed(
                                            Color.RED,
                                            "¿Quieres hablar aquí? Conecta tu cuenta de Discord."
                                    )
                            ).queue()
                    );
                    return;
                }

                JsonNode node = plugin.getJSONMapper().readTree(set.getString("members"));
                Set<UUID> members = new HashSet<>();
                for (JsonNode n : node) {
                    members.add(UUID.fromString(n.asText()));
                }

                UUID uuid = plugin.getLinksRegistry().get(event.getAuthor().getId());

                if (!members.contains(uuid)) {
                    event.getMessage().delete().queue();
                    event.getAuthor().openPrivateChannel().queue(
                            privateChannel -> privateChannel.sendMessageEmbeds(
                                    DiscordUtils.fastEmbed(
                                            Color.RED,
                                            "Debes ser miembro del evento o proyecto de esta publicación para poder hablar aquí."
                                    )
                            ).queue()
                    );
                }

            }

        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            event.getMessage().delete().queue();
            event.getAuthor().openPrivateChannel().queue(
                    privateChannel -> privateChannel.sendMessageEmbeds(
                            DiscordUtils.fastEmbed(
                                    Color.RED,
                                    "Ha ocurrido un error en la base de datos. No puedes hablar aquí por el momento."
                            )
                    ).queue()
            );
        }
    }
}
