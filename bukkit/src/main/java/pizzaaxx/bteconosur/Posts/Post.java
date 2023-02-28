package pizzaaxx.bteconosur.Posts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sk89q.worldguard.util.net.HttpRequest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.ForumPostAction;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.AttachmentUpdate;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.Projects.ProjectTag;
import pizzaaxx.bteconosur.Projects.ProjectType;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Utils.SatMapHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Post {

    private final BTEConoSur plugin;
    private String projectID;
    private final String channelID;
    private final Country country;
    private final Set<String> cities;
    private String name;
    private String description;
    private final String messageID;

    public static void createPost(
            BTEConoSur plugin,
            @NotNull ProjectWrapper project,
            String name,
            String description
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

        String finalName = name + (cityNames.isEmpty() ? "" : " - " + String.join(", ", cityNames));

        File file = new File(plugin.getDataFolder(), "projects/images/" + project.getId() + ".png");

        ForumPostAction action = channel.createForumPost(
                        finalName.substring(0, Math.min(finalName.length(), 99)),
                        MessageCreateData.fromContent(":speech_balloon: **Descripción:** " + description)
                )
                .setEmbeds(
                        new EmbedBuilder()
                                .setColor(project.getType().getColor())
                                .addField(
                                        ":crown: Líder:",
                                        plugin.getPlayerRegistry().get(project.getOwner()).getName(),
                                        true
                                )
                                .addField(
                                        ":busts_in_silhouette: Miembros:",
                                        (members.isEmpty() ? "Sin miembros." : String.join(", ", members)),
                                        true
                                )
                                .addField(
                                        ":game_die: Tipo:",
                                        project.getType().getDisplayName() + " (" + project.getPoints() + " puntos)",
                                        true
                                )
                                .build()
                )
                .setFiles(
                        FileUpload.fromData(file, "projectMap.png")
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
                                                "message_id", post.getMessage().getId()
                                        )
                                )
                        ).execute();
                        project.updatePost();
                        plugin.getPostsRegistry().idsFromChannelID.put(post.getThreadChannel().getId(), project.getId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        post.getThreadChannel().delete().queue();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        if (project instanceof Project) {
                            plugin.getProjectRegistry().unload(project.getId());
                        } else {
                            plugin.getFinishedProjectsRegistry().unload(project.getId());
                        }
                    }
                }
        );
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
            this.messageID = set.getString("message_id");

        } else {
            throw new IllegalArgumentException();
        }

    }

    public String getProjectID() {
        return projectID;
    }

    public ProjectWrapper getProject() {
        if (projectID.length() == 6) {
            return plugin.getProjectRegistry().get(projectID);
        } else {
            return plugin.getFinishedProjectsRegistry().get(projectID);
        }
    }

    public ThreadChannel getChannel() {
        return country.getGuild().getThreadChannelById(channelID);
    }

    public RestAction<Message> getMessage() {
        return this.getChannel().retrieveMessageById(messageID);
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

    public void addImage(Message.Attachment attachment) {
        this.getMessage().queue(
                message -> {
                    try {
                        URL url = new URL(attachment.getUrl());
                        message.editMessageAttachments(
                                FileUpload.fromData(HttpRequest.get(url).execute().getInputStream(), attachment.getFileName())
                        ).queue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
        );
    }

    public void removeImage() {
        this.getMessage().queue(
                message -> {
                    File file = new File(plugin.getDataFolder(), "projects/images/" + this.getProjectID() + ".png");
                    message.editMessageAttachments(
                            FileUpload.fromData(
                                    file, "projectMap.png"
                            )
                    ).queue();
                }
        );
    }

    public void setName(String name) throws SQLException {
        if (!this.name.equals(name)) {
            plugin.getSqlManager().update(
                    "posts",
                    new SQLValuesSet(
                            new SQLValue(
                                    "name", name
                            )
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition("project_id", "=", projectID)
                    )
            ).execute();
            this.name = name;

            List<String> cityNames = new ArrayList<>();
            for (String city : this.getProject().getCities()) {
                cityNames.add(plugin.getCityManager().getDisplayName(city));
            }

            this.getChannel().getManager().setName(
                    this.name + (cityNames.isEmpty() ? "" : " - " + String.join(", ", cityNames))
            ).queue();
        }
    }

    public void setDescription(String description) throws SQLException {
        if (!this.description.equals(description)) {

            plugin.getSqlManager().update(
                    "posts",
                    new SQLValuesSet(
                            new SQLValue(
                                    "description", description
                            )
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition("project_id", "=", projectID)
                    )
            ).execute();
            this.description = description;

            this.getMessage().queue(
                    message -> {
                        message.editMessage(":speech_balloon: **Descripción:** " + description).queue();
                    }
            );

        }
    }

    public void updateOwner() {
        this.getMessage().queue(
                message -> {
                    ProjectWrapper project = this.getProject();

                    MessageEmbed embed = message.getEmbeds().get(0);
                    EmbedBuilder builder = new EmbedBuilder(embed);
                    builder.getFields().add(0, new MessageEmbed.Field(
                            ":crown: Líder:",
                            plugin.getPlayerRegistry().get(project.getOwner()).getName(),
                            true
                    ));

                    message.editMessageEmbeds(builder.build()).queue();
                }
        );
    }

    public void updateMembers() {
        this.getMessage().queue(
                message -> {
                    ProjectWrapper project = this.getProject();

                    MessageEmbed embed = message.getEmbeds().get(0);
                    EmbedBuilder builder = new EmbedBuilder(embed);

                    List<String> members = new ArrayList<>();
                    for (UUID memberUUID : project.getMembers()) {
                        members.add(plugin.getPlayerRegistry().get(memberUUID).getName().replace("_", "\\_"));
                    }

                    builder.getFields().add(1, new MessageEmbed.Field(
                            ":busts_in_silhouette: Miembros:",
                            (members.isEmpty() ? "Sin miembros." : String.join(", ", members)),
                            true
                    ));

                    message.editMessageEmbeds(builder.build()).queue();
                }
        );
    }

    public void updateType() {
        this.getMessage().queue(
                message -> {
                    ProjectWrapper project = this.getProject();
                    ProjectType type = project.getType();

                    MessageEmbed embed = message.getEmbeds().get(0);
                    EmbedBuilder builder = new EmbedBuilder(embed);
                    builder.setColor(type.getColor());

                    builder.getFields().add(2, new MessageEmbed.Field(
                            ":game_die: Tipo:",
                            type.getDisplayName() + " (" + project.getPoints() + " puntos)",
                            true
                    ));

                    message.editMessageEmbeds(builder.build()).queue();
                }
        );
    }

    public enum StatusTag{
        ONGOING, FINISHED
    }

    public void updateTags(@Nullable ProjectTag tag) {

        ForumChannel forum = this.getChannel().getParentChannel().asForumChannel();
        List<ForumTag> tags = new ArrayList<>();

        if (projectID.length() == 6) {
            tags.add(
                    forum.getAvailableTagsByName("En construcción", true).get(0)
            );
        } else {
            tags.add(
                    forum.getAvailableTagsByName("Terminado", true).get(0)
            );
        }

        if (tag != null) {
            tags.add(
                    forum.getAvailableTagsByName(tag.toString(), true).get(0)
            );
        }

        this.getChannel().getManager().setAppliedTags(
                tags
        ).queue();
    }

    public void setProjectID(String projectID) throws SQLException {

        if (!this.projectID.equals(projectID)) {
            plugin.getSqlManager().update(
                    "posts",
                    new SQLValuesSet(
                            new SQLValue(
                                    "project_id", projectID
                            )
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "project_id", "=", this.projectID
                            )
                    )
            ).execute();
            this.projectID = projectID;
            plugin.getPostsRegistry().idsFromChannelID.put(this.getChannel().getId(), this.projectID);
        }

    }

    public void sendMessage(String message) {
        this.getChannel().sendMessage(message).queue();
    }

    public void close() throws SQLException, JsonProcessingException {
        plugin.getSqlManager().delete(
                "posts",
                new SQLANDConditionSet(
                        new SQLOperatorCondition(
                                "project_id", "=", projectID
                        )
                )
        ).execute();
        plugin.getPostsRegistry().idsFromChannelID.remove(this.getChannel().getId());
        this.getChannel().delete().queue();
        this.getProject().updatePost();
    }
}
