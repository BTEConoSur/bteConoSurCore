package pizzaaxx.bteconosur.helper;

public class NickNameValidator {

    private static final String REGEX = "[a-zA-Z0-9_]{1,16}";

    public static boolean validate(String nick) {
        if (nick.matches(REGEX)) {
            return true;
        }
        return false;
    }

}
