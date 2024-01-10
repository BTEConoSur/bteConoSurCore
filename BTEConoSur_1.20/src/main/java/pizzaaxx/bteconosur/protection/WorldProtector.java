package pizzaaxx.bteconosur.protection;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.projects.ProjectsCommand;

import java.util.UUID;

public class WorldProtector {

    private final BTEConoSurPlugin plugin;
    private final UUID uuid;

    public WorldProtector(BTEConoSurPlugin plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
    }

    public void onInteract(@NotNull PlayerInteractEvent event) {
        // check if is managing a project
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && ProjectsCommand.MANAGER_INTERFACES.containsKey(uuid)) {

            int slot = event.getPlayer().getInventory().getHeldItemSlot();
            plugin.log(slot);
            ProjectsCommand.MANAGER_INTERFACES.get(event.getPlayer().getUniqueId()).onManageClick(slot);

        } else {



        }
    }

}

