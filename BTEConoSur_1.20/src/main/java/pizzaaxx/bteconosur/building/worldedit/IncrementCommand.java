package pizzaaxx.bteconosur.building.worldedit;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;

import java.sql.SQLException;

public class IncrementCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public IncrementCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        try {
            OnlineServerPlayer s = plugin.getPlayerRegistry().get(player.getUniqueId()).asOnlinePlayer();

            if (args.length != 1) {
                player.sendMessage("El incremento actual es de §a" + s.getWorldEditManager().getIncrement() + "§f.");
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage("La cantidad debe ser un número entero.");
                return true;
            }

            if (amount < 1) {
                player.sendMessage("La cantidad debe ser mayor a 0.");
                return true;
            }

            s.getWorldEditManager().setIncrement(amount);
            player.sendMessage("Se ha cambiado el incremento de WorldEdit a §a" + amount + "§f.");
        } catch (SQLException | JsonProcessingException e) {
            player.sendMessage("Ha ocurrido un error en la base de datos.");
        }
        return true;

    }
}
