package pizzaaxx.bteconosur.worldedit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.*;
import com.sk89q.worldedit.world.World;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.worldedit.WorldEditHelper.getLocalSession;
import static pizzaaxx.bteconosur.worldedit.WorldEditHelper.transform;

public class SelectionCommands implements CommandExecutor, Listener {

    private final Plugin plugin;

    public SelectionCommands(Plugin plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, List<RegionSelector>> undoSteps = new HashMap<>();
    private final Map<UUID, List<RegionSelector>> redoSteps = new HashMap<>();
    private final Map<UUID, Long> lastQuit = new HashMap<>();

    private final List<String> COMMANDS = Arrays.asList(
            "//sel", "//desel", "//deselect", "//pos1", "//pos2", "//hpos1", "//hpos2", "//chunk", "//expand", "//contract", "//outset", "//inset", "//shift"
    );

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(@NotNull PlayerInteractEvent event) {

        if (event.getItem() != null && event.getItem().getType() == Material.WOOD_AXE && (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {

            RegionSelector region = getLocalSession(event.getPlayer()).getRegionSelector((World) new BukkitWorld(mainWorld));

            List<RegionSelector> regions = undoSteps.getOrDefault(event.getPlayer().getUniqueId(), new ArrayList<>());

            if (regions.isEmpty() || regions.get(regions.size() - 1) != region) {
                regions.add(cloneSelector(region));
                undoSteps.put(event.getPlayer().getUniqueId(), regions);
                if (redoSteps.containsKey(event.getPlayer().getUniqueId())) {
                    redoSteps.get(event.getPlayer().getUniqueId()).clear();
                }
            }

        }

    }

    @EventHandler
    public void onCommand(@NotNull PlayerCommandPreprocessEvent event) {

        if (COMMANDS.contains(event.getMessage().split(" ")[0])) {
            RegionSelector region = getLocalSession(event.getPlayer()).getRegionSelector((World) new BukkitWorld(mainWorld));

            List<RegionSelector> regions = undoSteps.getOrDefault(event.getPlayer().getUniqueId(), new ArrayList<>());

            if (regions.isEmpty() || regions.get(regions.size() - 1) != region) {
                regions.add(cloneSelector(region));
                undoSteps.put(event.getPlayer().getUniqueId(), regions);
                if (redoSteps.containsKey(event.getPlayer().getUniqueId())) {
                    redoSteps.get(event.getPlayer().getUniqueId()).clear();
                }
            }
        }

    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {

        UUID uuid = event.getPlayer().getUniqueId();

        lastQuit.put(uuid, System.currentTimeMillis());

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {

                if (System.currentTimeMillis() - lastQuit.get(uuid) >= 550000) {
                    lastQuit.remove(uuid);
                    undoSteps.remove(uuid);
                    redoSteps.remove(uuid);
                }

            }
        };

        runnable.runTaskLaterAsynchronously(plugin, 12000);

    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;
        World world = new BukkitWorld(mainWorld);

        if (command.getName().equals("/selundo")) {

            if (!undoSteps.containsKey(p.getUniqueId()) || undoSteps.get(p.getUniqueId()).isEmpty()) {

                p.sendMessage("§dNo hay nada que deshacer.");
                return true;

            }

            com.sk89q.worldedit.entity.Player actor = transform(p);

            List<RegionSelector> steps = undoSteps.get(p.getUniqueId());
            RegionSelector selector = steps.get(steps.size() - 1);

            LocalSession localSession = getLocalSession(p);

            List<RegionSelector> redoRegions = redoSteps.getOrDefault(p.getUniqueId(), new ArrayList<>());
            redoRegions.add(cloneSelector(localSession.getRegionSelector(world)));
            redoSteps.put(p.getUniqueId(), redoRegions);

            localSession.setRegionSelector((World) new BukkitWorld(mainWorld), selector);
            localSession.dispatchCUISelection(actor);

            steps.remove(steps.size() - 1);
            undoSteps.put(p.getUniqueId(), steps);

            p.sendMessage("§dÚltima selección deshecha.");
        }

        if (command.getName().equals("/selredo")) {

            if (!redoSteps.containsKey(p.getUniqueId()) || redoSteps.get(p.getUniqueId()).isEmpty()) {

                p.sendMessage("§dNo hay nada que rehacer.");
                return true;

            }

            com.sk89q.worldedit.entity.Player actor = transform(p);

            List<RegionSelector> steps = redoSteps.get(p.getUniqueId());
            RegionSelector selector = steps.get(steps.size() - 1);

            LocalSession localSession = getLocalSession(p);

            List<RegionSelector> undoRegions = undoSteps.getOrDefault(p.getUniqueId(), new ArrayList<>());
            undoRegions.add(cloneSelector(steps.get(steps.size() - 1)));
            undoSteps.put(p.getUniqueId(), undoRegions);

            localSession.setRegionSelector((World) new BukkitWorld(mainWorld), selector);
            localSession.dispatchCUISelection(actor);

            steps.remove(steps.size() - 1);
            redoSteps.put(p.getUniqueId(), steps);

            p.sendMessage("§dÚltima selección rehecha.");
        }

        return true;
    }

    public RegionSelector cloneSelector(RegionSelector selector) {

        if (selector instanceof CuboidRegion) {

            return new CuboidRegionSelector(selector);

        } else if (selector instanceof Polygonal2DRegionSelector) {

            return new Polygonal2DRegionSelector(selector);

        } else if (selector instanceof EllipsoidRegionSelector) {

            return new EllipsoidRegionSelector(selector);

        } else if (selector instanceof CylinderRegionSelector) {

            return new CylinderRegionSelector(selector);

        } else if (selector instanceof ExtendingCuboidRegionSelector) {

            return new ExtendingCuboidRegionSelector(selector);

        } else {

            return new ConvexPolyhedralRegionSelector(selector);

        }

    }

    public boolean compareRegionSelectors(RegionSelector r1, RegionSelector r2) {

        if (r1 == r2) {
            return true;
        }

        if (r1.getClass() != r2.getClass()) {
            return false;
        }

        // TODO THIS

    }
}
