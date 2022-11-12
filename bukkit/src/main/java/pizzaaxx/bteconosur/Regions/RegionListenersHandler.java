package pizzaaxx.bteconosur.Regions;

import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Utils.StringMatcher;

import java.util.HashMap;
import java.util.Map;

public class RegionListenersHandler {

    private final BTEConoSur plugin;

    private final Map<StringMatcher, RegionListener> enterListeners = new HashMap<>();
    private final Map<StringMatcher, RegionListener> leaveListeners = new HashMap<>();


}
