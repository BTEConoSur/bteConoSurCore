package pizzaaxx.bteconosur.ranks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class prefix implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;

        if (command.getName().equals("prefix")) {
            Inventory inventory = Bukkit.createInventory(p, 36, "Selecciona un país");

            ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE);
            glass.setTypeId((byte) 15);
            ItemMeta meta = glass.getItemMeta();
            meta.setDisplayName("");
            glass.setItemMeta(meta);
            for (int i = 0; i < 36; i++) {
                inventory.setItem(i, glass);
            }

            p.openInventory(inventory);
        }

        return true;
    }
}
