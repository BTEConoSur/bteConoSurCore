package pizzaaxx.bteconosur.HelpMethods;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DualMap<K, V> {

    private final Map<K, V> KtoV;
    private final Map<V, K> VtoK;

    public DualMap() {
        this.KtoV = new HashMap<>();
        this.VtoK = new HashMap<>();
    }

    public DualMap(@NotNull Map<K, V> map) {

        this.KtoV = new HashMap<>();
        this.VtoK = new HashMap<>();

        for (K key : map.keySet()) {
            KtoV.put(key, map.get(key));
            VtoK.put(map.get(key), key);
        }
    }

    public void put(K key, V value) {
        KtoV.put(key, value);
        VtoK.put(value, key);
    }

    public V get2(K key) {
        return KtoV.get(key);
    }

    public K get1(V value) {
        return VtoK.get(value);
    }

    public boolean contains1(K key) {
        return KtoV.containsKey(key);
    }

    public boolean contains2(V value) {
        return VtoK.containsKey(value);
    }

    public void remove1(K key) {
        if (contains1(key)) {
            VtoK.remove(KtoV.get(key));
            KtoV.remove(key);
        }
    }

    public void remove2(V value) {
        if (contains2(value)) {
            KtoV.remove(VtoK.get(value));
            VtoK.remove(value);
        }
    }

    public Map<K, V> getKtoV() {
        return KtoV;
    }

    public Map<V, K> getVtoK() {
        return VtoK;
    }
}
