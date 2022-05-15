package pizzaaxx.bteconosur.projects;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import java.util.List;
import java.util.UUID;

import static pizzaaxx.bteconosur.projects.ProjectsCommand.projectsPrefix;

public class ProjectManageInventoryListener implements Listener {

    private final Plugin plugin;

    public ProjectManageInventoryListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();

        String name = inventory.getName();

        // Proyecto KIPOMP - DSTemuco

        if (name.startsWith("Proyecto ")) {

            Player player = (Player) event.getWhoClicked();

            String id = name.substring(10, 16).toLowerCase();

            try {
                Project project = new Project(id);

                if (project.getOwner().getUniqueId() == player.getUniqueId()) {

                    ItemStack item = inventory.getItem(event.getSlot());
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore = meta.getLore();
                    String action = "";
                    for (String line : lore) {
                        if (line.startsWith("§0action: ")) {
                            action = line.replace("§0action: ", "");
                        }
                    }

                    if (action.equals("transfer")) {

                    } else if (action.startsWith("remove")) {

                        String uuid = action.replace("remove ", "");

                        player.closeInventory();

                        player.performCommand("/p remove " + new ServerPlayer(UUID.fromString(uuid)).getName());

                        BukkitRunnable runnable = new BukkitRunnable() {
                            @Override
                            public void run() {
                                player.performCommand("/p manage");
                            }
                        };
                        runnable.runTaskLaterAsynchronously(plugin, 40);
                    } else if (action.equals("add")) {

                    }

                } else {
                    player.closeInventory();
                    player.sendMessage(projectsPrefix + "§cHa ocurrido un error.");
                }
            } catch (Exception e) {
                player.closeInventory();
                player.sendMessage(projectsPrefix + "§cHa ocurrido un error.");
            }
        }
    }

}
