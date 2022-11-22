package pizzaaxx.bteconosur.Utils.FuzzyMatching;

import org.apache.commons.text.similarity.LevenshteinDistance;
import pizzaaxx.bteconosur.BTEConoSur;

public class FuzzyMatcher {

    private final BTEConoSur plugin;
    private final LevenshteinDistance distance = new LevenshteinDistance();

    public FuzzyMatcher(BTEConoSur plugin) {
        this.plugin = plugin;
    }


}
