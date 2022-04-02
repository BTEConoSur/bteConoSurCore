package pizzaaxx.bteconosur.helper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateHelper {

    private static final Map<Type, Integer> FORMULAS = new HashMap<>();

    static {
        FORMULAS.put(Type.DAY, 24 * 60 * 60 * 1000);
        FORMULAS.put(Type.HOUR, 60 * 60 * 1000);
        FORMULAS.put(Type.MINUTE, 60 * 1000);
        FORMULAS.put(Type.SECOND, 1000);

    }

    public static long difference(
            Date firstDate,
            Date secondDate
    ) {

        long longFirstDate = firstDate.getTime();
        long longSecondDate = secondDate.getTime();

        return longSecondDate - longFirstDate;
    }

    public static DataTime differenceToData(
            Date firstDate,
            Date secondDate
    ) {
        long difference = difference(firstDate, secondDate);
        DataTime dataTime = new DataTime();

        difference = resolve(Type.DAY, difference, dataTime);
        difference = resolve(Type.HOUR, difference, dataTime);
        difference = resolve(Type.MINUTE, difference, dataTime);
        resolve(Type.SECOND, difference, dataTime);

        return dataTime;
    }

    public static long resolve(Type type, long difference, DataTime dataTime) {
        int aLong = FORMULAS.get(type);
        dataTime.set(type, difference / aLong);
        return difference % aLong;
    }

    public enum Type {
        DAY, HOUR, MINUTE, SECOND;
    }

}
