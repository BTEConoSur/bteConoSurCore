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

        return true;
    }
}
