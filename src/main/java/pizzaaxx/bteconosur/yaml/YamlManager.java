package pizzaaxx.bteconosur.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class YamlManager {

    private Map<String, Object> data;
    private File path;
    private String child;

    // CONSTRUCTOR
    public YamlManager(File path, String child) {
        this.path = path;
        this.child = child;

        File file = new File(path, child);

        Yaml yaml = new Yaml();

        if (!(file.isFile())) {
            try {
                file.createNewFile();
                this.data = new LinkedHashMap<String, Object>();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (file.length() == 0){
            this.data = new LinkedHashMap<String, Object>();
        } else {

            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            this.data = yaml.load(inputStream);
        }
    }

    // GET
    public Map<String, Object> getAllData() {
        return this.data;
    }

    public Object getValue(String key) {
        return getNode(key, data);
    }

    public static Object getNode(String key, Map<String, Object> map) {
        String[] steps = key.split("[.]");
        Map<String, Object> main = map;
        int i = 1;
        for (String name : steps) {
            if (steps.length == i) {
                return main.get(name);
            }
            if (main.get(name) != null) {
                try {
                    main = (Map<String, Object>) main.get(name);
                } catch (ClassCastException e) {
                    return null;
                }
            } else {
                return null;
            }
            i++;
        }
        return null;
    }

    // SET
    public void setValue(String key, Object value) {

        data = setNode(key, value, data);

    }

    public static Map<String, Object> setNode(String key, Object value, Map<String, Object> map) {
        List<String> steps = Arrays.asList(key.split("[.]"));
        List<Map<String, Object>> maps = new ArrayList<>();
        maps.add(map);

        int i = 1;
        for (String step : steps) {
            Map<String, Object> last = maps.get(maps.size() - 1);

            if (i >= steps.size()) {
                last.put(step, value);

                List<String> reverseSteps = steps.subList(0, steps.size() - 1);
                Collections.reverse(reverseSteps);
                Collections.reverse(maps);
                for (int j = 1; j < maps.size(); j++) {
                    maps.get(j).put(reverseSteps.get(j - 1), maps.get(j - 1));
                }

            } else {
                try {
                    maps.add(last.containsKey(step) ? (Map<String, Object>) last.get(step) : new HashMap<>());
                } catch (ClassCastException e) {
                    maps.add(new HashMap<>());
                }
            }

            i++;
        }

        return maps.get(maps.size() - 1);
    }

    // DELETE
    public void deleteValue(String key) {

        data = deleteNode(key, data);

    }

    public static Map<String, Object> deleteNode(String key, Map<String, Object> map) {
        List<String> steps = Arrays.asList(key.split("[.]"));
        List<Map<String, Object>> maps = new ArrayList<>();
        maps.add(map);

        int i = 1;
        for (String step : steps) {
            Map<String, Object> last = maps.get(maps.size() - 1);

            if (i >= steps.size()) {
                last.remove(step);

                List<String> reverseSteps = steps.subList(0, steps.size() - 1);
                Collections.reverse(reverseSteps);
                Collections.reverse(maps);
                for (int j = 1; j < maps.size(); j++) {
                    maps.get(j).put(reverseSteps.get(j - 1), maps.get(j - 1));
                }

            } else {
                try {
                    maps.add(last.containsKey(step) ? (Map<String, Object>) last.get(step) : new HashMap<>());
                } catch (ClassCastException e) {
                    maps.add(new HashMap<>());
                }
            }

            i++;
        }

        return maps.get(maps.size() - 1);
    }

    // LISTS

    public Boolean addToList(String key, Object value, Boolean duplicate) {
        List<Object> list;
        if (this.data.get(key) != null) {
            list = (List<Object>) this.data.get(key);
        } else {
            list = new ArrayList<>();
        }
        if (duplicate) {
            list.add(value);
        } else if (!(list.contains(value))) {
            list.add(value);
        }
        this.data.put(key, list);
        return true;
    }

    public Boolean removeFromList(String key, Object value) {

        if (this.data.get(key) != null) {
            List<Object> list;

            list = (List<Object>) this.data.get(key);
            list.remove(value);

            if (list.size() > 0) {
                this.data.put(key, list);
            } else {
                this.data.remove(key);
            }
        }
        return true;
    }

    public List<?> getList(String key) {
        if (this.data.get(key) instanceof List<?>) {
            return (List<?>) getValue(key);
        }
        return null;
    }

    // SAVE
    public void write() {
        DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml projects = new Yaml(options);

        File file = new File(this.path, this.child);

        if (!(file.isFile())) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        projects.dump(this.data, writer);
    }

    // OLD

    public static Map<String, Object> getYamlData(File parent, String child) {
        File file = new File(parent, child);

        Map<String, Object> data = null;

        Yaml yaml = new Yaml();


        if (!(file.isFile())) {
            try {
                file.createNewFile();
                data = new LinkedHashMap<String, Object>();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (file.length() == 0){
            data = new LinkedHashMap<String, Object>();
        } else {

            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            data = yaml.load(inputStream);
        }
        return data;
    }

    public static void writeYaml(File parent, String children, Map<String, Object> data){
        DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml projects = new Yaml(options);

        File file = new File(parent, children);

        if (!(file.isFile())) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        projects.dump(data, writer);
    }
}
