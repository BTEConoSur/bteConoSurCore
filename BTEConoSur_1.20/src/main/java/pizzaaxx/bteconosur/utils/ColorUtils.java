package pizzaaxx.bteconosur.utils;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ColorUtils {

    public static @NotNull Color blendWithGray(@NotNull Color original, float blendRatio) {
        int red = (int) ((original.getRed() * (1 - blendRatio)) + (128 * blendRatio));
        int green = (int) ((original.getGreen() * (1 - blendRatio)) + (128 * blendRatio));
        int blue = (int) ((original.getBlue() * (1 - blendRatio)) + (128 * blendRatio));
        return new Color(red, green, blue);
    }

}
