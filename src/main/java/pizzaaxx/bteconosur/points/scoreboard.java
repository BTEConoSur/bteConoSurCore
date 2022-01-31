package pizzaaxx.bteconosur.points;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.country.Country;

import java.util.List;

import static pizzaaxx.bteconosur.ranks.points.getScoreboard;

public class scoreboard implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        List<ServerPlayer> top = getScoreboard(new Country(p.getLocation()));


    }

}
