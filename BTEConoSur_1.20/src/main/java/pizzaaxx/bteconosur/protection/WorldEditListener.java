package pizzaaxx.bteconosur.protection;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

public class WorldEditListener {

    private final BTEConoSurPlugin plugin;

    public WorldEditListener(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onEditSessionEvent(@NotNull EditSessionEvent event) {

        if (event.getActor() == null) {
            return;
        }

        if (event.getStage() == EditSession.Stage.BEFORE_CHANGE) {
            event.setExtent(new PlayerExtent(plugin, event.getExtent(), event.getActor().getUniqueId()));
        }
    }

}
