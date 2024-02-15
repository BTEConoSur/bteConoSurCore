package pizzaaxx.bteconosur.protection;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.projects.ProjectsCommand;
import pizzaaxx.bteconosur.utils.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class WorldProtector {

    private final BTEConoSurPlugin plugin;
    private final UUID uuid;

    public WorldProtector(BTEConoSurPlugin plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
    }

    public void onInteract(@NotNull PlayerInteractEvent event) {



    }

}

