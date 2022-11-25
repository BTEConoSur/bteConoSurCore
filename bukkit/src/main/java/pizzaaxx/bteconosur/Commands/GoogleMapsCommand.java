package pizzaaxx.bteconosur.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.ChatMessage;
import pizzaaxx.bteconosur.Chat.Components.ChatMessageComponent;
import pizzaaxx.bteconosur.Chat.Components.ClickAction;
import pizzaaxx.bteconosur.Chat.Components.HoverAction;
import pizzaaxx.bteconosur.Geo.Coords2D;

public class GoogleMapsCommand implements CommandExecutor {

    private final BTEConoSur plugin;

    public GoogleMapsCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores.");
            return true;
        }

        Player p = (Player) sender;

        Coords2D coords = new Coords2D(plugin, p.getLocation());

        String url = "https://www.google.com/maps/@" + coords.getLat() + "," + coords.getLon() + ",100m/data=!3m1!1e3";

        ChatMessage message = new ChatMessage(
                new ChatMessageComponent(
                        plugin.getPrefix()
                ),
                new ChatMessageComponent(
                        "Haz click "
                ),
                new ChatMessageComponent(
                        "aquí", ChatColor.GREEN, new HoverAction("Haz click para ir"), new ClickAction(url)
                ),
                new ChatMessageComponent(
                        " para ver dónde estás en GoogleMaps."
                )
        );

        p.sendMessage(message.getBaseComponents());

        return true;
    }
}
