package pizzaaxx.bteconosur.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCanBuildEvent;
import pizzaaxx.bteconosur.projects.Project;

public class ProjectBlockPlacingListener implements Listener {

    @EventHandler
    public void onBlockCanBuild(BlockCanBuildEvent event) {
        if (!event.isBuildable()) {
            if (Project.isProjectAt(event.getBlock().getRelative()))
        }
    }

}
