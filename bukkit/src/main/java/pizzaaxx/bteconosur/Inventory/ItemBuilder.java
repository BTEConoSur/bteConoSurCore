package pizzaaxx.bteconosur.Inventory;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private final ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(Material material, int amount, int data) {
        itemStack = new ItemStack(material, amount, (short) data);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material, int amount) {
        itemStack = new ItemStack(material, amount);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(Material material) {
        itemStack = new ItemStack(material);
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder name(String name) {
        itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        itemMeta.setLore(lore);
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
        if (itemMeta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) itemMeta;

            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
            gameProfile.getProperties().put("textures", new Property("textures", value));

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
        return of(material, 1, 1);
    }

    @NotNull
    @Contract("_, _ -> new")
    public static ItemBuilder of(Material material, int amount) {
        return of(material, amount, 1);
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static ItemBuilder of(Material material, int amount, int data) {
        return new ItemBuilder(material, amount, data);
    }

    @NotNull
    public static ItemStack head(String value, String name, List<String> lore) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM,1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

        if (name != null) {
            skullMeta.setDisplayName(name);
        }

        if (lore != null) {
            skullMeta.setLore(lore);
        }

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", value, null));
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

}
