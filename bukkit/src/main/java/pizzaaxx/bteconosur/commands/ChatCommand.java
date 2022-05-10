package pizzaaxx.bteconosur.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.helper.Arguments;
import pizzaaxx.bteconosur.server.player.NewServerPlayer;
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
            Player player = ((Player) sender).getPlayer();


            String argumentOne = arguments.getArgument(0);
            if (!argumentOne.isEmpty()) {
                NewServerPlayer newServerPlayer = playerRegistry.get(player.getUniqueId());

                newServerPlayer.setChannelChat(argumentOne);
                player.sendMessage("Has seteado tu canal de chat: " + argumentOne);

                return true;
            }

            sender.sendMessage("> Especifica un tipo de chat");
            return true;
        }
        return false;
    }
}
