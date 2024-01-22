package pizzaaxx.bteconosur.utilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static pizzaaxx.bteconosur.utils.ChatUtils.YELLOW;

public class HeightCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (player.getWorld().getName().equals("BTE_CS_1")) {
            player.sendActionBar(Component.text("Altitud: " + player.getLocation().getBlockY(), TextColor.color(YELLOW)));
        } else {
            player.sendActionBar(Component.text("Altitud: " + (player.getLocation().getBlockY() + 2032), TextColor.color(YELLOW)));
        }
        return true;
    }
}
