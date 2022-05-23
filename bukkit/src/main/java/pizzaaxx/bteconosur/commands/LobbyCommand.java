package pizzaaxx.bteconosur.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import pizzaaxx.bteconosur.server.player.ServerPlayer;
import pizzaaxx.bteconosur.misc.Misc;
import pizzaaxx.bteconosur.yaml.Configuration;

import static pizzaaxx.bteconosur.BteConoSur.mainWorld;
import static pizzaaxx.bteconosur.projects.ProjectsCommand.background;

public class LobbyCommand implements CommandExecutor, Listener {

    private final Plugin plugin;

    public static final String tpPrefix = "§f[§7TELEPORT§f] §7>>§r ";

    public LobbyCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Configuration teleports = new Configuration(plugin, "teleports");
            Player p = (Player) sender;
            if (command.getName().equals("assets")) {
                p.teleport(new Location(mainWorld, teleports.getDouble("assets.x"), teleports.getDouble("assets.y"), teleports.getDouble("assets.z")));
                p.sendMessage(tpPrefix + "Teletransportándote a §oassets§r.");
            }

            if (command.getName().equals("lobby")) {
                Inventory inventory = Bukkit.createInventory(p, 27, "Selecciona un lobby");

                for (int i = 0; i < 27; i++) {
                    inventory.setItem(i, background);
                }

                inventory.setItem(26, Misc.getCustomHead("§fSalir", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0="));
                inventory.setItem(11, Misc.getCustomHead("§aArgentina", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjZkMDMzZGM1ZjY3NWFkNTFiYzA2YzdhMTk0OWMzNWExZDM3ZTQ4YTJlMWMyNzg5YzJjZjdkMzBlYzU4ZjMyYyJ9fX0="));
                inventory.setItem(12, Misc.getCustomHead("§aBolivia", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQyYzlmOTg2MThjZDVmN2RiZjBjMWE1NGVlMDk0NzQ2NjJiNzEzYjVhYTI2NWM4NWVmYmZjNDY0MThlOTE1In19fQ=="));
                inventory.setItem(13, Misc.getCustomHead("§aChile", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTAzNTk0YzBjMTE2YjA1ZDc1NjA2MGEyMjM5ODM3NzQ3ODg4NzMyMjY5MzVkOTYyNzExYmMzZTI1ODQ2ZGM2YiJ9fX0="));
                inventory.setItem(14, Misc.getCustomHead("§aParaguay", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE3OGRlNjkxYjUwOGQ2MjQ5MTA5ZmM1NGFmNmZiYTQ5YmFhODM3N2FkMzcwNjEyZWQ2MTdkNzdkZDZhZDU4OCJ9fX0="));
                inventory.setItem(15, Misc.getCustomHead("§aPerú", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjRkMDNiZDQ0MTBiYWJkYzY4MjQ5M2IzYzJiYmEyNmU3MzBlNmJjNjU4ZDM4ODhlNzliZjcxMmY4NTMifX19"));
                inventory.setItem(16, Misc.getCustomHead("§aUruguay", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjg0NDA1OTdjNGJjMmFhZDYwMGE1NDYwNGRjN2IxZmI3NzEzNDNlMDIyZTZhMmUwMjJmOTBlNDBjYzI1ZjlmOCJ9fX0="));
                inventory.setItem(10, Misc.getCustomHead("§aPrincipal", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThkYWExZTNlZDk0ZmYzZTMzZTFkNGM2ZTQzZjAyNGM0N2Q3OGE1N2JhNGQzOGU3NWU3YzkyNjQxMDYifX19"));

                p.openInventory(inventory);
                }
        }
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getName().equals("Selecciona un lobby") && (e.getInventory().getSize() == 27)) {
            e.setCancelled(true);
            if (e.getCurrentItem() != background) {
                Player p = (Player) e.getWhoClicked();
                ServerPlayer s = new ServerPlayer(p);
                if (e.getSlot() == 26) {
                    e.getWhoClicked().closeInventory();
                } else {
                    ItemMeta meta = e.getCurrentItem().getItemMeta();
                    if (meta.hasDisplayName()) {
                        String name = ChatColor.stripColor(meta.getDisplayName()).replace("Perú", "Peru").toLowerCase();
                        Configuration teleports = new Configuration(plugin, "teleports");
                        p.teleport(new Location(mainWorld, teleports.getDouble("lobby_" + name + ".x"), teleports.getDouble("lobby_" + name + ".y"), teleports.getDouble("lobby_" + name + ".z")));
                        p.sendMessage(tpPrefix + "Teletransportándote al lobby...");
                        e.getWhoClicked().closeInventory();
                    }
                }
            }
        }
    }
}
