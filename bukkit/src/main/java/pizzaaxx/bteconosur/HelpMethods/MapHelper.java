package pizzaaxx.bteconosur.HelpMethods;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MapHelper {

    public static <K, V> @Nullable V getSingleValue(@NotNull Map<K, V> map) {
        for (K key : map.keySet()) {
            return map.get(key);
        }
        return null;
    }

    public static <K, V extends Comparable<? super V>> @NotNull LinkedHashMap<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

}
