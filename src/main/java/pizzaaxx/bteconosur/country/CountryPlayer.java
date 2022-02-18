package pizzaaxx.bteconosur.country;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.serverPlayer.ServerPlayer;

public class CountryPlayer implements Comparable<CountryPlayer> {

    private int points;
    private ServerPlayer player;
    private Country country;

    public CountryPlayer(ServerPlayer s, Country country) {
        this.player = s;
        this.points = s.getPoints(country);
        this.country = country;
    }

    public int getPoints() {
        return points;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public Country getCountry() {
        return country;
    }

    @Override
    public int compareTo(@NotNull CountryPlayer o) {
        return Integer.compare(o.getPoints(), getPoints());
    }
}
