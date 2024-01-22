package pizzaaxx.bteconosur.utilities;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class PTimeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (args.length == 0) {



            return true;
        } else if (args.length == 1) {

            int ticks;
            if (args[0].matches("^\\d{1,5}$")) {
                // format is in ticks
                ticks = Integer.parseInt(args[0]);
            } else {
                Pattern pattern = Pattern.compile("^(0?\\d|1[0-2])(:[0-5]\\d)?(am|pm)$");
                Matcher matcher = pattern.matcher(args[0]);
                if (matcher.matches()) {

                    int hour = Integer.parseInt(matcher.group(1));
                    int minutes = 0;
                    if (matcher.group(2) != null) {
                        minutes = Integer.parseInt(matcher.group(2).substring(1));
                    }
                    boolean am = !matcher.group(3).equals("pm");

                    // 0 ticks is 6am
                    ticks = (((hour + (am ? 0 : 12) + 18) % 24)) * 1000 + minutes * (1000 / 60);

                } else {
                    player.sendMessage(PREFIX + "El formato de hora es incorrecto.");
                    return true;
                }
            }
            player.setPlayerTime(ticks, false);
            player.sendMessage(PREFIX + "Tu hora ha sido cambiada a " + args[0] + ".");
        }

        return true;
    }
}
