package pizzaaxx.bteconosur.server.player.notification;

public class Notification {

    private final String message;
    private final long timestamp;

    private final boolean read;

    public Notification(String message,
                        long timestamp,
                        boolean read) {
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }

}
