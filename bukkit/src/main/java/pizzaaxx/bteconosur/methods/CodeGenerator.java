package pizzaaxx.bteconosur.methods;

import java.util.*;

public class CodeGenerator {

    private static final Random RANDOM = new Random();
    private static final String[] CHARACTERS = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    public static String generateCode(Integer length) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 1; i <= length; i++) {
            int number = RANDOM.nextInt(CHARACTERS.length);
            stringBuilder.append(CHARACTERS[number]);
        }

        return stringBuilder.toString();
    }

}
