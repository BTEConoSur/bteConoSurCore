package pizzaaxx.bteconosur.worldedit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.PlayerRegistry;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.player.data.PlayerData;

import static pizzaaxx.bteconosur.worldedit.Methods.wePrefix;

public class IncrementCommand implements CommandExecutor {

    private final PlayerRegistry playerRegistry;

    public IncrementCommand(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length > 0) {

                ServerPlayer serverPlayer = playerRegistry.get(player.getUniqueId());
                PlayerData playerData = serverPlayer.getData();

                if (args[0].matches("[0-9]{1,3}")) {

                    int increment = Integer.parseInt(args[0]);
                    if (increment != (Integer) playerData.getData("increment")) {
                        playerData.setData("increment", playerData);
                        playerData.save();

                        player.sendMessage(wePrefix + "Distancia de incremento establecida en §a" + playerData + "§f.");
                    }
                } else {
                    player.sendMessage(wePrefix + "Introduce un distancia válida (No más de 3 dígitos).");
                }
            } else {
                player.sendMessage(wePrefix + "Introduce una distancia.");
            }
        }

        return true;
    }
}
