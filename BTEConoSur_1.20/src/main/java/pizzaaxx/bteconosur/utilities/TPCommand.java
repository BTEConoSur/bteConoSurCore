package pizzaaxx.bteconosur.utilities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;

import static pizzaaxx.bteconosur.BTEConoSurPlugin.PREFIX;

public class TPCommand implements CommandExecutor {

    private final BTEConoSurPlugin plugin;

    public TPCommand(BTEConoSurPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // /tp <player> -> Teleport this player to the target player -> Executable only by player
        // /tp <x> <y> <z> -> Teleport this player to the target coordinates -> Executable only by player
        // /tp <player> <player> -> Teleport the first player to the second player -> Executable by player and console
        // /tp <player> <x> <y> <z> -> Teleport the player to the target coordinates -> Executable by player and console

        if (args.length == 0) {
            sender.sendMessage(PREFIX + "Uso: /tp <jugador> | <x> <y> <z> | <jugador> <jugador> | <jugador> <x> <y> <z>");
            return true;
        }

        boolean canTeleportOthers = false;
        if (sender instanceof Player player) {
            OfflineServerPlayer s = plugin.getPlayerRegistry().get(player.getUniqueId());
            try {
                canTeleportOthers = s.getRoles().contains(OfflineServerPlayer.Role.ADMIN) || s.getRoles().contains(OfflineServerPlayer.Role.MOD);
            } catch (Exception e) {
                player.sendMessage(PREFIX + "Ha ocurrido un error en la base de datos.");
            }
        } else if (sender instanceof ConsoleCommandSender) {
            canTeleportOthers = true;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(PREFIX + "Este comando solo puede ser ejecutado por un jugador.");
                return true;
            }
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(PREFIX + "El jugador no está conectado.");
                return true;
            }

            // check if target player allow teleports, override if this player can teleport others
            OfflineServerPlayer s = plugin.getPlayerRegistry().get(target.getUniqueId());
            if (!canTeleportOthers && !s.allowsTP()) {
                sender.sendMessage(PREFIX + "El jugador no permite teletransportes.");
                return true;
            }

            player.teleport(target);
            sender.sendMessage(PREFIX + "Teletransportado a §a" + target.getName() + "§f.");
            return true;
        } else if (args.length == 2) {

            if (!canTeleportOthers) {
                sender.sendMessage(PREFIX + "No tienes permisos para teletransportar a otros jugadores.");
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(PREFIX + "Este comando solo puede ser ejecutado por un jugador.");
                return true;
            }
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(PREFIX + "El jugador no está conectado.");
                return true;
            }
            Player destination = plugin.getServer().getPlayer(args[1]);
            if (destination == null) {
                sender.sendMessage(PREFIX + "El jugador no está conectado.");
                return true;
            }
            target.teleport(destination);
            sender.sendMessage(PREFIX + "Has teletransportado a §a" + target.getName() + "§f a §a" + destination.getName() + "§f.");
            return true;
        } else if (args.length == 3) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(PREFIX + "Este comando solo puede ser ejecutado por un jugador.");
                return true;
            }
            try {
                double x, y, z;
                if (args[0].equals("~")) {
                    x = player.getLocation().getX();
                } else {
                    x = Double.parseDouble(args[0]);
                }

                if (args[1].equals("~")) {
                    y = player.getLocation().getY();
                } else {
                    y = Double.parseDouble(args[1]);
                }

                World world = plugin.getWorld(y);

                if (args[2].equals("~")) {
                    z = player.getLocation().getZ();
                } else {
                    z = Double.parseDouble(args[2]);
                }

                player.teleport(
                        new Location(
                                world,
                                x,
                                y % 2032, // each world is 2032 blocks high
                                z
                        )
                );

                sender.sendMessage(PREFIX + "Teletransportado a §a" + x + ", " + y + ", " + z + "§f.");
            } catch (NumberFormatException e) {
                sender.sendMessage(PREFIX + "Uso: /tp <jugador> | <x> <y> <z> | <jugador> <jugador> | <jugador> <x> <y> <z>");
            }
            return true;
        } else if (args.length == 4) {

            if (!canTeleportOthers) {
                sender.sendMessage(PREFIX + "No tienes permisos para teletransportar a otros jugadores.");
                return true;
            }

            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(PREFIX + "El jugador no está conectado.");
                return true;
            }

            try {

                double x, y, z;
                if (args[1].equals("~")) {
                    x = target.getLocation().getX();
                } else {
                    x = Double.parseDouble(args[1]);
                }

                if (args[2].equals("~")) {
                    y = target.getLocation().getY();
                } else {
                    y = Double.parseDouble(args[2]);
                }

                World world = plugin.getWorld(y);

                if (args[3].equals("~")) {
                    z = target.getLocation().getZ();
                } else {
                    z = Double.parseDouble(args[3]);
                }

                target.teleport(
                        new Location(
                                world,
                                x,
                                y % 2032, // each world is 2032 blocks high
                                z
                        )
                );

                sender.sendMessage(PREFIX + "Teletransportado a §a" + target.getName() + "§f a §a" + x + ", " + y + ", " + z + "§f.");
            } catch (NumberFormatException e) {
                sender.sendMessage(PREFIX + "Uso: /tp <jugador> | <x> <y> <z> | <jugador> <jugador> | <jugador> <x> <y> <z>");
            }
        }

        return true;
    }
}
