package pizzaaxx.bteconosur.Projects.Actions;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Geo.Coords2D;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Utils.SatMapHandler;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

public class ClaimProjectAction {

    private final BTEConoSur plugin;
    private final Project project;
    private final UUID owner;

    public ClaimProjectAction(BTEConoSur plugin, Project project, UUID owner) {
        this.plugin = plugin;
        this.project = project;
        this.owner = owner;
    }

    public void execute() throws SQLException, IOException {
        new SetOwnerProjectAction(plugin, project, owner).execute();

        plugin.getPlayerRegistry().get(owner).getProjectManager().addProject(project);

        plugin.getTerramapHandler().deletePolygon(project.getId());
        List<Coords2D> coords = new ArrayList<>();
        for (BlockVector2D vector2D : project.getRegion().getPoints()) {
            coords.add(new Coords2D(plugin, vector2D));
        }
        plugin.getTerramapHandler().drawPolygon(coords, new Color(255, 200, 0), project.getId());

        DefaultDomain domain = new DefaultDomain();
        domain.addPlayer(owner);
        ProtectedPolygonalRegion region = project.getRegion();
        region.setMembers(domain);
        plugin.getRegionManager().addRegion(region);

        List<String> cities = new ArrayList<>();
        for (City city : project.getCitiesResolved()) {
            plugin.getScoreboardHandler().update(city);
            cities.add(city.getName());
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
                "Proyecto" + project.getId().toUpperCase() + (cities.isEmpty() ? "" : " - " + String.join(", ", cities)),
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
                                                new SQLValue("name", "Proyecto" + project.getId().toUpperCase()),
                                                new SQLValue("description", "N/A"),
                                                new SQLValue("message_id", forumPost.getMessage().getId())
                                        )
                                ).execute();
                            } catch (SQLException e) {
                                forumPost.getThreadChannel().delete().queue();
                            }
                        }
                );

        project.getCountry().getLogsChannel().sendMessage(":inbox_tray: **" + plugin.getPlayerRegistry().get(owner).getName() + "** ha reclamado el proyecto `" + project.getId() + "`.").queue();
    }
}
