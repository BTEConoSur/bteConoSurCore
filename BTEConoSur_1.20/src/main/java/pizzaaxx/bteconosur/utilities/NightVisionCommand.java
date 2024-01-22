package pizzaaxx.bteconosur.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import static pizzaaxx.bteconosur.utils.ChatUtils.GREEN;
import static pizzaaxx.bteconosur.utils.ChatUtils.RED;

public class NightVisionCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // if sender is player give them nightvision effect
        // else send error message
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.sendActionBar(
                    Component.text("Visión nocturna desactivada")
                            .color(TextColor.color(RED))
            );
            return true;
        }
        player.addPotionEffect(
            new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 1, false, false)
        );
        player.sendActionBar(
                Component.text("Visión nocturna activada")
                        .color(TextColor.color(GREEN))
        );
        return true;
    }
}
