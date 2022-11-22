package pizzaaxx.bteconosur.Utils;

import java.util.HashMap;
import java.util.Map;

public class DualMap<K, V> {

    private final Map<K, V> KtoV = new HashMap<>();
    private final Map<V, K> VtoK = new HashMap<>();

    public void put(K k, V v) {
        KtoV.put(k, v);
        VtoK.put(v, k);
    }

    public void removeK(K k) {
        KtoV.remove(k);
        VtoK.values().remove(k);
    }

    public void removeV(V v) {
        VtoK.remove(v);
        KtoV.values().remove(v);
    }

    public K getK(V v) {
        return VtoK.get(v);
    }

    public V getV(K k) {
        return KtoV.get(k);
    }
}
