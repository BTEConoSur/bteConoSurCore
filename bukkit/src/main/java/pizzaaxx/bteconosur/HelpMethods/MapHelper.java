package pizzaaxx.bteconosur.HelpMethods;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MapHelper {

    public static <K, T> @Nullable T getSingleValue(@NotNull Map<K, T> map) {
        for (K key : map.keySet()) {
            return map.get(key);
        }
        return null;
    }

}
