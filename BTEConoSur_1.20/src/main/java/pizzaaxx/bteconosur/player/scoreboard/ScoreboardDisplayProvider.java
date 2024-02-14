package pizzaaxx.bteconosur.player.scoreboard;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ScoreboardDisplayProvider {

    Map<String, ScoreboardDisplayProvider> SCOREBOARD_PROVIDERS = new HashMap<>();
    List<String> AUTO_PROVIDERS = new ArrayList<>();

    static ScoreboardDisplayProvider getNext(String type) {
        int index = AUTO_PROVIDERS.indexOf(type);
        String next = AUTO_PROVIDERS.get(index + 1 >= AUTO_PROVIDERS.size() ? 0 : index + 1);
        return SCOREBOARD_PROVIDERS.get(next);
    }

    ScoreboardDisplay getDisplay(Player player);
    String getIdentifier();

}
