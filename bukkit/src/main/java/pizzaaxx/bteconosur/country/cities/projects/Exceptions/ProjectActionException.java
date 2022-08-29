package pizzaaxx.bteconosur.country.cities.projects.Exceptions;

public class ProjectActionException extends Exception {

    public enum Type {

        InvalidName,
        PlayerNotMember,
        PlayerNotOnline,
        MemberLimitReached,
        TargetLimitReached,
        PlayerAlreadyAMember,
        NewRegionOutsideCountry,
        ProjectAlreadyClaimed

    }

    private final Type type;

    public ProjectActionException(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
