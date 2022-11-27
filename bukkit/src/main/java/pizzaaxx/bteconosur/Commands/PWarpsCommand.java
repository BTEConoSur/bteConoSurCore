package pizzaaxx.bteconosur.Commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.Prefixable;
import pizzaaxx.bteconosur.Player.Managers.MiscManager;
import pizzaaxx.bteconosur.Player.ServerPlayer;

import java.sql.SQLException;
import java.util.Map;

public class PWarpsCommand implements CommandExecutor{

    private final BTEConoSur plugin;

    public PWarpsCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Solo jugadores.");
            return true;
        }

        Player p = (Player) sender;
        ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());
        MiscManager miscManager = s.getMiscManager();

        if (args.length < 1) {
            p.sendMessage(plugin.getPrefix() + "Introduce un subcomando o el nombre de un warp personal.");
            return true;
        }

        if (args[0].equals("set")) {

            if (args.length < 2) {
                p.sendMessage(plugin.getPrefix() + "Introduce un nombre.");
                return true;
            }

            if (!args[1].matches("\b(?!set)\b\b(?!delete)\b\b(?!list)\b[a-zA-Z1-9_ñÑ]{1,32}")) {
                p.sendMessage(plugin.getPrefix() + "Introduce un nombre válido.");
                return true;
            }

            try {
                Location loc = p.getLocation();
                miscManager.setPWarp(args[1], loc);
                p.sendMessage(plugin.getPrefix() + "Warp personal §a" + args[1] + "§f establecido en §a" + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + "§f.");
            } catch (SQLException e) {
                p.sendMessage(plugin.getPrefix() + "Ha ocurrido un error en la base de datos.");
                return true;
            }

        } else if (args[0].equals("delete")) {



        } else if (args[0].equals("list")) {

            p.sendMessage("§8>-------[ §7PWARPS §8]-------<");
            for (Map.Entry<String, Location> pwarp : miscManager.getPWarps().entrySet()) {
                p.sendMessage("§8• §a" + pwarp.getKey() + "§7 - §a" + pwarp.getValue().getBlockX() + " " + pwarp.getValue().getBlockY() + " " + pwarp.getValue().getBlockZ());
            }
            p.sendMessage("§8>-----------------------<");

        } else if (miscManager.existsPWarp(args[0])) {

            Location loc = miscManager.getPWarp(args[0]);
            p.teleport(loc);
            p.sendMessage(plugin.getPrefix() + "Teletransportándote a §a" + args[0] + "§f.");

        } else {
            p.sendMessage(plugin.getPrefix() + "Introduce un subcomando o nombre válido.");
        }
        return true;
    }
}
