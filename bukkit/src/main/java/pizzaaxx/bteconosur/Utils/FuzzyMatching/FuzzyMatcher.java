package pizzaaxx.bteconosur.Utils.FuzzyMatching;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;

import java.util.List;

public class FuzzyMatcher {

    private final BTEConoSur plugin;
    private final LevenshteinDistance distance = new LevenshteinDistance();

    public FuzzyMatcher(BTEConoSur plugin) {
        this.plugin = plugin;
    }
    public int getDistance(String base, String input) {
        return distance.apply(base, input);
    }

    /**
     * Get the minimum distance present within the string.
     * @param base The base string.
     * @param input The input string.
     * @return The minimum distance found within the two strings.
     */
    public int getMinimumDistance(@NotNull String base, @NotNull String input) {
        if (base.length() <= input.length()) {
            return this.getDistance(base, input);
        }

        int inputLength = input.length();
        int min = base.length() * 2;
        for (int i = 0; i <= base.length() - inputLength; i++) {
            int distance = this.getDistance(input, base.substring(i, i + inputLength));
            if (distance < min) {
                min = distance;
            }
        }
        return min;
    }
}
