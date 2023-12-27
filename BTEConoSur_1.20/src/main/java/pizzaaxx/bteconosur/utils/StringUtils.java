package pizzaaxx.bteconosur.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StringUtils {

    public static @NotNull String transformToSmallCapital(@NotNull String input) {
        StringBuilder result = new StringBuilder();

        for (char c : input.toLowerCase().toCharArray()) {
            if (Character.isLetter(c)) {
                switch (c) {
                    case 'a', 'á' -> result.append('ᴀ');
                    case 'b' -> result.append('ʙ');
                    case 'c' -> result.append('ᴄ');
                    case 'd' -> result.append('ᴅ');
                    case 'e', 'é' -> result.append('ᴇ');
                    case 'f' -> result.append('ꜰ');
                    case 'g' -> result.append('ɢ');
                    case 'h' -> result.append('ʜ');
                    case 'i', 'í' -> result.append('ɪ');
                    case 'j' -> result.append('ᴊ');
                    case 'k' -> result.append('ᴋ');
                    case 'l' -> result.append('ʟ');
                    case 'm' -> result.append('ᴍ');
                    case 'n' -> result.append('ɴ');
                    case 'o', 'ó' -> result.append('ᴏ');
                    case 'p' -> result.append('ᴘ');
                    case 'q' -> result.append('ꞯ');
                    case 'r' -> result.append('ʀ');
                    case 's' -> result.append('ꜱ');
                    case 't' -> result.append('ᴛ');
                    case 'u', 'ú' -> result.append('ᴜ');
                    case 'v' -> result.append('ᴠ');
                    case 'w' -> result.append('ᴡ');
                    case 'x' -> result.append('x');
                    case 'y' -> result.append('ʏ');
                    case 'z' -> result.append('ᴢ');
                    default -> result.append(c);
                }
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static String[] UPPER_CASE = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    public static String[] LOWER_CASE = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
    public static String[] DIGITS = {"1","2","3","4","5","6","7","8","9","0"};

    @NotNull
    public static String generateCode(int length, Collection<String> notIn, @NotNull String[] @NotNull ... characters) {
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

    public static @NotNull Component deserialize(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

}
