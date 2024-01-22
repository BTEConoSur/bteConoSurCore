package pizzaaxx.bteconosur.utils.registry;

import kotlin.jvm.functions.Function0;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSurPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BaseRegistry<T extends RegistrableEntity<K>, K> {

    private final BTEConoSurPlugin plugin;
    protected final Map<K, T> cacheMap = new HashMap<>();
    private final Map<K, Long> deletionMap = new HashMap<>();

    public final boolean cache;
    private final Function<K, T> get;
    public final Collection<K> ids;

    public BaseRegistry(BTEConoSurPlugin plugin, @NotNull Function0<Collection<K>> getIDs, Function<K, T> get, boolean cache) {
        this.plugin = plugin;
        this.ids = getIDs.invoke();
        this.get = get;
        this.cache = cache;

        // IF VALUES AREN'T TO BE CACHED, ALL SHOULD BE LOADED INITIALLY
        if (!this.cache) {
            for (K id : ids) {
                T value = this.get.apply(id);
                this.cacheMap.put(id, value);
            }
        }
    }

    public Collection<K> getIds() {
        return ids;
    }

    public boolean exists(K id) {
        return ids.contains(id);
    }

    public boolean isLoaded(K id) {
        return cacheMap.containsKey(id);
    }

    /**
     *
     * @param id The ID to add.
     * @return True if the ID was not registered before and was added. False otherwise.
     */
    public boolean registerID(K id) {
        if (this.ids.contains(id)) {
            return false;
        }
        this.ids.add(id);

        // IF VALUES AREN'T TO BE CACHED, LOAD WHEN REGISTERING
        if (!this.cache) {
            T value = this.get.apply(id);
            this.cacheMap.put(id, value);
        }

        return true;
    }

    /**
     *
     * @param id The ID to remove.
     * @return True if the ID was registered before and was removed. False otherwise.
     */
    public boolean unregisterID(K id) {
        if (!this.ids.contains(id)) {
            return false;
        }
        this.ids.remove(id);

        // IF VALUES AREN'T TO BE CACHED, UNLOAD WHEN UNREGISTERING
        if (!this.cache) {
            this.cacheMap.remove(id);
        }

        return true;
    }

    public void unload(K id) {
        if (this.cache) {
            this.cacheMap.remove(id);
            this.deletionMap.remove(id);
        }
    }

    public T get(K id) {
        if (this.cacheMap.containsKey(id)) {
            return this.cacheMap.get(id);
        } else {
            if (this.ids.contains(id)) {
                T value = this.get.apply(id);
                this.cacheMap.put(id, value);
                this.scheduleDeletion(id);
                return value;
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    private void scheduleDeletion(K id) {
        if (this.cache) {
            this.deletionMap.put(id, System.currentTimeMillis());
            Bukkit.getScheduler().runTaskLater(
                    plugin,
                    scheduledTask -> {
                        if (this.deletionMap.containsKey(id) && System.currentTimeMillis() - this.deletionMap.get(id) > 540000) {
                            this.deletionMap.remove(id);
                            this.cacheMap.remove(id);
                        }
                    },
                    10 * 60 * 20
            );
        }
    }

}
