package pizzaaxx.bteconosur.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import pizzaaxx.bteconosur.misc.Misc;
import pizzaaxx.bteconosur.ServerPlayer.ChatManager;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;

import java.util.Objects;

import static pizzaaxx.bteconosur.projects.ProjectsCommand.background;

public class PrefixCommand implements CommandExecutor, Listener {
    public static String prefixPrefix = "§f[§3PAÍS§f] §7>>§r ";

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getName().equals("Selecciona un país") && (e.getInventory().getSize() == 36)) {
            e.setCancelled(true);
            if (e.getCurrentItem() != background) {
                Player p = (Player) e.getWhoClicked();
                ServerPlayer s = new ServerPlayer(p);
                ChatManager manager = s.getChatManager();
                if (e.getSlot() == 35) {
                    e.getWhoClicked().closeInventory();
                } else if (e.getSlot() == 22) {
                    if (s.getChatManager().getDisplayName() != null) {
                        manager.setCountryPrefix(null);
                        p.sendMessage(prefixPrefix + "Has eliminado tu prefijo de país.");
                    } else {
                        p.sendMessage(prefixPrefix + "No tienes un prefijo de país establecido.");
                    }
                    e.getWhoClicked().closeInventory();
                } else {
                    String name = e.getCurrentItem().getItemMeta().getDisplayName();
                    if (!Objects.equals(name, manager.getCountryPrefix())) {
                        manager.setCountryPrefix(name.replace("§", "&"));
                        p.sendMessage(prefixPrefix + "Has establecido tu prefijo de país en " + name + "§f.");
                    } else {
                        p.sendMessage(prefixPrefix + "Tu prefijo de país ya es el seleccionado.");
                    }
                    e.getWhoClicked().closeInventory();
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;

        Inventory inventory = Bukkit.createInventory(p, 36, "Selecciona un país");

        for (int i = 0; i < 36; i++) {
            inventory.setItem(i, background);
        }

        inventory.setItem(35, Misc.getCustomHead("§fSalir", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0="));
        inventory.setItem(10, Misc.getCustomHead("§b[AR§fGE§eN§fTI§bNA]", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjZkMDMzZGM1ZjY3NWFkNTFiYzA2YzdhMTk0OWMzNWExZDM3ZTQ4YTJlMWMyNzg5YzJjZjdkMzBlYzU4ZjMyYyJ9fX0="));
        inventory.setItem(11, Misc.getCustomHead("§4[BO§eLIV§2IA]", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQyYzlmOTg2MThjZDVmN2RiZjBjMWE1NGVlMDk0NzQ2NjJiNzEzYjVhYTI2NWM4NWVmYmZjNDY0MThlOTE1In19fQ=="));
        inventory.setItem(12, Misc.getCustomHead("§9[C§fHIL§cE]", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTAzNTk0YzBjMTE2YjA1ZDc1NjA2MGEyMjM5ODM3NzQ3ODg4NzMyMjY5MzVkOTYyNzExYmMzZTI1ODQ2ZGM2YiJ9fX0="));
        inventory.setItem(13, Misc.getCustomHead("§4[P§fAR§7AG§fUA§1Y]", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE3OGRlNjkxYjUwOGQ2MjQ5MTA5ZmM1NGFmNmZiYTQ5YmFhODM3N2FkMzcwNjEyZWQ2MTdkNzdkZDZhZDU4OCJ9fX0="));
        inventory.setItem(14, Misc.getCustomHead("§4[P§fER§4U]", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjRkMDNiZDQ0MTBiYWJkYzY4MjQ5M2IzYzJiYmEyNmU3MzBlNmJjNjU4ZDM4ODhlNzliZjcxMmY4NTMifX19"));
        inventory.setItem(15, Misc.getCustomHead("§f[§eU§fR§9U§fG§9U§fA§9Y§f]", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjg0NDA1OTdjNGJjMmFhZDYwMGE1NDYwNGRjN2IxZmI3NzEzNDNlMDIyZTZhMmUwMjJmOTBlNDBjYzI1ZjlmOCJ9fX0="));
        inventory.setItem(16, Misc.getCustomHead("§7[INTERNACIONAL]", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThkYWExZTNlZDk0ZmYzZTMzZTFkNGM2ZTQzZjAyNGM0N2Q3OGE1N2JhNGQzOGU3NWU3YzkyNjQxMDYifX19"));
        inventory.setItem(22, Misc.getCustomHead("§cEliminar país", null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ=="));

        p.openInventory(inventory);


        return true;
    }
}
