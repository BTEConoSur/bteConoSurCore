package pizzaaxx.bteconosur.country;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.server.player.ServerPlayer;

public class CountryPlayer implements Comparable<CountryPlayer> {

    private final int points;
    private final ServerPlayer player;

    public CountryPlayer(ServerPlayer s, OldCountry country) {
        this.player = s;
        this.points = s.getPointsManager().getPoints(country);
    }

    public int getPoints() {
        return points;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    @Override
    public int compareTo(@NotNull CountryPlayer o) {
        return Integer.compare(o.getPoints(), getPoints());
    }
}
