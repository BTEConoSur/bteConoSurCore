package pizzaaxx.bteconosur.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.helper.Arguments;
import pizzaaxx.bteconosur.server.player.PlayerRegistry;

public class ChatCommand implements CommandExecutor {

    private final PlayerRegistry playerRegistry;

    public ChatCommand(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Arguments arguments = Arguments.from(args);

            arguments.getArgument(0);


            return true;
        }
        return false;
    }
}
