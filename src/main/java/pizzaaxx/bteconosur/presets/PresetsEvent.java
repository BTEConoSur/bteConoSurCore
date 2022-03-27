package pizzaaxx.bteconosur.presets;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pizzaaxx.bteconosur.serverPlayer.DataManager;
import pizzaaxx.bteconosur.serverPlayer.ServerPlayer;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.util.Map;

import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;

public class PresetsEvent implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage();
        if (message.contains("$")) {
            DataManager data = new ServerPlayer(e.getPlayer()).getDataManager();
            ConfigurationSection presets = data.getConfigurationSection("presets");
            for (String word : message.split(" ")) {
                if (word.startsWith("$")) {
                    String preset = word.replace("$", "");
                    if (preset.matches("[a-zA-z0-9_]{1,32}") && presets.contains(preset)) {
                        message = message.replace(word, presets.getString(preset));
                    }
                }
            }
        }
        e.setMessage(message);
    }
}
