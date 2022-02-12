package pizzaaxx.bteconosur.events;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pizzaaxx.bteconosur.yaml.YamlManager;

import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.country.Country.countryAbbreviations;

public class EventsCommand implements CommandExecutor {

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
                        YamlManager yaml = new YamlManager(pluginFolder, "event.yml");
                        if (args[1].equals("name")) {
                            if (args.length > 2 && args[2].matches("[A-Za-z0-9]{1,32}")) {

                            }
                        } else if (args[1].equals("tp")) {

                        } else if (args[1].equals("redefine")) {

                        } else if (args[1].equals("groups")) {

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
