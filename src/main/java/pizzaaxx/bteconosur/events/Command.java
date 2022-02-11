package pizzaaxx.bteconosur.events;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static pizzaaxx.bteconosur.country.Country.countryAbbreviations;

public class Command implements CommandExecutor {

    public static String eventsPrefix = "[§bEVENTO§f] §7>> §f";

    @Override
    public boolean onCommand(CommandSender p, org.bukkit.command.Command command, String label, String[] args) {
        if (command.getName().equals("event")) {
            if (args.length > 0) {

            } else {
                // TODO GUI
            }
        }

        if (command.getName().equals("manageevent")) {
            if (args.length > 0) {
                if (countryAbbreviations.contains(args[0])) {
                    if (args.length > 1) {
                        if (args[1].equals("name")) {

                        } else if (args[1].equals("tp")) {

                        } else if (args[1].equals("redefine")) {

                        }
                    } else {
                        p.sendMessage(eventsPrefix + "Introduce una acción.");
                    }
                } else {
                    p.sendMessage(eventsPrefix + "Introduce un país válido.");
                }
            } else {
                p.sendMessage(eventsPrefix + "Introduce un país para manejar.");
            }
        }
        return true;
    }
}
