package pizzaaxx.bteconosur.projects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static pizzaaxx.bteconosur.misc.Misc.getCustomHead;
import static pizzaaxx.bteconosur.projects.ProjectsCommand.background;
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

        Player player = (Player) event.getWhoClicked();
        // Proyecto KIPOMP - DSTemuco

        if (name.startsWith("Proyecto ")) {

            event.setCancelled(true);

            String id = name.substring(9, 15).toLowerCase();

            try {
                Project project = new Project(id);

                if (project.getOwner().getUniqueId() == player.getUniqueId()) {

                    ItemStack item = inventory.getItem(event.getSlot());
                    if (item != null && item != background) {

                        ItemMeta meta = item.getItemMeta();
                        List<String> lore = meta.getLore();
                        String action = "";
                        for (String line : lore) {
                            if (line.startsWith("§0action: ")) {
                                action = line.replace("§0action: ", "");
                            }
                        }


                        if (action.equals("transfer")) {

                            Inventory gui = Bukkit.createInventory(null, 36, "Transferir el proyecto");

                            List<Integer> memberSlots = Arrays.asList(
                                    10, 11, 12, 13, 14, 15, 16,
                                    19, 20, 21, 22, 23, 24, 25
                            );

                            for (int i = 0; i < 36; i++) {
                                if (!memberSlots.contains(i)) {
                                    gui.setItem(i, background);
                                }
                            }

                            int i = 0;
                            for (OfflinePlayer member : project.getMembers()) {
                                ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                                SkullMeta m = (SkullMeta) head.getItemMeta();
                                ServerPlayer sMember = new ServerPlayer(member);
                                m.setDisplayName("§f" + sMember.getName());
                                m.setLore(Arrays.asList(
                                        sMember.getLoreWithoutTitle(),
                                        "\n§a[➡] §7Haz click para §etransferir §7el proyecto al jugador",
                                        "§0action: " + sMember.getPlayer().getUniqueId() + " " + project.getId()
                                ));
                                m.setOwningPlayer(member);
                                head.setItemMeta(m);

                                gui.setItem(memberSlots.get(i), head);

                                i++;
                            }

                            gui.setItem(35, getCustomHead("§f[⬅] Volver", "§0" + project.getId(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0="));

                            player.openInventory(gui);

                        } else if (action.startsWith("remove")) {

                            String uuid = action.replace("remove ", "");

                            player.closeInventory();

                            player.performCommand("p remove " + new ServerPlayer(UUID.fromString(uuid)).getName());

                            BukkitRunnable runnable = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.performCommand("p manage");
                                }
                            };
                            runnable.runTaskLaterAsynchronously(plugin, 40);
                        } else if (action.equals("add")) {
                            Inventory gui = Bukkit.createInventory(null, 54, "Añadir jugadores");
                            List<Integer> playerSlots = Arrays.asList(
                                    10, 11, 12, 13, 14, 15, 16,
                                    19, 20, 21, 22, 23, 24, 25,
                                    28, 29, 30, 31, 32, 33, 34,
                                    37, 38, 39, 40, 41, 42, 43
                            );
                            for (int i = 0; i < 54; i++) {
                                if (!playerSlots.contains(i)) {
                                    gui.setItem(i, background);
                                }
                            }
                            List<UUID> members = project.getAllMembers().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList());
                            int i = 0;
                            for (Player p : Bukkit.getOnlinePlayers()) {

                                if (!members.contains(p.getUniqueId())) {
                                    ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                                    SkullMeta m = (SkullMeta) head.getItemMeta();
                                    ServerPlayer sMember = new ServerPlayer(p);
                                    m.setDisplayName("§f" + sMember.getName());
                                    m.setLore(Arrays.asList(
                                            sMember.getLoreWithoutTitle(),
                                            "\n§a[+] §7Haz click para §aañadir §7al jugador al proyecto",
                                            "§0action: add " + sMember.getPlayer().getUniqueId() + " " + project.getId()
                                    ));
                                    m.setOwningPlayer(p);
                                    head.setItemMeta(m);

                                    gui.setItem(playerSlots.get(i), head);

                                    i++;
                                }

                            }

                            gui.setItem(53, getCustomHead("§f[⬅] Volver", "§0" + project.getId(), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0="));

                            player.openInventory(gui);
                        }
                    }

                } else {
                    player.closeInventory();
                    player.sendMessage(projectsPrefix + "§cHa ocurrido un error.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                player.closeInventory();
                player.sendMessage(projectsPrefix + "§cHa ocurrido un error.");
            }
        } else if (name.equals("Transferir el proyecto")) {

            ItemStack item = inventory.getItem(event.getSlot());

            if (event.getSlot() == 35) {
                player.closeInventory();

                String id = ChatColor.stripColor(item.getItemMeta().getLore().get(0));

                player.performCommand("p manage " + id);
                return;
            }


            if (item != null & item != background) {

                // TODO SUB MENUS

            }

        }
    }

}
