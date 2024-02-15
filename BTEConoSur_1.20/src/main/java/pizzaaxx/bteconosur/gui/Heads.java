package pizzaaxx.bteconosur.gui;

import org.bukkit.inventory.ItemStack;
import pizzaaxx.bteconosur.utils.StringUtils;

import java.util.List;

public class Heads {

    public static ItemStack CONFIRM =
            ItemBuilder.head(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=",
                    "§aConfirmar",
                    List.of(
                            StringUtils.deserialize("§a[✔] §7Haz click para confirmar.")
                    )
            );

    public static ItemStack CANCEL =
            ItemBuilder.head(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==",
                    "§cCancelar",
                    List.of(
                            StringUtils.deserialize("§c[✘] §7Haz click para cancelar.")
                    )
            );

    public static String BACK_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzNlOTE5MTlkYjBhY2VmZGMyNzJkNjdmZDg3YjRiZTg4ZGM0NGE5NTg5NTg4MjQ0NzRlMjFlMDZkNTNlNiJ9fX0=";

    public static String NEXT_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNmYzUyMjY0ZDhhZDllNjU0ZjQxNWJlZjAxYTIzOTQ3ZWRiY2NjY2Y2NDkzNzMyODliZWE0ZDE0OTU0MWY3MCJ9fX0=";

    public static String INFO_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmEyYWZhN2JiMDYzYWMxZmYzYmJlMDhkMmM1NThhN2RmMmUyYmFjZGYxNWRhYzJhNjQ2NjJkYzQwZjhmZGJhZCJ9fX0=";

    public static String MUSHROOM_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjk1MzRhZjE0YTdjYTVmMmM5ZTRmZDVkZGVlYmY3MzQxODFiOWI5ZTUzZTM2ZDAzOGM1MGU0NzNmYmVmNzRlMSJ9fX0=";

    public static String PISTON_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTk1ZWFhOTJkMmViOTY5NDI4NGI0ZTk4Y2FkZWNmZDdhODIzMjU2YmFkNWEzOTQ1OThmNjMyNGNmZTdmNzM3YiJ9fX0=";

    public static String STICKY_PISTON_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjExNzZjNGQ2Mzk1ZmY1NzY3YTc0YTM2OWZlMzg2ZDA2Y2M2MGEyMDk3YmM1YTUzYmQwMDVlYWRkMGE3Y2JkNCJ9fX0=";

    public static String IRON_TRAPDOOR_VALUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI3ZDJlODRhZTEwYzk3MWQ5MmNiMDViNmFiNDExNmY3NjUwM2E4N2QzMTc0MjQ5Y2QxZjQ5OTJiYTE4MWRiNCJ9fX0=";
}
