package pizzaaxx.bteconosur.testing;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.awt.*;
import java.util.Map;

import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;

public class Testing implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String name = args[0];
        YamlManager yaml = new YamlManager(pluginFolder, "testing/embeds.yml");
        EmbedBuilder embed = new EmbedBuilder();
        String color = (String) yaml.getValue(name + ".color");
        embed.setColor(new Color(Integer.parseInt(color.split(", ")[0]), Integer.parseInt(color.split(", ")[1]),  Integer.parseInt(color.split(", ")[2])));
        if (yaml.getValue(name + ".title") != null) {
            embed.setTitle((String) yaml.getValue(name + ".title"));
        }
        if (yaml.getValue(name + ".description") != null) {
            embed.setDescription(((String) yaml.getValue(name + ".description")).replace("~~", "\n"));
        }
        if (yaml.getValue(name + ".image") != null) {
            embed.setImage((String) yaml.getValue(name + ".image"));
        }
        if (yaml.getValue(name + ".thumbnail") != null) {
            embed.setThumbnail((String) yaml.getValue(name + ".thumbnail"));
        }
        if (yaml.getValue(name + ".fields") != null) {
            for (Map.Entry<String, String> entry : ((Map<String, String>) yaml.getValue(name + ".fields")).entrySet()) {
                embed.addField(entry.getKey(), entry.getValue().replace("$i ", "").replace("~~", "\n"), (entry.getValue().startsWith("$i")));
            }
        }

        return true;
    }
}
