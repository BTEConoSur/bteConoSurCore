package pizzaaxx.bteconosur.Projects.Actions;

import com.sk89q.worldedit.BlockVector2D;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Geo.Coords2D;
import pizzaaxx.bteconosur.Projects.Project;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        project.getCountry().getLogsChannel().sendMessage(":inbox_tray: **" + plugin.getPlayerRegistry().get(owner).getName() + "** ha reclamado el proyecto `" + project.getId() + "`.").queue();
    }
}
