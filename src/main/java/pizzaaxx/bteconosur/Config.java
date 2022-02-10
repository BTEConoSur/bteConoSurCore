package pizzaaxx.bteconosur;

import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.util.Map;

import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.discord.bot.conoSurBot;

public class Config implements CommandExecutor {

    public static Integer maxProjectsPerPlayer;
    public static Integer maxProjectPoints;
    public static Integer easyPoints;
    public static Integer mediumPoints;
    public static Integer hardPoints;
    public static Integer maxProjectMembers;
    public static TextChannel requestsAr;
    public static TextChannel requestsBo;
    public static TextChannel requestsCl;
    public static TextChannel requestsPy;
    public static TextChannel requestsPe;
    public static TextChannel requestsUy;
    public static TextChannel logsAr;
    public static TextChannel logsBo;
    public static TextChannel logsCl;
    public static TextChannel logsPy;
    public static TextChannel logsPe;
    public static TextChannel logsUy;
    public static TextChannel gateway;


    public static void reload() {
        Map<String, Object> data = YamlManager.getYamlData(pluginFolder, "config.yml");

        if (!(data.containsKey("maxProjectsPerPlayer"))) {
            data.put("maxProjectsPerPlayer", 10);
        }
        maxProjectsPerPlayer = (Integer) data.get("maxProjectsPerPlayer");

        if (!(data.containsKey("maxProjectPoints"))) {
            data.put("maxProjectPoints", 15);
        }
        maxProjectPoints = (Integer) data.get("maxProjectPoints");

        if (!(data.containsKey("easyPoints"))) {
            data.put("easyPoints", 15);
        }
        easyPoints = (Integer) data.get("easyPoints");

        if (!(data.containsKey("mediumPoints"))) {
            data.put("mediumPoints", 50);
        }
        mediumPoints = (Integer) data.get("mediumPoints");

        if (!(data.containsKey("hardPoints"))) {
            data.put("hardPoints", 100);
        }
        hardPoints = (Integer) data.get("hardPoints");

        if (!(data.containsKey("maxProjectMembers"))) {
            data.put("maxProjectMembers", 15);
        }
        maxProjectMembers = (Integer) data.get("maxProjectMembers");

        if (!(data.containsKey("requestsAr"))) {
            data.put("requestsAr", "932074847016718426");
        }
        requestsAr = conoSurBot.getTextChannelById((String) data.get("requestsAr"));

        if (!(data.containsKey("requestsBo"))) {
            data.put("requestsBo", "932074847016718426");
        }
        requestsBo = conoSurBot.getTextChannelById((String) data.get("requestsBo"));

        if (!(data.containsKey("requestsCl"))) {
            data.put("requestsCl", "932074847016718426");
        }
        requestsCl = conoSurBot.getTextChannelById((String) data.get("requestsCl"));

        if (!(data.containsKey("requestsPy"))) {
            data.put("requestsPy", "932074847016718426");
        }
        requestsPy = conoSurBot.getTextChannelById((String) data.get("requestsPy"));

        if (!(data.containsKey("requestsPe"))) {
            data.put("requestsPe", "932074847016718426");
        }
        requestsPe = conoSurBot.getTextChannelById((String) data.get("requestsPe"));

        if (!(data.containsKey("requestsUy"))) {
            data.put("requestsUy", "932074847016718426");
        }
        requestsUy = conoSurBot.getTextChannelById((String) data.get("requestsUy"));

        if (!(data.containsKey("logsAr"))) {
            data.put("logsAr", "932074832164712488");
        }
        logsAr = conoSurBot.getTextChannelById((String) data.get("logsAr"));

        if (!(data.containsKey("logsBo"))) {
            data.put("logsBo", "932074832164712488");
        }
        logsBo = conoSurBot.getTextChannelById((String) data.get("logsBo"));

        if (!(data.containsKey("logsCl"))) {
            data.put("logsCl", "932074832164712488");
        }
        logsCl = conoSurBot.getTextChannelById((String) data.get("logsCl"));

        if (!(data.containsKey("logsPy"))) {
            data.put("logsPy", "932074832164712488");
        }
        logsPy = conoSurBot.getTextChannelById((String) data.get("logsPy"));

        if (!(data.containsKey("logsPe"))) {
            data.put("logsPe", "932074832164712488");
        }
        logsPe = conoSurBot.getTextChannelById((String) data.get("logsPe"));

        if (!(data.containsKey("logsUy"))) {
            data.put("logsUy", "932074832164712488");
        }
        logsUy = conoSurBot.getTextChannelById((String) data.get("logsUy"));

        if (!(data.containsKey("gatewayChannel"))) {
            data.put("gatewayChannel", "932064914405724231");
        }
        gateway = conoSurBot.getTextChannelById((String) data.get("gatewayChannel"));

        YamlManager.writeYaml(pluginFolder, "config.yml", data);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equals("btecs_reload")) {
            if (sender.hasPermission("bteconosur.reload")) {
                reload();
                sender.sendMessage("§aConfiguración recargada.");
            } else {
             sender.sendMessage("§cNo puedes hacer esto.");
            }
        }
        return true;
    }
}
