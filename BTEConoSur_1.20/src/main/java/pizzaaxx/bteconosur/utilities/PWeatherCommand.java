package pizzaaxx.bteconosur.utilities;

import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class PWeatherCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(PREFIX + "Debes especificar el tipo de clima.");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "clear" -> {
                player.setPlayerWeather(WeatherType.CLEAR);
                player.sendMessage(PREFIX + "El clima ha sido cambiado a despejado.");
            }
            case "rain" -> {
                player.setPlayerWeather(WeatherType.DOWNFALL);
                player.sendMessage(PREFIX + "El clima ha sido cambiado a lluvia.");
            }
            default -> player.sendMessage(PREFIX + "El tipo de clima es incorrecto.");
        }

        return true;
    }
}
