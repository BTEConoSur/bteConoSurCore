package pizzaaxx.bteconosur.SQL;

import com.sk89q.worldedit.BlockVector2D;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Projects.Project;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SQLParser {

    /**
     *
     * @param object The object to be parsed.
     * @param insideJSON Whether the object is part of a JSON object or not.
     * @return A {@link String} representation of the value depending on its context.
     */
    @Contract(pure = true)
    public static @NotNull String getString(Object object, boolean insideJSON) {

        if (object instanceof UUID) {
            // binary(16)
            UUID uuid = (UUID) object;
            if (insideJSON) {
                return "\"" + uuid + "\"";
            }
            return "unhex(replace('" + uuid + "','-',''))";
        } else if (object instanceof Location) {

            Location loc = (Location) object;
            Map<String, Double> coords = new HashMap<>();

            coords.put("x", loc.getX());
            coords.put("z", loc.getZ());
            coords.put("y", loc.getY());
            return SQLParser.getString(coords, insideJSON);

        } else if (object instanceof BlockVector2D) {
            
            BlockVector2D vector = (BlockVector2D) object;
            Map<String, Double> coords = new HashMap<>();
            coords.put("x", vector.getX());
            coords.put("z", vector.getZ());
            return SQLParser.getString(coords, insideJSON);
            
        } else if (object instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) object;
            int size = collection.size();

            StringBuilder builder = new StringBuilder((insideJSON?"":"'") + "[");

            int counter = 1;
            for (Object obj : collection) {
                builder.append(SQLParser.getString(obj, true));
                if (counter < size) {
                    builder.append(", ");
                }
                counter++;
            }

            builder.append("]").append(insideJSON ? "" : "'");
            return builder.toString();
        } else if (object instanceof Map<?,?>) {
            Map<?, ?> map = (Map<?, ?>) object;
            int size = map.size();

            StringBuilder builder = new StringBuilder((insideJSON?"":"'") + "{");

            int counter = 1;
            for (Map.Entry<?,?> entry : map.entrySet()) {
                builder.append(SQLParser.getString(entry.getKey(), true)).append(": ").append(SQLParser.getString(entry.getValue(), true));
                if (counter < size) {
                    builder.append(", ");
                }
                counter++;
            }

            builder.append("}").append(insideJSON ? "" : "'");
            return builder.toString();
        } else if (object instanceof String) {
            return (insideJSON?"\"":"'") + object + (insideJSON?"\"":"'");
        } else if (object instanceof Country) {
            Country country = (Country) object;
            return (insideJSON?"\"":"'") + country.getName() + (insideJSON?"\"":"'");
        } else if (object instanceof Project) {
            Project project = (Project) object;
            return (insideJSON?"\"":"'") + project.getId() + (insideJSON?"\"":"'");
        } else if (object == null) {
            return (insideJSON?"{}":"NULL");
        }
        return object.toString();
    }


    /**
     * A shorthand method that assumes the object is not inside a JSON object.
     * @param object The object to be parsed.
     * @return A {@link String} representation of the value.
     */
    public static @NotNull String getString(@NotNull Object object) {
        return SQLParser.getString(object, false);
    }

}
