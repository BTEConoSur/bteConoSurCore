package pizzaaxx.bteconosur.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVisionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
        }

        Player p = (Player) sender;

        if (p.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            p.sendMessage("§aVisión nocturna desactivada.");
        } else {
            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 1));
            p.sendMessage("§aVisión nocturna activada.");
        }


        return true;
    }
}
