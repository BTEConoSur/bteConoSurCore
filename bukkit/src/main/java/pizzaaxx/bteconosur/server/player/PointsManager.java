package pizzaaxx.bteconosur.server.player;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.worldguard.WorldGuardProvider;
import pizzaaxx.bteconosur.configuration.Configuration;

import java.util.*;
import java.util.stream.Collectors;

import static pizzaaxx.bteconosur.country.OldCountry.allCountries;
import static pizzaaxx.bteconosur.server.player.ScoreboardManager.ScoreboardType.TOP;

public class PointsManager {

    private static class PointsGlobalComparator implements Comparator<ServerPlayer> {

        @Override
        public int compare(ServerPlayer s1, ServerPlayer s2) {
            int total1 = 0, total2 = 0;
            for (OldCountry country : allCountries) {
                total1 += s1.getPointsManager().getPoints(country);
                total2 += s2.getPointsManager().getPoints(country);
            }
            return Integer.compare(total1, total2);
        }
    }

    private static class PointsCountryComparator implements Comparator<ServerPlayer> {

        private final OldCountry country;

        public PointsCountryComparator(OldCountry country) {
            this.country = country;
        }

        @Override
        public int compare(ServerPlayer s1, ServerPlayer s2) {
            return Integer.compare(s1.getPointsManager().getPoints(country), s2.getPointsManager().getPoints(country));
        }
    }

    private final Map<String, Integer> countriesPoints = new HashMap<>();

    public static String pointsPrefix = "§f[§9PUNTOS§f] §7>>§r ";

    private final Configuration maxConfig = new Configuration(Bukkit.getPluginManager().getPlugin("bteConoSur"), "points/max");

    private final ServerPlayer serverPlayer;
    private final DataManager data;

    public PointsManager(@NotNull ServerPlayer s) {
        data = s.getDataManager();

        serverPlayer = s;

        if (data.contains("points")) {
            ConfigurationSection pointsSection = data.getConfigurationSection("points");
            for (String key : pointsSection.getKeys(false)) {

                countriesPoints.put(key, pointsSection.getInt(key));
            }
        }
    }

    public ServerPlayer getServerPlayer() {
        return serverPlayer;
    }

    public int getPoints(@NotNull OldCountry country) {

        if (country.getName().equals("global")) {
            int total = 0;
            for (OldCountry c : allCountries) {
                total += getPoints(c);
            }
            return total;
        }

        return countriesPoints.getOrDefault(country.getName(), 0);
    }

    public void setPoints(@NotNull OldCountry country, int points) {
        int old = countriesPoints.get(country.getName());
        if (old != points) {
            countriesPoints.put(country.getName(), points);
            Map<String, Integer> map = new HashMap<>(countriesPoints);
            data.set("points", map);
            data.save();
            int diff = Math.abs(points - old);
            country.getLogs().sendMessage((diff > 0 ? ":up" : ":down") + "wards_trend: Se han " + (diff > 0 ? "añadido" : "quitado") + " `" + diff + "` puntos a **" + serverPlayer.getName() + "**. Total: `" + points + "`.").queue();
        }

        serverPlayer.getGroupsManager().checkGroups();

        checkTop(country);

    }

    public void addPoints(OldCountry country, int points) {
        int newAmount = getPoints(country) + points;
        setPoints(country, newAmount);
    }

    public int removePoints(OldCountry country, int points) {
        int newAmount = getPoints(country) - points;
        setPoints(country, newAmount);
        return newAmount;
    }

    public Map.Entry<String, Integer> getMaxPoints() {
        Map.Entry<String, Integer> max = null;
        for (Map.Entry<String, Integer> entry : countriesPoints.entrySet()) {
            if (max == null || entry.getValue().compareTo(max.getValue()) > 0) {
                max = entry;
            }
        }
        return max;
    }

    public LinkedHashMap<OldCountry, Integer> getSorted() {
        LinkedHashMap<OldCountry, Integer> sorted = new LinkedHashMap<>();
        countriesPoints.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sorted.put(new OldCountry(x.getKey()), x.getValue()));
        return sorted;
    }

    public void checkTop(OldCountry country) {
        List<String> maxStrings = maxConfig.getStringList(country.getName());

        List<UUID> maxUUIDs = new ArrayList<>();
        for (String string : maxStrings) {
            maxUUIDs.add(UUID.fromString(string));
        }

        // add this uuid if missing
        if (!maxUUIDs.contains(serverPlayer.getId())) {
            maxUUIDs.add(serverPlayer.getId());
        }

        List<ServerPlayer> players = new ArrayList<>();
        for (UUID uuid : maxUUIDs) {
            players.add(new ServerPlayer(uuid));
        }
        players.sort(new PointsCountryComparator(country));

        Collections.reverse(players);

        maxConfig.set(country.getName(), players.subList(0, Math.min(10, players.size())).stream().map(ServerPlayer::getId).map(UUID::toString).collect(Collectors.toList()));
        maxConfig.save();

        checkGlobalTop();

        for (Player player : WorldGuardProvider.getPlayersInCountry(country)) {
            ServerPlayer s = new ServerPlayer(player);
            if (s.getScoreboardManager().getType() == TOP) {
                s.getScoreboardManager().update();
            }
        }
    }

    private void checkGlobalTop() {
        List<String> maxStrings = maxConfig.getStringList("global");

        List<UUID> maxUUIDs = new ArrayList<>();
        for (String string : maxStrings) {
            maxUUIDs.add(UUID.fromString(string));
        }

        // add this uuid if missing
        if (!maxUUIDs.contains(serverPlayer.getId())) {
            maxUUIDs.add(serverPlayer.getId());
        }

        List<ServerPlayer> players = new ArrayList<>();
        for (UUID uuid : maxUUIDs) {
            players.add(new ServerPlayer(uuid));
        }

        players.sort(new PointsGlobalComparator());

        Collections.reverse(players);

        maxConfig.set("global", players.subList(0, Math.min(10, players.size())).stream().map(ServerPlayer::getId).map(UUID::toString).collect(Collectors.toList()));
        maxConfig.save();
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
