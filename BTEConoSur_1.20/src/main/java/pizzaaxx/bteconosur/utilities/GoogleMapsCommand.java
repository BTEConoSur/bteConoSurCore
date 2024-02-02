package pizzaaxx.bteconosur.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.terra.TerraCoords;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class GoogleMapsCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public GoogleMapsCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        TerraCoords coords = TerraCoords.fromMc(
                player.getLocation().getBlockX(),
                player.getLocation().getBlockZ()
        );

        String url = "https://www.google.cl/maps/@" + coords.getLat() + "," + coords.getLon() + ",116m/data=!3m1!1e3?entry=ttu";

        player.sendMessage(
                Component.text(PREFIX + "Este es tu ")
                        .append(
                                Component.text(" §aenlace a GoogleMaps")
                                        .hoverEvent(
                                                Component.text("Haz clic para abrir en Google Maps")
                                        ).clickEvent(
                                                ClickEvent.openUrl(url)
                                        )
                        ).append(
                                Component.text("§f.")
                        )
        );

        return true;
    }
}
