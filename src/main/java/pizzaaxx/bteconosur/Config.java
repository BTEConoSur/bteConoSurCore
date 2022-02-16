package pizzaaxx.bteconosur;

import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import pizzaaxx.bteconosur.yaml.Configuration;

import static pizzaaxx.bteconosur.discord.Bot.conoSurBot;

public class Config implements CommandExecutor {

    public static int maxProjectsPerPlayer;
    public static int maxProjectPoints;
    public static int easyPoints;
    public static int mediumPoints;
    public static int hardPoints;
    public static int maxProjectMembers;

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

    private final Configuration configuration;

    public Config(Configuration configuration) {
        this.configuration = configuration;
        reload();
    }

    public void reload() {
        maxProjectsPerPlayer = configuration.getInt("max-project-per-player");
        maxProjectPoints = configuration.getInt("max-project-per-points");
        easyPoints = configuration.getInt("easy-points");
        mediumPoints = configuration.getInt("medium-points");
        hardPoints = configuration.getInt("hard-points");

        ConfigurationSection requestSection = configuration.getConfigurationSection("request");
        requestsAr = conoSurBot.getTextChannelById(requestSection.getLong("ar"));
        requestsBo = conoSurBot.getTextChannelById(requestSection.getLong("bo"));
        requestsCl = conoSurBot.getTextChannelById(requestSection.getLong("cl"));
        requestsPy = conoSurBot.getTextChannelById(requestSection.getLong("py"));
        requestsPe = conoSurBot.getTextChannelById(requestSection.getLong("pe"));

        ConfigurationSection logsSection = configuration.getConfigurationSection("logs");
        logsAr = conoSurBot.getTextChannelById(logsSection.getLong("ar"));
        logsBo = conoSurBot.getTextChannelById(logsSection.getLong("bo"));
        logsCl = conoSurBot.getTextChannelById(logsSection.getLong("cl"));
        logsPy = conoSurBot.getTextChannelById(logsSection.getLong("py"));
        logsPe = conoSurBot.getTextChannelById(logsSection.getLong("pe"));

        gateway = conoSurBot.getTextChannelById(configuration.getLong("gateway-channel"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equals("btecs_reload")) {
            if (sender.hasPermission("bteconosur.reload")) {
                configuration.reload();
                reload();
                sender.sendMessage("§aConfiguración recargada.");
            } else {
                sender.sendMessage("§cNo puedes hacer esto.");
            }
        }
        return true;
    }
}
