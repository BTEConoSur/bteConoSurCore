package pizzaaxx.bteconosur.serverPlayer;

import org.bukkit.configuration.ConfigurationSection;
import pizzaaxx.bteconosur.country.Country;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static pizzaaxx.bteconosur.BteConoSur.discord;

public class PointsManager {

    private final TreeMap<Country, Integer> countriesPoints = new TreeMap<>();
    private final ServerPlayer serverPlayer;
    private final DataManager data;

    public PointsManager(ServerPlayer s) {
        data = s.getDataManager();

        serverPlayer = s;

        if (data.contains("points")) {
            ConfigurationSection pointsSection = data.getConfigurationSection("points");
            for (String key : pointsSection.getKeys(false)) {
                countriesPoints.put(new Country(key), pointsSection.getInt(key));
            }
        }
    }

    public int getPoints(Country country) {
        return countriesPoints.getOrDefault(country, 0);
    }

    public void setPoints(Country country, int points) {
        int old = countriesPoints.get(country);
        if (old != points) {
            countriesPoints.put(country, points);
            Map<String, Integer> map = new HashMap<>();
            countriesPoints.forEach((key, value) -> map.put(key.getCountry(), value));
            data.set("points", map);
            data.save();
            int diff = Math.abs(points - old);
            discord.log(country, ":chart_with_" + (diff > 0 ? "up" : "down") + "wards_trend: Se han " + (diff > 0 ? "añadido" : "quitado") + " `" + diff + "` puntos a **" + serverPlayer.getName() + "**. Total: `" + points + "`.");
        }
    }

    public int addPoints(Country country, int points) {
        int newAmount = getPoints(country) + points;
        setPoints(country, newAmount);
        return newAmount;
    }

    public int removePoints(Country country, int points) {
        int newAmount = getPoints(country) - points;
        setPoints(country, newAmount);
        return newAmount;
    }

    public Map.Entry<Country, Integer> getMaxPoints() {
        Map.Entry<Country, Integer> max = null;
        for (Map.Entry<Country, Integer> entry : countriesPoints.entrySet()) {
            if (max == null || entry.getValue().compareTo(max.getValue()) > 0) {
                max = entry;
            }
        }
        return max;
    }

    public TreeMap<Country, Integer> getSorted() {
        return countriesPoints;
    }
}
