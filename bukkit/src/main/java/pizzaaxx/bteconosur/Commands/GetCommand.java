package pizzaaxx.bteconosur.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Inventory.InventoryGUI;
import pizzaaxx.bteconosur.Inventory.ItemBuilder;

import java.util.ArrayList;

public class GetCommand implements CommandExecutor, Listener {

    private final BTEConoSur plugin;

    public GetCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores.");
            return true;
        }

        Player p = (Player) sender;

        InventoryGUI gui = new InventoryGUI(
                5,
                "Bloques especiales"
        );

        // TODO FIX THIS

        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGRkMGNkMTU4YzJiYjY2MTg2NTBlMzk1NGIyZDI5MjM3ZjViNGMwZGRjN2QyNThlMTczODBhYjY5NzlmMDcxIn19fQ=="
        , "§a[43:8] §fPiedra Lisa", new ArrayList<>()), 10);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNmZTI0YTkxZTE0OTQ0ODYxZmYzN2M3YTYwZTE4YzVmYzU0YTQxYTg1MzFmOWJhNjEyNGQxNTI5MjI0OTgyNCJ9fX0="
        , "§a[43:9] §fArenisca Lisa", new ArrayList<>()), 11);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDIzYjczMDcxNDBmOWZkNzU3YmNlY2JlYjg1NzM0ZjFlYWZkMDU3NGFlZWQ5ZTkzNWJiMWRiMzQ5NDlkMzM4OSJ9fX0="
        , "§a[181:9] §fArenisca Roja Lisa", new ArrayList<>()), 12);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQyZTMxMDg3OWE2NDUwYWY1NjI1YmNkNDUwOTNkZDdlNWQ4ZjgyN2NjYmZlYWM2OWM4MTUzNzc2ODQwNmIifX19"
        , "§a[17:12] §fTronco de Roble Entero", new ArrayList<>()), 13);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDI2ZWVmZDg3MjYzY2M0ODVjMTdjYjVmODE4ZWUzYmFkOTNjYTc2OTEzODVjYjVlNWQ2OThhZmY3MzNhMyJ9fX0="
        , "§a[17:13] §fTronco de Abeto Entero", new ArrayList<>()), 14);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk5MWYzYjczZWJiOWRlYzkxZWRkYzgzNjFjYTJmZWNmNTI4MGQyYzczM2VkYTllY2I2OTVmODNkMTU4MCJ9fX0="
        , "§a[17:14] §fTronco de Abedul Entero", new ArrayList<>()), 15);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzljYTM1NDA2MjFjMWM3OWMzMmJmNDI0Mzg3MDhmZjFmNWY3ZDBhZjliMTRhMDc0NzMxMTA3ZWRmZWI2OTFjIn19fQ=="
        , "§a[17:15] §fTronco de Jungla Entero", new ArrayList<>()), 16);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTY2NjlmZTJkYmY3ODc5MmEzZTE5MTYyMmE4ZWQxZjllYjgwM2Y4ODI2YzliOTQ5ZDBkYzE1YTUxYzU5MzkxIn19fQ=="
        , "§a[162:12] §fTronco de Acacia Entero", new ArrayList<>()), 19);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjc3ZGM2ZThiNDVhZGNmOTI4ZjJjNjEzOWRmYTJhMzYxYWExMDdiODljOWFkMzVjMzQ3YjY3N2EwN2M3OWY1In19fQ=="
        , "§a[162:13] §fTronco de Roble Oscuro Entero", new ArrayList<>()), 20);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGUzNWYzODkzNGMzNzFmNWQxNWUzNGZhNDQ1ZDY5OGZkMTQxZWQ3ZTc1MjkzMTk5MzkwYjBiZDE2NzhlZiJ9fX0="
        , "§a[100:14] §fChampiñón Rojo", new ArrayList<>()), 21);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmE0OWVjYTAzNjlkMWUxNThlNTM5ZDc4MTQ5YWNiMTU3Mjk0OWI4OGJhOTIxZDllZTY5NGZlYTRjNzI2YjMifX19"
        , "§a[99:14] §fChampiñón Café", new ArrayList<>()), 22);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ZhMzljY2Y0Nzg4ZDkxNzlhODc5NWU2YjcyMzgyZDQ5Mjk3YjM5MjE3MTQ2ZWRhNjhhZTc4Mzg0MzU1YjEzIn19fQ=="
        , "§a[99:0 / 100:0] §fChampiñón Interior", new ArrayList<>()), 23);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjU1ZmE2NDJkNWViY2JhMmM1MjQ2ZmU2NDk5YjFjNGY2ODAzYzEwZjE0ZjUyOTljOGU1OTgxOWQ1ZGMifX19"
        , "§a[99:15 / 100:15] §fTallo de Champiñón Entero", new ArrayList<>()), 24);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI3ZDJlODRhZTEwYzk3MWQ5MmNiMDViNmFiNDExNmY3NjUwM2E4N2QzMTc0MjQ5Y2QxZjQ5OTJiYTE4MWRiNCJ9fX0="
        , "§a[167:5] §fTrampilla de Hierro Norte", new ArrayList<>()), 25);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI3ZDJlODRhZTEwYzk3MWQ5MmNiMDViNmFiNDExNmY3NjUwM2E4N2QzMTc0MjQ5Y2QxZjQ5OTJiYTE4MWRiNCJ9fX0="
        , "§a[167:4] §fTrampilla de Hierro Sur", new ArrayList<>()), 29);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI3ZDJlODRhZTEwYzk3MWQ5MmNiMDViNmFiNDExNmY3NjUwM2E4N2QzMTc0MjQ5Y2QxZjQ5OTJiYTE4MWRiNCJ9fX0="
        , "§a[167:6] §fTrampilla de Hierro Este", new ArrayList<>()), 30);
        gui.setItem(ItemBuilder.head(
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI3ZDJlODRhZTEwYzk3MWQ5MmNiMDViNmFiNDExNmY3NjUwM2E4N2QzMTc0MjQ5Y2QxZjQ5OTJiYTE4MWRiNCJ9fX0="
        , "§a[167:7] §fTrampilla de Hierro Oeste", new ArrayList<>()), 31);
        gui.setItem(ItemBuilder.of(Material.BARRIER).name("§a[166] §fBarrera").build(), 32);
        gui.setItem(ItemBuilder.of(Material.STRUCTURE_VOID).name("§a[217] §fVacío").build(), 33);

        gui.setDraggable(10,11,12,13,14,15,16,19,20,21,22,23,24,25,29,30,31,32,33);

        plugin.getInventoryHandler().open(p, gui);

        return true;
    }
}
