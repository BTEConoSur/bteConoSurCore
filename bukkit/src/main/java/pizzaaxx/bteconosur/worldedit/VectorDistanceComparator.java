package pizzaaxx.bteconosur.worldedit;

import com.sk89q.worldedit.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class VectorDistanceComparator implements Comparator<Vector> {

    private final Vector origin;

    public VectorDistanceComparator(Vector origin) {
        this.origin = origin;
    }

    @Override
    public int compare(@NotNull Vector v1, @NotNull Vector v2) {

        return Double.compare(v1.distance(origin), v2.distance(origin));

    }
}
