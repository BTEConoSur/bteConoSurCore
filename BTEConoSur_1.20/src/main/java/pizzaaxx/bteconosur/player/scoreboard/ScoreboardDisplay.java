package pizzaaxx.bteconosur.player.scoreboard;

import net.kyori.adventure.text.Component;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import java.util.List;

public interface ScoreboardDisplay {

    Component getTitle();
    List<Component> getLines();
    ScoreboardDisplayProvider getProvider();
    boolean isSavable();


}
