package pizzaaxx.bteconosur.Chat;

public class ChatException extends Exception {

    public enum Type {
        ProjectNotFound,
        IdNotFound
    }

    private final Type type;

    public ChatException(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
