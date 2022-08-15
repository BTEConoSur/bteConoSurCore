package pizzaaxx.bteconosur.discord.slashCommands.link;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import pizzaaxx.bteconosur.methods.CodeGenerator;
import pizzaaxx.bteconosur.ServerPlayer.Managers.DiscordManager;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static pizzaaxx.bteconosur.discord.slashCommands.link.LinkUnlinkCommand.discordToMinecraft;

public class LinkUnlinkMinecraftCommand implements CommandExecutor {

    private final JDA bot;
    private final Plugin plugin;

    public LinkUnlinkMinecraftCommand(JDA bot, Plugin plugin) {
        this.bot = bot;
        this.plugin = plugin;
    }

    public static Map<String, UUID> minecraftToDiscord = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            Player p = (Player) sender;

            String prefix = "§f[§bLINK§f] §7>>§r ";


            if (command.getName().equals("link")) {

                if (args.length == 0) {
                    String code = CodeGenerator.generateCode(6, minecraftToDiscord.keySet());

                    minecraftToDiscord.put(code, p.getUniqueId());

                    p.sendMessage(prefix + "Tu código es §b" + code + "§f. Usa §a/link [código] §fen Discord para terminar de conectar tus cuentas.");

                    BukkitRunnable runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            minecraftToDiscord.put(code, null);
                        }
                    };

                    runnable.runTaskLaterAsynchronously(plugin, 12000);


                } else {

                    String code = args[0];

                    if (code.matches("[a-z]{6}")) {

                        if (discordToMinecraft.containsKey(code)) {

                            ServerPlayer s = new ServerPlayer(p);
                            User target = bot.retrieveUserById(discordToMinecraft.get(code)).complete();
                            s.getDiscordManager().connect(target, plugin);

                            p.sendMessage(prefix + "Se ha conectado exitosamente tu cuenta a la cuenta de Discord §b" + target.getName() + "#" + target.getDiscriminator() + "§f.");

                            discordToMinecraft.remove(code);

                        } else {
                            p.sendMessage(prefix + "El código introducido no existe.");
                        }

                    } else {
                        p.sendMessage(prefix + "El código introducido es inválido.");
                    }
                }

            } else if (command.getName().equals("unlink")) {

                DiscordManager manager = new ServerPlayer(p).getDiscordManager();

                if (manager.isLinked()) {
                    manager.disconnect(plugin);
                    p.sendMessage(prefix + "Se ha desconectado tu cuenta exitosamente.");
                } else {
                    p.sendMessage(prefix + "Tu cuenta no esta conectada a ninguna cuenta de Discord.");
                }

            }
        }


        return true;
    }
}
