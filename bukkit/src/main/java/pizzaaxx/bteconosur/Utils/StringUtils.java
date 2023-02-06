package pizzaaxx.bteconosur.Utils;

import org.jetbrains.annotations.Contract;
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

    @NotNull
    public static String insert(@NotNull String originalString, String insertedString, Integer... indexes) {

        Set<Integer> index = new HashSet<>(Arrays.asList(indexes));
        StringBuilder result = new StringBuilder(new String());

        for (int i = 0; i < originalString.length(); i++) {
            result.append(originalString.charAt(i));
            if (index.contains(i)) {
                result.append(insertedString);
            }
        }

        return result.toString();
    }

    @NotNull
    @Contract(pure = true)
    public static String getGenericPrefix(String color, String name) {
        return "§f[§" + color + name + "§f] §7>> §f";
    }

    @NotNull
    @Contract(pure = true)
    public static StringBuilder[] getASCIITable(@NotNull String[][] values) {

        StringBuilder[] result = new StringBuilder[values[0].length + 3];

        int[] maxLengths = new int[values.length];
        int counter = 0;
        for (String[] column : values) {
            int maxLength = 0;
            for (String value : column) {
                if (value.length() > maxLength) {
                    maxLength = value.length();
                }
            }
            maxLengths[counter] = maxLength;
            counter++;
        }

        int lastLineIndex = values[0].length + 2;

        for (int i = 0; i < result.length; i++) {
            if (i == 0 || i == 2 || i == lastLineIndex) {
                result[i] = new StringBuilder("+");
            } else {
                result[i] = new StringBuilder("|");
            }
        }

        int columnCounter = 0;
        for (String[] column : values) {
            int valuesCounter = 0;
            for (int i = 0; i < result.length; i++) {
                if (i == 0 || i == 2 || i == lastLineIndex) {
                    for (int j = 0; j < maxLengths[columnCounter] + 2; j++) {
                        result[i].append("-");
                    }
                    result[i].append("+");
                } else {
                    result[i].append(" ").append(column[valuesCounter]);
                    for (int j = 0; j < maxLengths[columnCounter] - column[valuesCounter].length(); j++) {
                        result[i].append(" ");
                    }
                    result[i].append(" |");
                    valuesCounter++;
                }
            }
            columnCounter++;
        }
        return result;
    }
}
