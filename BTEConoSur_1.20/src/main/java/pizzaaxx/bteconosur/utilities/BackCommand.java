package pizzaaxx.bteconosur.utilities;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class BackCommand implements CommandExecutor {

    public static final Map<UUID, Location> BACK_LOCATIONS = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (BACK_LOCATIONS.containsKey(player.getUniqueId())) {
            player.teleport(BACK_LOCATIONS.get(player.getUniqueId()));
            return true;
        } else {
            player.sendMessage(PREFIX + "Ha ocurrido un error.");
        }
        return true;
    }
}
