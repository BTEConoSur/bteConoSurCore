package pizzaaxx.bteconosur.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.coords.Coords2D;
import xyz.upperlevel.spigot.book.BookUtil;

public class GoogleMapsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores.");
        }

        Player p = (Player) sender;
        Coords2D coords = new Coords2D(p.getLocation());

        BookUtil.TextBuilder prefix = BookUtil.TextBuilder.of("§f[§aGOOGLE MAPS§f] §7>>§r Enlace a GoogleMaps: ");
        BookUtil.TextBuilder url = BookUtil.TextBuilder.of("§a§nCLICK AQUÍ")
                .onHover(BookUtil.HoverAction.showText("Haz click para abrir el enlace."))
                .onClick(BookUtil.ClickAction.openUrl("https://www.google.com/maps/@" + coords.getLat() + "," + coords.getLon() + ",19z"));

        p.sendMessage(prefix.build(), url.build());

        return true;
    }
}
