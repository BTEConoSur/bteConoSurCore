package pizzaaxx.bteconosur.country.ProjectTypes.Points;

public class ProjectPointsTypeParseException extends Exception {

    private final String message;

    public ProjectPointsTypeParseException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
