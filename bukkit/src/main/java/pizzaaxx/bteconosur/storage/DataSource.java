package pizzaaxx.bteconosur.storage;

import java.util.Map;

public class DataSource {

    private final Map<String, String> source;

    public DataSource(Map<String, String> source) {
        this.source = source;
    }

    public <T> T get(String name, Class<T> clazz) {
        if (clazz.getSimpleName().equals("Integer")) {
            return (T) Integer.valueOf(get(name));
        }

        return clazz.cast(get(name));
    }

    public String get(String name) {
        return source.get(name);
    }

}
