package pizzaaxx.bteconosur.Scoreboard;

import java.util.Collections;
import java.util.List;

public class NotFoundDisplay implements ScoreboardDisplay {

    private final String type;
    private final String title;
    private final String error;

    public NotFoundDisplay(String type, String title, String error) {
        this.type = type;
        this.title = title;
        this.error = error;
    }

    @Override
    public String getScoreboardTitle() {
        return title;
    }

    @Override
    public List<String> getScoreboardLines() {
        return Collections.singletonList(error);
    }

    @Override
    public String getScoreboardType() {
        return type;
    }
}
