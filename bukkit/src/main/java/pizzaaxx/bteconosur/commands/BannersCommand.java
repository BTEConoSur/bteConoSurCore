package pizzaaxx.bteconosur.commands;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BannersCommand implements CommandExecutor {

    private final Map<Character, List<PatternType>> letters = new HashMap<>();

    public BannersCommand() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String @NotNull [] args) {

        // /banner <letter> <back> <text>

        if (args.length >= 3) {
            String letterColor = args[0];
            String backColor = args[1];
            String text = String.join(" ", Arrays.asList(args).subList(2, args.length));
        }

        return true;
    }

    private DyeColor translateColor(@NotNull String color) {
        switch (color) {
            case "white":
            case "blanco":
                return DyeColor.WHITE;
            case "orange":
            case "naranjo":
            case "naranja":
            case "anaranjado":
                return DyeColor.ORANGE;
            case "magenta":
                return DyeColor.MAGENTA;
            case "light_blue":
            case "celeste":
            case "azul_claro":
                return DyeColor.LIGHT_BLUE;
            case "yellow":
            case "amarillo":
                return DyeColor.YELLOW;
            case "lime":
            case "lima":
            case "verde_claro":
                return DyeColor.LIME;
            case "pink":
            case "rosado":
            case "rosa":
                return DyeColor.PINK;
            case "dark_grey":
            case "dark_gray":
            case "gris_oscuro":
                return DyeColor.GRAY;
            case "gray":
            case "grey":
            case "gris":
            case "gris_claro":
                return DyeColor.SILVER;
            case "cyan":
            case "cian":
                return DyeColor.CYAN;
            case "purple":
            case "purpura":
            case "morado":
                return DyeColor.PURPLE;
            case "blue":
            case "azul":
            case "azul_oscuro":
                return DyeColor.BLUE;
            case "brown":
            case "marron":
            case "cafe":
                return DyeColor.BROWN;
            case "green":
            case "verde":
            case "verde_oscuro":
                return DyeColor.GREEN;
            case "red":
            case "rojo":
                return DyeColor.RED;
            case "black":
            case "negro":
                return DyeColor.BLACK;
        }
        return null;
    }
}
