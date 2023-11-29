package pizzaaxx.bteconosur.inventory;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(Material material, int amount) {
        itemStack = new ItemStack(material, amount);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material) {
        itemStack = new ItemStack(material);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder name(String name) {
        itemMeta.displayName(Component.text(name));
        return this;
    }

    public ItemBuilder lore(@NotNull List<String> lore) {
        itemMeta.lore(lore.stream().map(Component::text).collect(Collectors.toList()));
        return this;
    }

    public ItemBuilder lore(String... lore) {
        return lore(Arrays.asList(lore));
    }

    public ItemBuilder flags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder unbreakable() {
        itemMeta.setUnbreakable(true);
        return this;
    }

    public ItemBuilder headValue(String value) {
        if (itemMeta instanceof SkullMeta skullMeta) {

            PlayerProfile gameProfile = Bukkit.createProfile(UUID.randomUUID(), null);
            gameProfile.getProperties().add(new ProfileProperty("textures", value));

            try {
                Field field = skullMeta
                        .getClass()
                        .getDeclaredField("profile");
                field.setAccessible(true);

                field.set(skullMeta, gameProfile);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            itemStack.setItemMeta(skullMeta);
            itemMeta = itemStack.getItemMeta();

        }

        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @NotNull
    @Contract("_ -> new")
    public static ItemBuilder of(Material material) {
        return of(material, 1);
    }
    @NotNull
    @Contract("_, _ -> new")
    public static ItemBuilder of(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    @NotNull
    public static ItemStack head(UUID owner, String name, @Nullable List<String> lore) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        if (name != null) {
            skullMeta.displayName(Component.text(name));
        }

        if (lore != null) {
            skullMeta.lore(lore.stream().map(Component::text).collect(Collectors.toList()));
        }

        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        skull.setItemMeta(skullMeta);

        return skull;
    }

    @NotNull
    public static ItemStack head(String value, String name, @Nullable List<String> lore) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD,1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        if (name != null) {
            skullMeta.displayName(Component.text(name));
        }

        if (lore != null) {
            skullMeta.lore(lore.stream().map(Component::text).collect(Collectors.toList()));
        }

        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
        profile.getProperties().add(new ProfileProperty("textures", value));
        Field field;
        try {
            field = skullMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException x) {
            x.printStackTrace();
        }
        skull.setItemMeta(skullMeta);

        return skull;
    }

    public static String CONFIRM_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=";

    public static String CANCEL_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc1NDgzNjJhMjRjMGZhODQ1M2U0ZDkzZTY4YzU5NjlkZGJkZTU3YmY2NjY2YzAzMTljMWVkMWU4NGQ4OTA2NSJ9fX0=";

    public static String BACK_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzNlOTE5MTlkYjBhY2VmZGMyNzJkNjdmZDg3YjRiZTg4ZGM0NGE5NTg5NTg4MjQ0NzRlMjFlMDZkNTNlNiJ9fX0=";

    public static String NEXT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNmYzUyMjY0ZDhhZDllNjU0ZjQxNWJlZjAxYTIzOTQ3ZWRiY2NjY2Y2NDkzNzMyODliZWE0ZDE0OTU0MWY3MCJ9fX0=";

    public static String INFO_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcwNWZkOTRhMGM0MzE5MjdmYjRlNjM5YjBmY2ZiNDk3MTdlNDEyMjg1YTAyYjQzOWUwMTEyZGEyMmIyZTJlYyJ9fX0=";

    public static String LEFT_DOWN_CORNER_ARROW_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTdkZDc2YWIzZjljNjQ1MTk2ZTI0NTg1NjRkODU1NmFkNTlmZDMyNzI1ZWQ0ODg4YzAxZTMwN2EzN2FlYiJ9fX0=";
}
