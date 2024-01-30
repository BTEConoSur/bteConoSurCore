package pizzaaxx.bteconosur.utilities;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;

import java.sql.SQLException;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class TPToggleCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public TPToggleCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        OfflineServerPlayer s = plugin.getPlayerRegistry().get(player.getUniqueId());

        try {
            if (s.allowsTP()) {
                s.setAllowTP(false);
                player.sendActionBar(Component.text("§cTeletransportes hacia ti desactivados"));
            } else {
                s.setAllowTP(true);
                player.sendActionBar(Component.text("§aTeletransportes hacia ti activados"));
            }
        } catch (SQLException e) {
            player.sendMessage(PREFIX + "Ha ocurrido un error en la base de datos.");
        }

        return true;
    }
}
