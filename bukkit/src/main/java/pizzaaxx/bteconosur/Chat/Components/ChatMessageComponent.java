package pizzaaxx.bteconosur.Chat.Components;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import xyz.upperlevel.spigot.book.BookUtil;

public class ChatMessageComponent {

    private final String message;
    private final ChatColor color;
    private final HoverAction hover;
    private final ClickAction click;

    public ChatMessageComponent(String message) {
        this (message, null, null, null);
    }

    public ChatMessageComponent(String message, ChatColor color) {
        this(message, color, null, null);
    }

    public ChatMessageComponent(String message, HoverAction hover) {
        this(message, null, hover, null);
    }

    public ChatMessageComponent(String message, ClickAction click) {
        this(message, null, null, click);
    }

    public ChatMessageComponent(String message, ChatColor color, HoverAction hover) {
        this(message, color, hover, null);
    }

    public ChatMessageComponent(String message, ChatColor color, ClickAction click) {
        this(message, color, null, click);
    }

    public ChatMessageComponent(String message, HoverAction hover, ClickAction click) {
        this(message, null, hover, click);
    }

    public ChatMessageComponent(String message, ChatColor color, HoverAction hover, ClickAction click) {
        this.message = message;
        this.color = color;
        this.hover = hover;
        this.click = click;
    }

    public BaseComponent getBaseComponent() {
        BookUtil.TextBuilder builder = BookUtil.TextBuilder.of(message);
        if (color != null) {
            builder.color(color);
        } else {
            builder.color(ChatColor.WHITE);
        }
        if (hover != null) {
            builder.onHover(BookUtil.HoverAction.showText(hover.getString()));
        }
        if (click != null) {
            if (click.getAction().startsWith("http")) {
                builder.onClick(BookUtil.ClickAction.openUrl(click.getAction()));
            } else {
                builder.onClick(BookUtil.ClickAction.runCommand(click.getAction().replaceFirst("/", "")));
            }
        }
        return builder.build();
    }
}
