package pizzaaxx.bteconosur.playerData;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static pizzaaxx.bteconosur.bteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.yaml.YamlManager.getYamlData;
import static pizzaaxx.bteconosur.yaml.YamlManager.writeYaml;

public class PlayerData {

    private final YamlManager data;
    private OfflinePlayer player;

    // CONSTRUCTOR
    public PlayerData(OfflinePlayer player) {
        this.player = player;
        UUID uuid = this.player.getUniqueId();
        this.data = new YamlManager(pluginFolder, "playerData/" + uuid.toString() + ".yml");
    }

    // SET PLAYER

    public void setPlayer(OfflinePlayer p) {
        this.player = p;
    }

    // GET DATA

    public Object getData(String key) {
        return data.getValue(key);
    }

    public Map<String, Object> getAllData() {
        return this.data.getAllData();
    }

    // SET DATA

    public void setData(String key, Object value) {
        data.setValue(key, value);
    }

    // DELETE DATA

    public void deleteData(String key) {
        data.deleteValue(key);
    }

    // LISTS

    public Boolean addToList(String key, Object value, Boolean duplicate) {
        return data.addToList(key, value, duplicate);
    }

    public Boolean removeFromList(String key, Object value) {
        return data.removeFromList(key, value);
    }

    public List<?> getList(String key) {
        return data.getList(key);
    }

    // SAVE PLAYER DATA

    public void save(){
        this.data.write();
    }
}
