package pizzaaxx.bteconosur.worldedit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.player.data.PlayerData;

import static pizzaaxx.bteconosur.worldedit.Methods.wePrefix;

public class IncrementCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equals("increment")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                PlayerData pData = new PlayerData(p);
                if (args.length > 0) {
                    if (args[0].matches("[0-9]{1,3}")) {
                        int d = Integer.parseInt(args[0]);
                        if (d != (Integer) pData.getData("increment")) {
                            pData.setData("increment", d);
                            pData.save();

                            p.sendMessage(wePrefix + "Distancia de incremento establecida en §a" + d + "§f.");
                        }
                    } else {
                        p.sendMessage(wePrefix + "Introduce un distancia válida (No más de 3 dígitos).");
                    }
                } else {
                    p.sendMessage(wePrefix + "Introduce una distancia.");
                }
            }
        }

        return true;
    }
}
