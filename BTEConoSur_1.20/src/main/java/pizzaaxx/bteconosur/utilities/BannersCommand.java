package pizzaaxx.bteconosur.utilities;

import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pizzaaxx.bteconosur.utils.Pair;

import java.util.*;

public class BannersCommand implements CommandExecutor, TabCompleter {

    private enum PatternColor {
        BACKGROUND, FRONT;
    }
    private final Map<String, List<Pair<PatternColor, PatternType>>> letters = new HashMap<>();

    public BannersCommand() {
        List<Pair<PatternColor, PatternType>> a = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_middle"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("a", a);
        List<Pair<PatternColor, PatternType>> b = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_middle"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("b", b);
        List<Pair<PatternColor, PatternType>> c = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("c", c);
        List<Pair<PatternColor, PatternType>> d = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("curly_border"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("half_vertical"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("d", d);
        List<Pair<PatternColor, PatternType>> e = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_middle"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("e", e);
        List<Pair<PatternColor, PatternType>> f = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_middle"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("f", f);
        List<Pair<PatternColor, PatternType>> g = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("half_horizontal"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("g", g);
        List<Pair<PatternColor, PatternType>> h = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_middle"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("h", h);
        List<Pair<PatternColor, PatternType>> i = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_center"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("i", i);
        List<Pair<PatternColor, PatternType>> j = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("half_horizontal"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("j", j);
        List<Pair<PatternColor, PatternType>> k = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("circle"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_downleft"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_downright"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("k", k);
        List<Pair<PatternColor, PatternType>> l = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("l", l);
        List<Pair<PatternColor, PatternType>> m = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("triangle_top"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("triangles_top"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("m", m);
        List<Pair<PatternColor, PatternType>> n = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_downright"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("n", n);
        List<Pair<PatternColor, PatternType>> o = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("o", o);
        List<Pair<PatternColor, PatternType>> p = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("half_horizontal_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_middle"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("p", p);
        List<Pair<PatternColor, PatternType>> q = Arrays.asList(
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("rhombus"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("square_bottom_right"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("q", q);
        List<Pair<PatternColor, PatternType>> r = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_downright"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("r", r);
        List<Pair<PatternColor, PatternType>> s = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_downright"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("curly_border"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("s", s);
        List<Pair<PatternColor, PatternType>> t = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_center"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("t", t);
        List<Pair<PatternColor, PatternType>> u = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("u", u);
        List<Pair<PatternColor, PatternType>> v = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("triangle_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_downleft"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("v", v);
        List<Pair<PatternColor, PatternType>> w = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("triangle_bottom"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("triangles_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("w", w);
        List<Pair<PatternColor, PatternType>> x = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_downleft"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_downright"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("x", x);
        List<Pair<PatternColor, PatternType>> y = Arrays.asList(
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_downright"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("diagonal_up_right"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_downleft"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("y", y);
        List<Pair<PatternColor, PatternType>> z = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_downleft"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("z", z);
        List<Pair<PatternColor, PatternType>> zero = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_downleft"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("0", zero);
        List<Pair<PatternColor, PatternType>> one = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_center"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("square_top_left"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("1", one);
        List<Pair<PatternColor, PatternType>> two = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("rhombus"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_downleft"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("2", two);
        List<Pair<PatternColor, PatternType>> three = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_middle"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("curly_border"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("3", three);
        List<Pair<PatternColor, PatternType>> four = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("half_horizontal_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_middle"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("4", four);
        List<Pair<PatternColor, PatternType>> five = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("rhombus"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_downright"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("5", five);
        List<Pair<PatternColor, PatternType>> six = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("half_horizontal"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_middle"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("6", six);
        List<Pair<PatternColor, PatternType>> seven = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_downleft"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("7", seven);
        List<Pair<PatternColor, PatternType>> eight = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_middle"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("8", eight);
        List<Pair<PatternColor, PatternType>> nine = Arrays.asList(
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_left"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("half_horizontal_bottom"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_top"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_right"))),
                new Pair<>(PatternColor.FRONT, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("stripe_middle"))),
                new Pair<>(PatternColor.BACKGROUND, Registry.BANNER_PATTERN.get(NamespacedKey.minecraft("border")))
        );
        letters.put("9", nine);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (sender instanceof Player p) {

            // /banner <letter> <back> <text>

            String PREFIX = "§f[§7BANNERS§f] §7>>§r ";
            if (args.length >= 3) {
                String letterColorString = args[0];
                String backColorString = args[1];
                String text = String.join(" ", Arrays.asList(args).subList(2, args.length)).toLowerCase();

                DyeColor letterColor = translateColor(letterColorString);
                if (letterColor == null) {
                    p.sendMessage(PREFIX + "Introduce un color de letra válido.");
                    return true;
                }

                DyeColor backColor = translateColor(backColorString);
                if (backColor == null) {
                    p.sendMessage(PREFIX + "Introduce un color de fondo válido.");
                    return true;
                }

                final Set<String> passed = new HashSet<>();
                for (String c : text.split("")) {
                    if (!passed.contains(c) && c.matches("[a-z0-9]")) {
                        ItemStack banner = new ItemStack(this.colorToMaterial(c.equals("q") ? letterColor : backColor), 1);
                        BannerMeta meta = (BannerMeta) banner.getItemMeta();
                        for (Pair<PatternColor, PatternType> pair : letters.get(c)) {
                            meta.addPattern(new Pattern((pair.getKey() == PatternColor.BACKGROUND ? backColor : letterColor), pair.getValue()));
                        }
                        banner.setItemMeta(meta);
                        p.getInventory().addItem(banner);
                    }
                    passed.add(c);
                }
            } else {
                p.sendMessage(PREFIX + "§cUso: /banner <color de letra> <color de fondo> <texto>");
            }
        }

        return true;
    }

    @Contract(pure = true)
    private final Material colorToMaterial(@NotNull DyeColor color) {
        switch (color) {
            case WHITE -> {
                return Material.WHITE_BANNER;
            }
            case ORANGE -> {
                return Material.ORANGE_BANNER;
            }
            case MAGENTA -> {
                return Material.MAGENTA_BANNER;
            }
            case LIGHT_BLUE -> {
                return Material.LIGHT_BLUE_BANNER;
            }
            case YELLOW -> {
                return Material.YELLOW_BANNER;
            }
            case LIME -> {
                return Material.LIME_BANNER;
            }
            case PINK -> {
                return Material.PINK_BANNER;
            }
            case GRAY -> {
                return Material.GRAY_BANNER;
            }
            case LIGHT_GRAY -> {
                return Material.LIGHT_GRAY_BANNER;
            }
            case CYAN -> {
                return Material.CYAN_BANNER;
            }
            case PURPLE -> {
                return Material.PURPLE_BANNER;
            }
            case BLUE -> {
                return Material.BLUE_BANNER;
            }
            case BROWN -> {
                return Material.BROWN_BANNER;
            }
            case GREEN -> {
                return Material.GREEN_BANNER;
            }
            case RED -> {
                return Material.RED_BANNER;
            }
            default -> {
                return Material.BLACK_BANNER;
            }
        }
    }

    private @Nullable DyeColor translateColor(@NotNull String color) {
        return switch (color) {
            case "white", "blanco" -> DyeColor.WHITE;
            case "orange", "naranjo", "naranja", "anaranjado" -> DyeColor.ORANGE;
            case "magenta" -> DyeColor.MAGENTA;
            case "light_blue", "celeste", "azul_claro" -> DyeColor.LIGHT_BLUE;
            case "yellow", "amarillo" -> DyeColor.YELLOW;
            case "lime", "lima", "verde_claro" -> DyeColor.LIME;
            case "pink", "rosado", "rosa" -> DyeColor.PINK;
            case "dark_grey", "dark_gray", "gris_oscuro" -> DyeColor.GRAY;
            case "gray", "grey", "gris", "gris_claro" -> DyeColor.LIGHT_GRAY;
            case "cyan", "cian" -> DyeColor.CYAN;
            case "purple", "purpura", "morado" -> DyeColor.PURPLE;
            case "blue", "azul", "azul_oscuro" -> DyeColor.BLUE;
            case "brown", "marron", "cafe" -> DyeColor.BROWN;
            case "green", "verde", "verde_oscuro" -> DyeColor.GREEN;
            case "red", "rojo" -> DyeColor.RED;
            case "black", "negro" -> DyeColor.BLACK;
            default -> null;
        };
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1 || args.length == 2) {
            completions.addAll(
                    Arrays.asList(
                            "white", "blanco",
                            "orange", "naranjo", "anaranjado", "naranja",
                            "magenta",
                            "light_blue", "celeste", "azul_claro",
                            "yellow", "amarillo",
                            "lime", "lima", "verde_claro",
                            "pink", "rosado", "rosa",
                            "dark_gray", "dark_grey", "gris_oscuro",
                            "gray", "grey", "gris", "gris_claro",
                            "cyan", "cian",
                            "purple", "purpura", "morado",
                            "blue", "azul", "azul_oscuro",
                            "brown", "marron", "cafe",
                            "green", "verde", "verde_oscuro",
                            "red", "rojo",
                            "black", "negro"
                    )
            );
        }

        List<String> finalCompletions = new ArrayList<>();
        for (String completion : completions) {
            if (completion.startsWith(args[args.length - 1])) {
                finalCompletions.add(completion);
            }
        }
        Collections.sort(finalCompletions);
        return finalCompletions;
    }
}
