package pizzaaxx.bteconosur.methods;

import java.util.*;

public class CodeGenerator {

    private static final Random RANDOM = new Random();

    public static String generateCode(Integer length) {
        List<String> chars = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));
        String code = "";
        for (int i=1; i<=length; i++) {
            int rndmNumber = rndm.nextInt(chars.size());
            code = code + chars.get(rndmNumber);
        }
        return code;
    }
}
