package pizzaaxx.bteconosur.testing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

public class Testing implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
         /*
        ServerPlayer s = new ServerPlayer((Player) sender);

        if (s.getDiscordManager().isLinked()) {
            s.getDiscordManager().checkDiscordRoles(new OldCountry("chile"));
        }

          */
        return true;
    }
}
