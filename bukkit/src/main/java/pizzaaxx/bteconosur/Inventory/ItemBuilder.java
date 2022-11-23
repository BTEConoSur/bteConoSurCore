package pizzaaxx.bteconosur.Inventory;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Material;
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
    private final ItemMeta itemMeta;

    public ItemBuilder(Material material, int amount, int data) {
        itemStack = new ItemStack(material, amount, (short) data);
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

    public ItemBuilder headValue(String texture) {
        if (itemMeta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) itemMeta;

            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
            PropertyMap propertyMap = gameProfile.getProperties();

            propertyMap.put("texture", new Property("texture", texture));

            try {
                Field field = skullMeta
                        .getClass()
                        .getDeclaredField("profile");
                field.setAccessible(true);

                field.set(skullMeta, gameProfile);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

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
    @Contract(" -> new")
    public static ItemBuilder head(String value) {
        return new ItemBuilder(Material.SKULL_ITEM, 1, 3).headValue(value);
    }

}
