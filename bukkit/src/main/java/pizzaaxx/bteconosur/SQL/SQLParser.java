package pizzaaxx.bteconosur.SQL;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class SQLParser {

    @Contract(pure = true)
    private static @NotNull String getString(@NotNull Object object, boolean insideJSON) {

        if (object instanceof UUID) {
            // binary(16)
            UUID uuid = (UUID) object;
            return "unhex(replace(\"" + uuid + "\",'-',''))";
        } else if (object instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) object;
            int size = collection.size();

            StringBuilder builder = new StringBuilder((insideJSON?"":"\"") + "[");

            int counter = 1;
            for (Object obj : collection) {
                builder.append(SQLParser.getString(obj, true));
                if (counter < size) {
                    builder.append(",");
                }
                counter++;
            }

            builder.append("]").append(insideJSON ? "" : "\"");
            return builder.toString();
        } else if (object instanceof Map<?,?>) {
            Map<?, ?> map = (Map<?, ?>) object;
            int size = map.size();

            StringBuilder builder = new StringBuilder((insideJSON?"":"\"") + "{");

            int counter = 1;
            for (Map.Entry<?,?> entry : map.entrySet()) {
                builder.append("\"").append(entry.getKey().toString()).append("\": ").append(SQLParser.getString(entry.getValue(), true));
                if (counter < size) {
                    builder.append(",");
                }
                counter++;
            }

            builder.append("}").append(insideJSON ? "" : "\"");
            return builder.toString();
        } else if (object instanceof String) {
            return "\"" + object + "\"";
        }
        return object.toString();
    }


    public static @NotNull String getString(@NotNull Object object) {
        return SQLParser.getString(object, false);
    }

}
