package pizzaaxx.bteconosur.ServerPlayer.Managers.Scoreboards;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.country.Country;

import java.util.ArrayList;
import java.util.List;

public class ServerScoreboard implements ScoreboardType {

    private final String title;
    private final List<String> lines = new ArrayList<>();

    public ServerScoreboard(@NotNull BteConoSur plugin) {

        title = "§3§nBTE Cono Sur";
        lines.add("§aIP: §fbteconosur.com");
        lines.add(" ");

        lines.add("§aJugadores: " + Bukkit.getOnlinePlayers().size() + "/20");

        for (Country country : plugin.getCountryManager().getAllCountries()) {
            lines.add("§a" + country.getDisplayName() + ": §f" + country.getPlayersInside());
        }

    }

    @Override
    public List<String> getLines() {
        return lines;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
