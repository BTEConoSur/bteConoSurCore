package pizzaaxx.bteconosur.utilities;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.gui.ItemBuilder;

public class SkullCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        OfflinePlayer target;
        if (args.length == 0) {
            target = player;
        } else {
            target = player.getServer().getOfflinePlayer(args[0]);
        }

        player.getInventory().addItem(
                ItemBuilder.head(
                        target.getUniqueId(),
                        "§aCabeza de " + target.getName(),
                        null
                )
        );

        return true;
    }
}
