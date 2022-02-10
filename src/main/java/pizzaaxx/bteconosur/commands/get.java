package pizzaaxx.bteconosur.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import static pizzaaxx.bteconosur.misc.misc.getCustomHead;
import static pizzaaxx.bteconosur.misc.misc.itemBuilder;
import static pizzaaxx.bteconosur.projects.command.background;
import static pizzaaxx.bteconosur.worldguard.WorldGuardProvider.getWorldGuard;

public class get implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equals("get")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                Inventory gui = Bukkit.createInventory(p, 45, "Bloques especiales");
                for (int i=0; i < 45; i++) {
                    gui.setItem(i, background);
                }

                // SET HEADS
                gui.setItem(10, getCustomHead("§a[43:8] §fPiedra Lisa", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGRkMGNkMTU4YzJiYjY2MTg2NTBlMzk1NGIyZDI5MjM3ZjViNGMwZGRjN2QyNThlMTczODBhYjY5NzlmMDcxIn19fQ=="));
                gui.setItem(11, getCustomHead("§a[43:9] §fArenisca Lisa", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNmZTI0YTkxZTE0OTQ0ODYxZmYzN2M3YTYwZTE4YzVmYzU0YTQxYTg1MzFmOWJhNjEyNGQxNTI5MjI0OTgyNCJ9fX0="));
                gui.setItem(12, getCustomHead("§a[181:9] §fArenisca Roja Lisa", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDIzYjczMDcxNDBmOWZkNzU3YmNlY2JlYjg1NzM0ZjFlYWZkMDU3NGFlZWQ5ZTkzNWJiMWRiMzQ5NDlkMzM4OSJ9fX0="));
                gui.setItem(13, getCustomHead("§a[17:12] §fTronco de Roble Entero", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQyZTMxMDg3OWE2NDUwYWY1NjI1YmNkNDUwOTNkZDdlNWQ4ZjgyN2NjYmZlYWM2OWM4MTUzNzc2ODQwNmIifX19"));
                gui.setItem(14, getCustomHead("§a[17:13] §fTronco de Abeto Entero", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDI2ZWVmZDg3MjYzY2M0ODVjMTdjYjVmODE4ZWUzYmFkOTNjYTc2OTEzODVjYjVlNWQ2OThhZmY3MzNhMyJ9fX0="));
                gui.setItem(15, getCustomHead("§a[17:14] §fTronco de Abedul Entero", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk5MWYzYjczZWJiOWRlYzkxZWRkYzgzNjFjYTJmZWNmNTI4MGQyYzczM2VkYTllY2I2OTVmODNkMTU4MCJ9fX0="));
                gui.setItem(16, getCustomHead("§a[17:15] §fTronco de Jungla Entero", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzljYTM1NDA2MjFjMWM3OWMzMmJmNDI0Mzg3MDhmZjFmNWY3ZDBhZjliMTRhMDc0NzMxMTA3ZWRmZWI2OTFjIn19fQ=="));
                gui.setItem(19, getCustomHead("§a[162:12] §fTronco de Acacia Entero", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTY2NjlmZTJkYmY3ODc5MmEzZTE5MTYyMmE4ZWQxZjllYjgwM2Y4ODI2YzliOTQ5ZDBkYzE1YTUxYzU5MzkxIn19fQ=="));
                gui.setItem(20, getCustomHead("§a[162:13] §fTronco de Roble Oscuro Entero", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjc3ZGM2ZThiNDVhZGNmOTI4ZjJjNjEzOWRmYTJhMzYxYWExMDdiODljOWFkMzVjMzQ3YjY3N2EwN2M3OWY1In19fQ=="));
                gui.setItem(21, getCustomHead("§a[100:14] §fChampiñón Rojo", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGUzNWYzODkzNGMzNzFmNWQxNWUzNGZhNDQ1ZDY5OGZkMTQxZWQ3ZTc1MjkzMTk5MzkwYjBiZDE2NzhlZiJ9fX0="));
                gui.setItem(22, getCustomHead("§a[99:14] §fChampiñón Café", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmE0OWVjYTAzNjlkMWUxNThlNTM5ZDc4MTQ5YWNiMTU3Mjk0OWI4OGJhOTIxZDllZTY5NGZlYTRjNzI2YjMifX19"));
                gui.setItem(23, getCustomHead("§a[99:0 / 100:0] §fChampiñón Interior", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ZhMzljY2Y0Nzg4ZDkxNzlhODc5NWU2YjcyMzgyZDQ5Mjk3YjM5MjE3MTQ2ZWRhNjhhZTc4Mzg0MzU1YjEzIn19fQ=="));
                gui.setItem(24, getCustomHead("§a[99:15 / 100:15] §fTallo de Champiñón Entero", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjU1ZmE2NDJkNWViY2JhMmM1MjQ2ZmU2NDk5YjFjNGY2ODAzYzEwZjE0ZjUyOTljOGU1OTgxOWQ1ZGMifX19"));
                gui.setItem(25, getCustomHead("§a[167:5] §fTrampilla de Hierro Norte", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI3ZDJlODRhZTEwYzk3MWQ5MmNiMDViNmFiNDExNmY3NjUwM2E4N2QzMTc0MjQ5Y2QxZjQ5OTJiYTE4MWRiNCJ9fX0="));
                gui.setItem(29, getCustomHead("§a[167:4] §fTrampilla de Hierro Sur", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI3ZDJlODRhZTEwYzk3MWQ5MmNiMDViNmFiNDExNmY3NjUwM2E4N2QzMTc0MjQ5Y2QxZjQ5OTJiYTE4MWRiNCJ9fX0="));
                gui.setItem(30, getCustomHead("§a[167:6] §fTrampilla de Hierro Este", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI3ZDJlODRhZTEwYzk3MWQ5MmNiMDViNmFiNDExNmY3NjUwM2E4N2QzMTc0MjQ5Y2QxZjQ5OTJiYTE4MWRiNCJ9fX0="));
                gui.setItem(31, getCustomHead("§a[167:7] §fTrampilla de Hierro Oeste", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI3ZDJlODRhZTEwYzk3MWQ5MmNiMDViNmFiNDExNmY3NjUwM2E4N2QzMTc0MjQ5Y2QxZjQ5OTJiYTE4MWRiNCJ9fX0="));
                gui.setItem(32, itemBuilder(Material.BARRIER, "§a[166] §fBarrera"));
                gui.setItem(33, itemBuilder(Material.STRUCTURE_VOID, "§a[217] §fVacío"));

                p.openInventory(gui);
            }
        }

        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getName().equals("Bloques especiales")) {
            if (e.getCurrentItem() == background) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (getWorldGuard().canBuild(e.getPlayer(), e.getClickedBlock().getRelative(e.getBlockFace()))) {
                Player p = e.getPlayer();
                if (p.getInventory().getItemInMainHand() != null) {
                    if (p.getInventory().getItemInMainHand().getType() == Material.SKULL_ITEM) {
                        if (p.getInventory().getItemInMainHand().getItemMeta().hasDisplayName() && p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().startsWith("§a[")) {
                            String name  = p.getInventory().getItemInMainHand().getItemMeta().getDisplayName();

                            Material material;
                            byte metadata;
                            switch (name) {
                                case "§a[43:9] §fArenisca Lisa":
                                    material = Material.DOUBLE_STEP;
                                    metadata = 9;
                                    break;
                                case "§a[181:9] §fArenisca Roja Lisa":
                                    material = Material.DOUBLE_STONE_SLAB2;
                                    metadata = 9;
                                    break;
                                case "§a[17:12] §fTronco de Roble Entero":
                                    material = Material.LOG;
                                    metadata = 12;
                                    break;
                                case "§a[17:13] §fTronco de Abeto Entero":
                                    material = Material.LOG;
                                    metadata = 13;
                                    break;
                                case "§a[17:14] §fTronco de Abedul Entero":
                                    material = Material.LOG;
                                    metadata = 14;
                                    break;
                                case "§a[17:15] §fTronco de Jungla Entero":
                                    material = Material.LOG;
                                    metadata = 15;
                                    break;
                                case "§a[162:12] §fTronco de Acacia Entero":
                                    material = Material.LOG_2;
                                    metadata = 12;
                                    break;
                                case "§a[162:13] §fTronco de Roble Oscuro Entero":
                                    material = Material.LOG_2;
                                    metadata = 13;
                                    break;
                                case "§a[100:14] §fChampiñón Rojo":
                                    material = Material.HUGE_MUSHROOM_2;
                                    metadata = 14;
                                    break;
                                case "§a[99:14] §fChampiñón Café":
                                    material = Material.HUGE_MUSHROOM_1;
                                    metadata = 14;
                                    break;
                                case "§a[99:0 / 100:0] §fChampiñón Interior":
                                    material = Material.HUGE_MUSHROOM_2;
                                    metadata = 0;
                                    break;
                                case "§a[99:15 / 100:15] §fTallo de Champiñón Entero":
                                    material = Material.HUGE_MUSHROOM_2;
                                    metadata = 15;
                                    break;
                                case "§a[167:5] §fTrampilla de Hierro Norte":
                                    material = Material.IRON_TRAPDOOR;
                                    metadata = 5;
                                    break;
                                case "§a[167:4] §fTrampilla de Hierro Sur":
                                    material = Material.IRON_TRAPDOOR;
                                    metadata = 4;
                                    break;
                                case "§a[167:6] §fTrampilla de Hierro Este":
                                    material = Material.IRON_TRAPDOOR;
                                    metadata = 6;
                                    break;
                                case "§a[167:7] §fTrampilla de Hierro Oeste":
                                    material = Material.IRON_TRAPDOOR;
                                    metadata = 7;
                                    break;
                                default:
                                    material = Material.DOUBLE_STEP;
                                    metadata = 8;
                                    break;
                            }

                            e.setCancelled(true);
                            e.getClickedBlock().getRelative(e.getBlockFace()).setType(material);
                            e.getClickedBlock().getRelative(e.getBlockFace()).setData(metadata);
                        }
                    }
                }
            }
        }
    }
}
