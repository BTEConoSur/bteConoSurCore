package pizzaaxx.bteconosur.HelpMethods;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ColorHelper {
    public static double colorDistance(@NotNull Color c1, @NotNull Color c2) {
        int red1 = c1.getRed();
        int red2 = c2.getRed();
        int rMean = (red1 + red2) >> 1;
        int r = red1 - red2;
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return Math.sqrt((((512+rMean)*r*r)>>8) + 4*g*g + (((767-rMean)*b*b)>>8));
    }

}
