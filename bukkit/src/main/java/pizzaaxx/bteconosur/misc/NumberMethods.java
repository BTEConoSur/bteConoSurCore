package pizzaaxx.bteconosur.misc;

public class NumberMethods {

    public static double getNumberInNewRange(double oldMin, double oldMax, double newMin, double newMax, double oldNumber) {

        double oldRange = oldMax - oldMin;
        if (oldRange == 0) {

            return newMin;

        } else {

            double newRange = newMax - newMin;
            return (((oldNumber - oldMin) * newRange) / oldRange) + newMin;

        }

    }

}
