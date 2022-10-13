package pizzaaxx.bteconosur.country.ProjectTypes.Points;

public class ProjectTypeParseException extends Exception {

    private final String message;

    public ProjectTypeParseException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
