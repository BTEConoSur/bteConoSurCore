package pizzaaxx.bteconosur.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StringUtils {

    public static String[] UPPER_CASE = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    public static String[] LOWER_CASE = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
    public static String[] DIGITS = {"1","2","3","4","5","6","7","8","9","0"};

    @NotNull
    public static String generateCode(int length, Collection<String> notIn, @NotNull String[]... characters) {
        List<String> chars = new ArrayList<>();
        for (String[] charactersList : characters) {
            chars.addAll(Arrays.asList(charactersList));
        }

        Random random = new Random();
        int charsLength = chars.size();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(chars.get(random.nextInt(charsLength)));
        }

        while (notIn.contains(builder.toString())) {
            builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(chars.get(random.nextInt(charsLength)));
            }
        }

        return builder.toString();
    }

}
