package pizzaaxx.bteconosur.utilities;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class HatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }
        ItemStack stack = player.getInventory().getItemInMainHand();
        if (stack.getType().isAir()) {
            player.sendMessage(PREFIX + "Debes tener un item en la mano.");
            return true;
        }

        player.getInventory().setHelmet(stack);

        return true;
    }
}
