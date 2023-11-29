package pizzaaxx.bteconosur.terra;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.terra.TerraConnector;
import pizzaaxx.bteconosur.terra.TerraCoords;

public class TpllCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public TpllCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cSolo jugadores pueden usar este comando.");
            return true;
        }

        if (command.getName().equals("tpll")) {
            double[] geoCoordinates = new double[2];
            geoCoordinates[1] = Double.parseDouble(args[0].replace(",", "").replace("°", ""));
            geoCoordinates[0] = Double.parseDouble(args[1].replace("°", ""));

            TerraCoords coords = TerraCoords.fromGeo(
                    geoCoordinates[0],
                    geoCoordinates[1]
            );

            double height;
            if (args.length >= 3) {
                height = Double.parseDouble(args[2]);
            } else {
                height = TerraConnector.getHeight((int) coords.getX(), (int) coords.getZ()).join();
            }

            World world;
            if (height < 2032) {
                world = plugin.getWorlds()[0];
            } else if (height < 4064) {
                world = plugin.getWorlds()[1];
                height -= 2032;
            } else {
                player.sendMessage("§7[TPLL] §8» §7No puedes teletransportarte a estas coordenadas, está demasiado alto.");
                return true;
            }

            Location location = new Location(
                    world,
                    coords.getX(),
                    (args.length >= 3
                            ? world.getHighestBlockYAt(
                                    (int) coords.getX(),
                                    (int) coords.getZ()
                            )
                            : height
                    ),
                    coords.getZ()
            );

            if (!location.isChunkLoaded()) {
                player.sendMessage("§7[TPLL] §8» §7Se esta generando el terreno, espera un momento...");

                player.teleportAsync(
                        location
                ).whenCompleteAsync(
                        (result, throwable) -> {
                            if (result) {
                                player.sendMessage("§7[TPLL] §8» §7Teletransportándote a §a" + coords.getLon() + "§7, §a" + coords.getLat() + "§7.");
                            }
                        }
                );
            } else {
                player.teleport(location);
                player.sendMessage("§7[TPLL] §8» §7Teletransportándote a §a" + coords.getLon() + "§7, §a" + coords.getLat() + "§7.");
            }
        }
        return true;
    }
}
