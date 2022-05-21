package pizzaaxx.bteconosur.worldedit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.server.player.DataManager;
import pizzaaxx.bteconosur.server.player.PlayerRegistry;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

import static pizzaaxx.bteconosur.worldedit.WorldEditHelper.WORLD_EDIT_PREFIX;

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
                DataManager data = serverPlayer.getDataManager();

                if (args[0].matches("[0-9]{1,3}")) {

                    int increment = Integer.parseInt(args[0]);
                    if (increment != data.getInt("increment")) {
                        data.set("increment", increment);
                        data.save();

                        player.sendMessage(WORLD_EDIT_PREFIX+ "Distancia de incremento establecida en §a" + data.getInt("increment") + "§f.");
                    }
                } else {
                    player.sendMessage(WORLD_EDIT_PREFIX + "Introduce un distancia válida (No más de 3 dígitos).");
                }
            } else {
                player.sendMessage(WORLD_EDIT_PREFIX + "Introduce una distancia.");
            }
        }

        return true;
    }
}
