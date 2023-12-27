package pizzaaxx.bteconosur.gui.book;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BookBuilder {

    public static class PageBuilder {

        private Component page = Component.empty();

        public PageBuilder add(Component component) {
            this.page = this.page.append(component);
            return this;
        }

        //new line
        public PageBuilder newLine() {
            this.page = this.page.append(Component.newline());
            return this;
        }

        public Component build() {
            return this.page;
        }

    }

    private final List<Component> pages = new ArrayList<>();

    public BookBuilder addPage(Component component) {
        this.pages.add(component);
        return this;
    }

    public void open(@NotNull Player player) {
        ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) stack.getItemMeta();
        meta.setAuthor("BTEConoSur");
        meta.setTitle("BTEConoSur");
        for (Component page : pages) {
            meta.addPages(page);
        }
        stack.setItemMeta(meta);
        player.openBook(stack);
    }

}
