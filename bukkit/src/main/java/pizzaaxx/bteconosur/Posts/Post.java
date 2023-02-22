package pizzaaxx.bteconosur.Posts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk89q.worldguard.util.net.HttpRequest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.ForumPostAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Post {

    private final BTEConoSur plugin;
    private final String projectID;
    private final String channelID;
    private final Country country;
    private final Set<String> cities;
    private final String name;
    private final String description;
    private final String imageURL;

    public static void createPost(
            BTEConoSur plugin,
            @NotNull ProjectWrapper project,
            String name,
            String description,
            URL imageURL
    ) throws IOException {

        List<String> cityNames = new ArrayList<>();
        for (String city : project.getCities()) {
            cityNames.add(plugin.getCityManager().getDisplayName(city));
        }

        ForumChannel channel = project.getCountry().getProjectsForumChannel();

        List<String> members = new ArrayList<>();
        for (UUID memberUUID : project.getMembers()) {
            members.add(plugin.getPlayerRegistry().get(memberUUID).getName().replace("_", "\\_"));
        }

        ForumPostAction action = channel.createForumPost(
                name + (cityNames.isEmpty() ? "" : " - " + String.join(", ", cityNames)),
                new MessageCreateBuilder()
                        .addContent(":speech_balloon: **Descripción:** " + description)
                        .addEmbeds(
                                new EmbedBuilder()
                                        .setColor(project.getType().getColor())
                                        .addField(
                                                ":crown: Líder:",
                                                plugin.getPlayerRegistry().get(project.getOwner()).getName(),
                                                true
                                        )
                                        .addField(
                                                (members.isEmpty() ?
                                                        null :
                                                        new MessageEmbed.Field(
                                                                ":busts_in_silhouette: Miembros:",
                                                                String.join(", ", members),
                                                                true
                                                        ))
                                        )
                                        .addField(
                                                ":game_die: Tipo:",
                                                project.getType().getDisplayName() + " (" + project.getPoints() + " puntos)",
                                                true
                                        )
                                        .addField(
                                                ":speech_balloon: Descripción:",
                                                description,
                                                false
                                        )
                                        .setImage(
                                                "attachment://image.png"
                                        )
                                        .build()
                        )
                        .addFiles(
                                FileUpload.fromData(HttpRequest.get(imageURL).execute().getInputStream(), "image.png")
                        )
                        .build()
        );

        List<ForumTag> appliedTags = new ArrayList<>();

        if (project.getTag() != null) {
            String tag = project.getTag().toString();
            List<ForumTag> tags = channel.getAvailableTagsByName(tag, true);
            appliedTags.add(tags.get(0));
        }

        String statusTag;

        if (project instanceof Project) {
            statusTag = "En construcción";
        } else {
            statusTag = "Terminado";
        }

        appliedTags.add(channel.getAvailableTagsByName(statusTag, true).get(0));

        action.setTags(appliedTags);

        action.queue(
                post -> {
                    post.getMessage().addReaction(Emoji.fromUnicode("U+1F48E")).queue();

                    try {
                        plugin.getSqlManager().insert(
                                "posts",
                                new SQLValuesSet(
                                        new SQLValue(
                                                "project_id", project.getId()
                                        ),
                                        new SQLValue(
                                                "channel_id", post.getThreadChannel().getId()
                                        ),
                                        new SQLValue(
                                                "country", project.getCountry().getName()
                                        ),
                                        new SQLValue(
                                                "cities", project.getCities()
                                        ),
                                        new SQLValue(
                                                "name", name
                                        ),
                                        new SQLValue(
                                                "description", description
                                        ),
                                        new SQLValue(
                                                "image_url", imageURL.toString()
                                        )
                                )
                        ).execute();
                        project.updatePost();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        post.getThreadChannel().delete().queue();
                    } catch (JsonProcessingException e) {
                        if (project instanceof Project) {
                            plugin.getProjectRegistry().unload(project.getId());
                        } else {
                            plugin.getFinishedProjectsRegistry().unload(project.getId());
                        }
                    }
                }
        );

    }

    public static void deletePost(String projectID) {



    }

    public Post(@NotNull BTEConoSur plugin, String projectID) throws SQLException, JsonProcessingException {

        this.plugin = plugin;
        this.projectID = projectID;

        ResultSet set = plugin.getSqlManager().select(
                "posts",
                new SQLColumnSet(
                        "*"
                ),
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "project_id", "=", projectID
                        )
                )
        ).retrieve();

        if (set.next()) {

            this.channelID = set.getString("channel_id");
            this.country = plugin.getCountryManager().get(set.getString("country"));
            this.cities = plugin.getJSONMapper().readValue(set.getString("cities"), HashSet.class);
            this.name = set.getString("name");
            this.description = set.getString("description");
            this.imageURL = set.getString("imageURL");

        } else {
            throw new IllegalArgumentException();
        }

    }

    public String getProjectID() {
        return projectID;
    }

    public ThreadChannel getChannel() {
        return country.getGuild().getThreadChannelById(channelID);
    }

    public RestAction<Message> getMessage() {
        return this.getChannel().retrieveParentMessage();
    }

    public Set<String> getCities() {
        return cities;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

}
