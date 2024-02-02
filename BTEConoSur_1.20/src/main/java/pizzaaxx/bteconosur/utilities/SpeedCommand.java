package pizzaaxx.bteconosur.utilities;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class SpeedCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(PREFIX + "Debes ingresar un valor.");
            return true;
        }

        try {
            float speed = Float.parseFloat(args[0]);
            if (speed < 0.1 || speed > 10) {
                player.sendMessage(PREFIX + "El valor debe estar entre 0.1 y 10.");
                return true;
            }
            if (player.isFlying()) {
                player.setFlySpeed(speed / 10);
                player.sendMessage(PREFIX +  "Velocidad de vuelo cambiada a " + speed);
            } else {
                // 0 -> 0
                // 1 -> 0.2
                // 10 -> 1
                float finalSpeed;
                if (speed < 1) {
                    finalSpeed = speed / 5;
                } else {
                    finalSpeed = (0.8f / 9) * speed + (1f/9);
                }
                player.setWalkSpeed(finalSpeed);
                player.sendMessage(PREFIX +  "Velocidad de caminata cambiada a " + speed);
            }
        } catch (NumberFormatException e) {
            player.sendMessage(PREFIX + "El valor debe ser un número.");
        }
        return true;
    }
}
