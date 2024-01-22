package pizzaaxx.bteconosur.utilities;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class JumpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        Block targetBlock = player.getTargetBlock(null, 100);
        Location loc = targetBlock.getLocation();

        float pitch = player.getLocation().getPitch();
        float yaw = player.getLocation().getYaw();
        // get unit vector of pitch/yaw
        double pitchRadians = Math.toRadians(pitch);
        double yawRadians = Math.toRadians(yaw);
        double x = -Math.cos(pitchRadians) * Math.sin(yawRadians);
        double y = -Math.sin(pitchRadians);
        double z = Math.cos(pitchRadians) * Math.cos(yawRadians);

        Vector vector = new Vector(x, y, z);
        loc = loc.add(vector);

        int counter = 0;
        while (!loc.getBlock().isEmpty()) {
            loc = loc.add(vector);
            counter++;
            if (counter > 100 || loc.getY() > 2032 || loc.getY() < 0) {
                player.sendMessage("No se ha encontrado un lugar para teletransportarte.");
                return true;
            }
        }

        player.teleport(loc);

        return true;
    }
}
