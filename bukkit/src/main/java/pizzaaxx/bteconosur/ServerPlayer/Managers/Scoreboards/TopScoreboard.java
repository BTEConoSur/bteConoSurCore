package pizzaaxx.bteconosur.ServerPlayer.Managers.Scoreboards;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.Points.PointsContainer;
import pizzaaxx.bteconosur.ServerPlayer.Managers.PointsManager;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;
import pizzaaxx.bteconosur.country.Country;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TopScoreboard implements ScoreboardType {

    private final String title;
    private final List<String> lines = new ArrayList<>();

    public TopScoreboard(Location loc, @NotNull BteConoSur plugin) {

        Country country = plugin.getCountryManager().get(loc);

        PointsContainer container;

        if (country != null && country.allowsProjects()) {

            title = "§a§nTop de " + country.getDisplayName();

            container = country;

        } else {

            title = "§a§nTop global";

            container = plugin;

        }

        int i = 1;
        for (UUID uuid : container.getMaxPoints()) {

            ServerPlayer s = plugin.getPlayerRegistry().get(uuid);

            int points = s.getPointsManager().getPoints(container);

            ChatColor color;

            switch (PointsManager.BuilderRank.getFrom(points)) {

                case AVANZADO:
                    color = ChatColor.DARK_BLUE;
                    break;
                case VETERANO:
                    color = ChatColor.YELLOW;
                    break;
                case MAESTRO:
                    color = ChatColor.GOLD;
                    break;
                default:
                    color = ChatColor.BLUE;
                    break;
            }

            lines.add(i + ". §a" + points + " §7- " + color + s.getName());
            i++;

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
