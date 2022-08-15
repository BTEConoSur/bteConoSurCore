package pizzaaxx.bteconosur.Points;

import java.util.List;
import java.util.UUID;

public interface PointsContainer {

    void checkMaxPoints(UUID uuid);

    List<UUID> getMaxPoints();

}
