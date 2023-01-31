package pizzaaxx.bteconosur.Projects.Listeners;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.Regions.RegionEnterEvent;
import pizzaaxx.bteconosur.Regions.RegionListener;

public class ActionBarListener extends RegionListener {

    private final BTEConoSur plugin;

    public ActionBarListener(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onRegionEnter(@NotNull RegionEnterEvent event) {
        String projectID = event.getRegionID().replace("project_", "");
        if (plugin.getProjectRegistry().exists(projectID)) {
            Project project = plugin.getProjectRegistry().get(projectID);
            if (project.isClaimed()) {
                event.getPlayer().sendActionBar("§eProyecto " + project.getDisplayName() + " §8- §7" + plugin.getPlayerRegistry().get(project.getOwner()).getName());
            } else {
                event.getPlayer().sendActionBar("§aProyecto " + project.getDisplayName() + " §8- §7Disponible");
            }
        }
    }

}
