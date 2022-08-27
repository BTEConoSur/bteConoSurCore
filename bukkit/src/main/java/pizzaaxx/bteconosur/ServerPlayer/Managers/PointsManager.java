package pizzaaxx.bteconosur.ServerPlayer.Managers;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.Points.PointsContainer;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;
import pizzaaxx.bteconosur.country.Country;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PointsManager {

    private final Map<PointsContainer, Integer> countriesPoints = new HashMap<>();

    public static String pointsPrefix = "§f[§9PUNTOS§f] §7>>§r ";

    private final ServerPlayer serverPlayer;
    private final DataManager data;
    private final BteConoSur plugin;

    public PointsManager(@NotNull ServerPlayer s) {
        data = s.getDataManager();

        serverPlayer = s;
        this.plugin = s.getPlugin();

        if (data.contains("points")) {
            ConfigurationSection pointsSection = data.getConfigurationSection("points");
            for (String key : pointsSection.getKeys(false)) {

                countriesPoints.put(plugin.getCountryManager().get(key), pointsSection.getInt(key));
            }
        }
    }

    public ServerPlayer getServerPlayer() {
        return serverPlayer;
    }

    public int getPoints(@NotNull PointsContainer container) {
        if (container instanceof BteConoSur) {
            return getTotalPoints();
        }
        return countriesPoints.getOrDefault(container, 0);
    }

    public int getTotalPoints() {
        int total = 0;
        for (Integer points : countriesPoints.values()) {
            total += points;
        }
        return total;
    }

    public void setPoints(@NotNull Country country, int points) {
        int old = countriesPoints.getOrDefault(country, 0);
        if (old != points) {
            countriesPoints.put(country, points);
            country.checkMaxPoints(this.serverPlayer.getId());
            plugin.checkMaxPoints(this.serverPlayer.getId());
            Map<PointsContainer, Integer> map = new HashMap<>(countriesPoints);
            data.set("points", map);
            data.save();
            int diff = Math.abs(points - old);
            country.getProjectsLogsChannel().sendMessage(":chart_with_" + (diff > 0 ? "up" : "down") + "wards_trend: Se han " + (diff > 0 ? "añadido" : "quitado") + " `" + diff + "` puntos a **" + serverPlayer.getName() + "**. Total: `" + points + "`.").queue();
        }

        serverPlayer.getGroupsManager().checkGroups();

    }

    public void addPoints(Country country, int points) {
        int newAmount = getPoints(country) + points;
        setPoints(country, newAmount);
    }

    public Map.Entry<PointsContainer, Integer> getMaxPoints() {
        Map.Entry<PointsContainer, Integer> max = null;
        for (Map.Entry<PointsContainer, Integer> entry : countriesPoints.entrySet()) {
            if (max == null || entry.getValue().compareTo(max.getValue()) > 0) {
                max = entry;
            }
        }
        return max;
    }

    public LinkedHashMap<Country, Integer> getSorted() {
        LinkedHashMap<Country, Integer> sorted = new LinkedHashMap<>();
        countriesPoints.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sorted.put((Country) x.getKey(), x.getValue()));
        return sorted;
    }

    public enum BuilderRank {
        BUILDER, AVANZADO, VETERANO, MAESTRO;

        public static BuilderRank getFrom(int points) {
            if (points >= 1000) {
                return MAESTRO;
            } else if (points >= 500) {
                return AVANZADO;
            } else if (points >= 150) {
                return VETERANO;
            }
            return BUILDER;
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        public String getAsPrefix() {
            switch (this) {
                case AVANZADO:
                    return "§f[§1AVANZADO§f]";
                case VETERANO:
                    return "§f[§eVETERANO§f]";
                case MAESTRO:
                    return "§f[§6MAESTRO§f]";
                default:
                    return "§f[§9BUILDER§f]";
            }
        }
    }

}
