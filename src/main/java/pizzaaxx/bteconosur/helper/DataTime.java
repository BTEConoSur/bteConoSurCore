package pizzaaxx.bteconosur.helper;

public class DataTime {

    private long seconds = 0L;
    private long minutes = 0L;
    private long hours = 0L;
    private long days = 0L;

    public void set(DateHelper.Type type, long quantity) {
        switch (type) {
            case DAY:
                this.days = quantity;
            case HOUR:
                this.hours = quantity;
            case MINUTE:
                this.minutes = quantity;
            default:
                this.seconds = quantity;
        }
    }

    public long get(DateHelper.Type type) {
        switch (type) {
            case DAY:
                return days;
            case HOUR:
                return hours;
            case MINUTE:
                return minutes;
            default:
                return seconds;
        }
    }

}
