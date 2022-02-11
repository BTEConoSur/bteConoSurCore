package pizzaaxx.bteconosur.link;

import net.dv8tion.jda.api.entities.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.playerData.PlayerData;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.util.HashMap;
import java.util.Map;

import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.link.LinkDiscord.discordLinks;
import static pizzaaxx.bteconosur.methods.CodeGenerator.generateCode;

public class LinkMinecraft implements CommandExecutor {
    public static Map<String, OfflinePlayer> minecraftLinks = new HashMap<>();
    public static String linkPrefix = "[§9LINK§f] §7>> §f";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser usado por jugadores.");
            return true;
        }

        Player p = (Player) sender;

        if (command.getName().equals("link")) {
            if (args.length == 0) {
                String code = generateCode(6);
                while (minecraftLinks.containsKey(code)) {
                    code = generateCode(6);
                }
                minecraftLinks.put(code, p);

                p.sendMessage(linkPrefix + "Envía §a/link " + code + " §f a §aBTE Cono Sur#4466 §fpara conectar tu cuenta.");
            } else {
                if (args[0].matches("[a-z]{6}")) {
                    if (discordLinks.containsKey(args[0])) {
                        User user = discordLinks.get(args[0]);

                        PlayerData playerData = new PlayerData(p);
                        Map<String, String> discord = new HashMap<>();
                        discord.put("id", user.getId());
                        discord.put("name", user.getName());
                        discord.put("discriminator", user.getDiscriminator());
                        playerData.setData("discord", discord);
                        playerData.save();

                        Map<String, Object> linkData = YamlManager.getYamlData(pluginFolder, "link/links.yml");
                        linkData.put(user.getId(), p.getUniqueId());
                        YamlManager.writeYaml(pluginFolder, "link/links.yml", linkData);

                        p.sendMessage(linkPrefix + "Se ha conectado exitosamente la cuenta de Minecraft §a" + p.getName() + "§f a la cuenta de Discord §a" + user.getName() + "#" + user.getDiscriminator() + "§f.");
                        p.sendMessage(linkPrefix + "Desde ahora recibirás las notificaciones del servidor por medio de Discord.");
                    } else {
                        p.sendMessage(linkPrefix + "El código introducido no existe.");
                    }
                } else {
                    p.sendMessage(linkPrefix + "Introduce un código válido.");
                }
            }
        }

        if (command.getName().equals("unlink")) {
            Map<String, Object> linkData = YamlManager.getYamlData(pluginFolder, "link/links.yml");
            if (linkData.containsValue(p.getUniqueId())) {
                PlayerData playerData = new PlayerData(p);

                String userId = (String) playerData.getData("discord");
                linkData.remove(userId);
                YamlManager.writeYaml(pluginFolder, "links/links.yml", linkData);

                playerData.deleteData("discord");
                playerData.save();

                p.sendMessage(linkPrefix + "Se ha desconectado exitosamente tu cuenta de Minecraft de tu cuenta de Discord.");
            } else {
                p.sendMessage(linkPrefix + "Tu cuenta de Minecraft no está conectada a ninguna cuenta de Discord.");
            }
        }
        return true;
    }
}
