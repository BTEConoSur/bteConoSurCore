package pizzaaxx.bteconosur.commands;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pizzaaxx.bteconosur.helper.Pair;

import java.util.*;

public class BannersCommand implements CommandExecutor {

    private final String PREFIX = "§f[§7BANNERS§f] §7>>§r ";

    private enum PatternColor {
        BACKGROUND, FRONT
    }

    private final Map<String, List<Pair<PatternColor, PatternType>>> letters = new HashMap<>();

    public BannersCommand() {
        List<Pair<PatternColor, PatternType>> a = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ms")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("a", a);
        List<Pair<PatternColor, PatternType>> b = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ms")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("b", b);
        List<Pair<PatternColor, PatternType>> c = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("c", c);
        List<Pair<PatternColor, PatternType>> d = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("cbo")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("vh")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("d", d);
        List<Pair<PatternColor, PatternType>> e = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ms")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("e", e);
        List<Pair<PatternColor, PatternType>> f = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ms")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("f", f);
        List<Pair<PatternColor, PatternType>> g = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("hh")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("g", g);
        List<Pair<PatternColor, PatternType>> h = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ms")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("h", h);
        List<Pair<PatternColor, PatternType>> i = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("cs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("i", i);
        List<Pair<PatternColor, PatternType>> j = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("hh")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("j", j);
        List<Pair<PatternColor, PatternType>> k = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("mc")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("dls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("drs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("k", k);
        List<Pair<PatternColor, PatternType>> l = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("l", l);
        List<Pair<PatternColor, PatternType>> m = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("tt")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("tts")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("m", m);
        List<Pair<PatternColor, PatternType>> n = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("drs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("n", n);
        List<Pair<PatternColor, PatternType>> o = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("o", o);
        List<Pair<PatternColor, PatternType>> p = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("hhb")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ms")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("p", p);
        List<Pair<PatternColor, PatternType>> q = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("mr")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("br")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("q", q);
        List<Pair<PatternColor, PatternType>> r = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("drs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("r", r);
        List<Pair<PatternColor, PatternType>> s = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("drs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("cbo")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("s", s);
        List<Pair<PatternColor, PatternType>> t = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("cs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("t", t);
        List<Pair<PatternColor, PatternType>> u = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("u", u);
        List<Pair<PatternColor, PatternType>> v = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bt")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("dls")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("v", v);
        List<Pair<PatternColor, PatternType>> w = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bt")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bts")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("w", w);
        List<Pair<PatternColor, PatternType>> x = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("dls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("drs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("x", x);
        List<Pair<PatternColor, PatternType>> y = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("drs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rd")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("dls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bo"))
        ));
        letters.put("y", y);
        List<Pair<PatternColor, PatternType>> z = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("dls")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("z", z);
        List<Pair<PatternColor, PatternType>> zero = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("dls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("0", zero);
        List<Pair<PatternColor, PatternType>> one = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("cs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("tl")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("1", one);
        List<Pair<PatternColor, PatternType>> two = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("mr")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("dls")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("2", two);
        List<Pair<PatternColor, PatternType>> three = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ms")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("cbo")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("3", three);
        List<Pair<PatternColor, PatternType>> four = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("hhb")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ms")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("4", four);
        List<Pair<PatternColor, PatternType>> five = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("mr")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("drs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("5", five);
        List<Pair<PatternColor, PatternType>> six = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("hh")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ms")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("6", six);
        List<Pair<PatternColor, PatternType>> seven = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("dls")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("7", seven);
        List<Pair<PatternColor, PatternType>> eight = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("bs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ms")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("8", eight);
        List<Pair<PatternColor, PatternType>> nine = new ArrayList<>(Arrays.asList(
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ls")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("hhb")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ts")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("rs")),
                new Pair<>(PatternColor.FRONT, PatternType.getByIdentifier("ms")),
                new Pair<>(PatternColor.BACKGROUND, PatternType.getByIdentifier("bo"))
        ));
        letters.put("9", nine);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String @NotNull [] args) {

        if (sender instanceof Player) {
            Player p = (Player) sender;

            // /banner <letter> <back> <text>

            if (args.length >= 3) {
                String letterColor = args[0];
                String backColor = args[1];
                String text = String.join(" ", Arrays.asList(args).subList(2, args.length));
            } else {
                p.sendMessage(PREFIX + "§Uso: /banner <color de letra> <color de fondo> <texto>");
            }
        }

        return true;
    }

    private @Nullable DyeColor translateColor(@NotNull String color) {
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
