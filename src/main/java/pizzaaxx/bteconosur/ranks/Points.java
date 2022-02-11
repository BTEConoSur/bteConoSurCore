package pizzaaxx.bteconosur.ranks;

import org.bukkit.Bukkit;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.yaml.YamlManager;

import java.util.*;

import static pizzaaxx.bteconosur.BteConoSur.pluginFolder;

public class Points {
    public static List<ServerPlayer> getScoreboard(Country country) {
        List<ServerPlayer> scoreboard = new ArrayList<>();

        YamlManager yaml = new YamlManager(pluginFolder, "points/max.yml");
        for (String uuid : (List<String>) yaml.getList(country.getAbbreviation() + "_max")) {
            scoreboard.add(new ServerPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid))));
        }

        return scoreboard;
    }

    public static void checkTop(ServerPlayer s, Country country) {
        Map<ServerPlayer, Integer> top = new HashMap<>();
        for (ServerPlayer m : getScoreboard(country)) {
            top.put(m, m.getPoints(country));
        }

        top.put(s, s.getPoints(country));

        TreeMap<ServerPlayer, Integer> orderedTop = new TreeMap<>(top);

        List<String> newUUIDs = new ArrayList<>();
        int i = 0;
        for (Map.Entry<ServerPlayer, Integer> entry : orderedTop.entrySet()) {
            newUUIDs.add(entry.getKey().getPlayer().getUniqueId().toString());
            i++;
            if (i > 10) {
                break;
            }
        }

        YamlManager yaml = new YamlManager(pluginFolder, "points/max.yml");
        yaml.setValue(country.getAbbreviation() + "_max", newUUIDs);
        yaml.write();
    }
}
