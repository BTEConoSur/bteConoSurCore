package pizzaaxx.bteconosur;

import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.projects.Project;
import pizzaaxx.bteconosur.yaml.Configuration;

import java.util.HashMap;
import java.util.Map;

import static pizzaaxx.bteconosur.discord.Bot.conoSurBot;

public class Config implements CommandExecutor {

    public static int maxProjectsPerPlayer;
    public static int maxProjectPoints;
    public static int easyPoints;
    public static Map<Project.Difficulty, Integer> points = new HashMap<>();
    public static int mediumPoints;
    public static int hardPoints;
    public static int maxProjectMembers;

    public static int maxPlayers;

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

    public static Map<String, String> groupsPrefixes = new HashMap<>();

    public Config(Configuration configuration) {
        this.configuration = configuration;
        reload();
    }

    public void reload() {
        maxProjectsPerPlayer = configuration.getInt("max-projects-per-player");
        maxProjectPoints = configuration.getInt("max-project-points");
        maxProjectMembers = configuration.getInt("max-members-per-project");
        ConfigurationSection pointsSection = configuration.getConfigurationSection("points");
        points.put(Project.Difficulty.FACIL, pointsSection.getInt("facil"));
        points.put(Project.Difficulty.INTERMEDIO, pointsSection.getInt("intermedio"));
        points.put(Project.Difficulty.DIFICIL, pointsSection.getInt("dificil"));

        ConfigurationSection requestSection = configuration.getConfigurationSection("request");
        requestsAr = conoSurBot.getTextChannelById(requestSection.getString("ar"));
        requestsBo = conoSurBot.getTextChannelById(requestSection.getString("bo"));
        requestsCl = conoSurBot.getTextChannelById(requestSection.getString("cl"));
        requestsPy = conoSurBot.getTextChannelById(requestSection.getString("py"));
        requestsPe = conoSurBot.getTextChannelById(requestSection.getString("pe"));

        ConfigurationSection logsSection = configuration.getConfigurationSection("logs");
        logsAr = conoSurBot.getTextChannelById(logsSection.getString("ar"));
        logsBo = conoSurBot.getTextChannelById(logsSection.getString("bo"));
        logsCl = conoSurBot.getTextChannelById(logsSection.getString("cl"));
        logsPy = conoSurBot.getTextChannelById(logsSection.getString("py"));
        logsPe = conoSurBot.getTextChannelById(logsSection.getString("pe"));

        gateway = conoSurBot.getTextChannelById(configuration.getString("gateway-channel"));

        maxPlayers = configuration.getInt("max-players");

        Configuration colors = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "chat/colors");
        for (String key : colors.getKeys(false)) {
            groupsPrefixes.put(key, "[§" + colors.getString(key) + key.toUpperCase() + "§f]");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, String label, String[] args) {

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
