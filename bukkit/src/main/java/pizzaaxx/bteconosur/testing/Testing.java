package pizzaaxx.bteconosur.testing;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import static pizzaaxx.bteconosur.country.OldCountry.allCountries;

public class Testing implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Bukkit.getConsoleSender().sendMessage("test0");
        ServerPlayer s = new ServerPlayer((Player) sender);

        Bukkit.getConsoleSender().sendMessage("test1");
        if (s.getDiscordManager().isLinked()) {
            Bukkit.getConsoleSender().sendMessage("test2");
            for (OldCountry country : allCountries) {
                s.getDiscordManager().checkDiscordRoles(country);
            }
        }

        return true;
    }
}
