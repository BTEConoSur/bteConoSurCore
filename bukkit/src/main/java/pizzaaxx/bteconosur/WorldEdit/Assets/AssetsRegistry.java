package pizzaaxx.bteconosur.WorldEdit.Assets;

import pizzaaxx.bteconosur.Registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AssetsRegistry implements Registry<String, Asset> {

    private final Map<String, Asset> assetsCache = new HashMap<>();
    private final Map<String, Long> deletionCache = new HashMap<>();

    private final Set<String> ids;



    @Override
    public boolean isLoaded(String id) {
        return false;
    }

    @Override
    public void load(String id) {

    }

    @Override
    public void unload(String id) {

    }

    @Override
    public boolean exists(String id) {
        return false;
    }

    @Override
    public Asset get(String id) {
        return null;
    }

    @Override
    public Set<String> getIds() {
        return null;
    }

    @Override
    public void scheduleDeletion(String id) {

    }
}
