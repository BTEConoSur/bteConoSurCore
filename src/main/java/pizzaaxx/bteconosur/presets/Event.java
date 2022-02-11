package pizzaaxx.bteconosur.presets;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.util.Map;

import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;

public class Event implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage();
        if (message.contains("$")) {
            Map<String, String> presets = (Map<String, String>) new YamlManager(pluginFolder, "playerData/" + e.getPlayer().getUniqueId().toString() + ".yml").getValue("presets");
            for (String word : message.split(" ")) {
                if (word.startsWith("$")) {
                    String preset = word.replace("$", "");
                    if (preset.matches("[a-zA-z0-9_]{1,32}") && presets.containsKey(preset)) {
                        message = message.replace(word, presets.get(preset));
                    }
                }
            }
        }
        e.setMessage(message);
    }
}
