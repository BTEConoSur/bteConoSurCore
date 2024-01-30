package pizzaaxx.bteconosur.utilities;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class EditSignCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public EditSignCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Block target = player.getTargetBlock(null, 5);
        if (target.isEmpty()) {
            player.sendMessage(PREFIX + "Debes estar mirando un cartel.");
            return true;
        }

        if (!target.getType().name().contains("SIGN")) {
            player.sendMessage(PREFIX + "Debes estar mirando un cartel.");
            return true;
        }

        Sign sign = (Sign) target.getState();
        player.openSign(sign, sign.getInteractableSideFor(player));

        return true;
    }
}
