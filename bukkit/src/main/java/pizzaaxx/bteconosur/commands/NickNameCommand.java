package pizzaaxx.bteconosur.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.helper.NickNameValidator;
import pizzaaxx.bteconosur.server.player.NewServerPlayer;
import pizzaaxx.bteconosur.server.player.PlayerRegistry;


public class NickNameCommand implements CommandExecutor {
    public static final String NICK_PREFIX = "§f[§9NICK§f] §7>>§r ";

    private final PlayerRegistry playerRegistry;

    public NickNameCommand(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        if (args.length > 0) {
            sender.sendMessage("Introduce un apodo");
            return true;
        }

        Player player = (Player) sender;
        NewServerPlayer newServerPlayer = playerRegistry.get(player.getUniqueId());

        if (args[0].equals("set") && args.length > 2) {
            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage("El jugador no está online");
                return true;
            }

            NewServerPlayer targetServerPlayer = playerRegistry.get(target.getUniqueId());

            updateNick(targetServerPlayer, args[2], sender);
            return true;
        }
        String nickUpdated = args[0];

        updateNick(newServerPlayer, nickUpdated, sender);
        return true;
    }

    private void updateNick(NewServerPlayer newServerPlayer, String name, CommandSender sender) {
        if (!NickNameValidator.validate(name)) {
            sender.sendMessage("El nick no es valido");
            return;
        }
        newServerPlayer.setNick(name);
    }

}
