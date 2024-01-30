package pizzaaxx.bteconosur.utilities;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VanishCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (player.isInvisible()) {
            player.setInvisible(false);
            player.sendActionBar(Component.text("§aAhora eres visible."));
        } else {
            player.setInvisible(true);
            player.sendActionBar(Component.text("§aAhora eres invisible."));
        }

        return true;
    }
}
