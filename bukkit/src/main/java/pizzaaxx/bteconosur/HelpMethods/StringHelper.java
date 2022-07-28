package pizzaaxx.bteconosur.HelpMethods;

public class StringHelper {

    public static String removeAccents(String input) {
        return input
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("Á", "a")
                .replace("É", "e")
                .replace("Í", "i")
                .replace("Ó", "o")
                .replace("Ú", "u")
                .replace("ä", "a")
                .replace("ë", "e")
                .replace("ï", "i")
                .replace("ö", "o")
                .replace("ü", "u")
                .replace("Ä", "a")
                .replace("Ë", "e")
                .replace("Ï", "i")
                .replace("Ö", "o")
                .replace("Ü", "u");
    }

}
