package pizzaaxx.bteconosur.Schematics.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Player.ServerPlayer;

public class SchematicsCommand implements CommandExecutor {

    private final BTEConoSur plugin;
    private final String prefix;

    public SchematicsCommand(@NotNull BTEConoSur plugin) {
        this.plugin = plugin;
        this.prefix = plugin.getWorldEdit().getPrefix();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /schem create [nombre]
        // /schem edit [id] [nuevoNombre]
        // /schem delete [id]
        // /schem search [texto]
        // /schem addfav [id]
        // /schem removefav [id]
        // /schem fav

        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores.");
            return true;
        }

        Player p = (Player) sender;
        ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());

        if (args.length < 1) {
            p.sendMessage(prefix + "Introduce un subcomando.");
            return true;
        }

        switch (args[0]) {
            case "create": {

                break;
            }
        }

        return true;
    }
}
