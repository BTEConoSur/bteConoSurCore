package pizzaaxx.bteconosur.LegacyConversion;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Projects.Finished.FinishedProject;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Utils.SatMapHandler;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ArgentinaFix implements CommandExecutor {

    private final BTEConoSur plugin;

    public ArgentinaFix(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof ConsoleCommandSender)) {
            return true;
        }

        try {
            ResultSet set = plugin.getSqlManager().select(
                    "projects",
                    new SQLColumnSet(
                            "*"
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "country", "=", "argentina"
                            )
                    )
            ).retrieve();

            while (set.next()) {
                Project project = plugin.getProjectRegistry().get(set.getString("id"));
                ResultSet postsSet = plugin.getSqlManager().select(
                        "posts",
                        new SQLColumnSet("*"),
                        new SQLANDConditionSet(
                                new SQLOperatorCondition(
                                        "target_id", "=", set.getString("id")
                                )
                        )
                ).retrieve();
                if (!postsSet.next() && project.isClaimed()) {
                    List<String> cities = new ArrayList<>();
                    for (City city : project.getCitiesResolved()) {
                        cities.add(city.getDisplayName());
                    }

                    List<String> members = new ArrayList<>();
                    for (UUID memberUUID : project.getMembers()) {
                        members.add(plugin.getPlayerRegistry().get(memberUUID).getName().replace("_", "\\_"));
                    }

                    ForumChannel channel = project.getCountry().getProjectsForumChannel();
                    Set<ForumTag> tags = new HashSet<>();
                    tags.add(channel.getAvailableTagsByName("En construcción", true).get(0));
                    if (project.getTag() != null) {
                        tags.add(channel.getAvailableTagsByName(project.getTag().toString(), true).get(0));
                    }
                    tags.add(channel.getAvailableTagsByName(project.getType().getDisplayName(), true).get(0));

                    project.getCountry().getProjectsForumChannel().createForumPost(
                                    "Proyecto " + project.getId().toUpperCase() + (cities.isEmpty() ? "" : " - " + String.join(", ", cities)),
                                    MessageCreateData.fromContent(":speech_balloon: **Descripción:** N/A")
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
                                    FileUpload.fromData(
                                            plugin.getSatMapHandler().getMapStream(
                                                    new SatMapHandler.SatMapPolygon(
                                                            plugin,
                                                            project.getRegionPoints()
                                                    )
                                            ),
                                            "projectMap.png"
                                    )
                            ).setTags(
                                    tags
                            ).queue(
                                    forumPost -> {
                                        try {
                                            plugin.getSqlManager().insert(
                                                    "posts",
                                                    new SQLValuesSet(
                                                            new SQLValue("target_type", "project"),
                                                            new SQLValue("target_id", project.getId()),
                                                            new SQLValue("channel_id", forumPost.getThreadChannel().getId()),
                                                            new SQLValue("members", project.getAllMembers()),
                                                            new SQLValue("country", project.getCountry()),
                                                            new SQLValue("cities", project.getCities()),
                                                            new SQLValue("name", "Proyecto " + project.getId().toUpperCase()),
                                                            new SQLValue("description", "N/A"),
                                                            new SQLValue("message_id", forumPost.getMessage().getId())
                                                    )
                                            ).execute();
                                        } catch (SQLException e) {
                                            forumPost.getThreadChannel().delete().queue();
                                        }
                                    }
                            );
                }
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        try {
            ResultSet set = plugin.getSqlManager().select(
                    "finished_projects",
                    new SQLColumnSet(
                            "*"
                    ),
                    new SQLANDConditionSet(
                            new SQLOperatorCondition(
                                    "country", "=", "argentina"
                            )
                    )
            ).retrieve();

            while (set.next()) {
                FinishedProject project = plugin.getFinishedProjectsRegistry().get(set.getString("id"));
                ResultSet postsSet = plugin.getSqlManager().select(
                        "posts",
                        new SQLColumnSet("*"),
                        new SQLANDConditionSet(
                                new SQLOperatorCondition(
                                        "target_id", "=", set.getString("id")
                                )
                        )
                ).retrieve();
                if (!postsSet.next() && project.isClaimed()) {
                    List<String> cities = new ArrayList<>();
                    for (String name : project.getCities()) {
                        cities.add(plugin.getCityManager().get(name).getDisplayName());
                    }

                    List<String> members = new ArrayList<>();
                    for (UUID memberUUID : project.getMembers()) {
                        members.add(plugin.getPlayerRegistry().get(memberUUID).getName().replace("_", "\\_"));
                    }

                    ForumChannel channel = project.getCountry().getProjectsForumChannel();
                    Set<ForumTag> tags = new HashSet<>();
                    tags.add(channel.getAvailableTagsByName("Terminado", true).get(0));
                    if (project.getTag() != null) {
                        tags.add(channel.getAvailableTagsByName(project.getTag().toString(), true).get(0));
                    }
                    tags.add(channel.getAvailableTagsByName(project.getType().getDisplayName(), true).get(0));

                    project.getCountry().getProjectsForumChannel().createForumPost(
                                    "Proyecto " + project.getId().toUpperCase() + (cities.isEmpty() ? "" : " - " + String.join(", ", cities)),
                                    MessageCreateData.fromContent(":speech_balloon: **Descripción:** N/A")
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
                                    FileUpload.fromData(
                                            plugin.getSatMapHandler().getMapStream(
                                                    new SatMapHandler.SatMapPolygon(
                                                            plugin,
                                                            project.getRegionPoints()
                                                    )
                                            ),
                                            "projectMap.png"
                                    )
                            ).setTags(
                                    tags
                            ).queue(
                                    forumPost -> {
                                        try {
                                            plugin.getSqlManager().insert(
                                                    "posts",
                                                    new SQLValuesSet(
                                                            new SQLValue("target_type", "project"),
                                                            new SQLValue("target_id", project.getId()),
                                                            new SQLValue("channel_id", forumPost.getThreadChannel().getId()),
                                                            new SQLValue("members", project.getAllMembers()),
                                                            new SQLValue("country", project.getCountry()),
                                                            new SQLValue("cities", project.getCities()),
                                                            new SQLValue("name", "Proyecto " + project.getId().toUpperCase()),
                                                            new SQLValue("description", "N/A"),
                                                            new SQLValue("message_id", forumPost.getMessage().getId())
                                                    )
                                            ).execute();
                                        } catch (SQLException e) {
                                            forumPost.getThreadChannel().delete().queue();
                                        }
                                    }
                            );
                }
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
