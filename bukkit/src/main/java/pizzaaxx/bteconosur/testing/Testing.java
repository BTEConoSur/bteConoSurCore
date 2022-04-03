package pizzaaxx.bteconosur.testing;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import pizzaaxx.bteconosur.yaml.Configuration;

import java.awt.*;

public class Testing implements CommandExecutor {

    private final Plugin plugin;

    public Testing(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String name = args[0];
        Configuration yaml = new Configuration(plugin, "testing/embeds");
        EmbedBuilder embed = new EmbedBuilder();
        String color = yaml.getString(name + ".color");
        embed.setColor(new Color(Integer.parseInt(color.split(", ")[0]), Integer.parseInt(color.split(", ")[1]),  Integer.parseInt(color.split(", ")[2])));
        if (yaml.contains(name + ".title")) {
            embed.setTitle(yaml.getString(name + ".title"));
        }
        if (yaml.contains(name + ".description")) {
            embed.setDescription(yaml.getString(name + ".description").replace("~~", "\n"));
        }
        if (yaml.contains(name + ".image")) {
            embed.setImage(yaml.getString(name + ".image"));
        }
        if (yaml.contains(name + ".thumbnail")) {
            embed.setThumbnail(yaml.getString(name + ".thumbnail"));
        }
        if (yaml.contains(name + ".fields")) {
            ConfigurationSection section = yaml.getConfigurationSection(name + ".fields");
            for (String key : section.getKeys(false)) {
                embed.addField(key, section.getString(key).replace("$i ", "").replace("~~", "\n"), (section.getString(key).startsWith("$i")));
            }
        }

        return true;
    }
}
