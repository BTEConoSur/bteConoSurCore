package pizzaaxx.bteconosur.test;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

public class WorldEditHookTest {

    private final BTEConoSurPlugin plugin;

    public WorldEditHookTest(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onEditSessionEvent(EditSessionEvent event) {
        if (event.getStage() == EditSession.Stage.BEFORE_CHANGE) {

        }
    }
}
