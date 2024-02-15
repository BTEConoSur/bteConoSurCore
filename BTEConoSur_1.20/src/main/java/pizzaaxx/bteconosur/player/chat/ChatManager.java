package pizzaaxx.bteconosur.player.chat;

import com.github.PeterMassmann.Columns.SQLColumnSet;
import com.github.PeterMassmann.Conditions.SQLANDConditionSet;
import com.github.PeterMassmann.Conditions.SQLOperatorCondition;
import com.github.PeterMassmann.SQLResult;
import com.github.PeterMassmann.Values.SQLValue;
import com.github.PeterMassmann.Values.SQLValuesSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import pizzaaxx.bteconosur.BTEConoSurPlugin;
import pizzaaxx.bteconosur.chat.Chat;
import pizzaaxx.bteconosur.chat.ChatProvider;
import pizzaaxx.bteconosur.player.OfflineServerPlayer;
import pizzaaxx.bteconosur.player.OnlineServerPlayer;
import pizzaaxx.bteconosur.player.PlayerManager;

import java.sql.ResultSet;
import java.sql.SQLException;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static pizzaaxx.bteconosur.chat.ChatProvider.CHAT_PROVIDERS;

public class ChatManager implements PlayerManager {

    private final BTEConoSurPlugin plugin;
    private final OnlineServerPlayer player;

    private boolean hidden;
    private String currentChat;
    private String defaultChat;
    private String nickname;
    private String countryPrefixTab;
    private String countryPrefixChat;

    public ChatManager(BTEConoSurPlugin plugin, OnlineServerPlayer player) throws SQLException {
        this.plugin = plugin;
        this.player = player;

        try (SQLResult result = plugin.getSqlManager().select(
                "chat_managers",
                new SQLColumnSet("*"),
                new SQLANDConditionSet(
                        new SQLOperatorCondition("uuid", "=", player.getUUID())
                )
        ).retrieve()) {

            ResultSet set = result.getResultSet();

            if (!set.next()) {
                plugin.getSqlManager().insert(
                        "chat_managers",
                        new SQLValuesSet(
                                new SQLValue("uuid", player.getUUID()),
                                new SQLValue("default_chat", "global_global"),
                                new SQLValue("hidden", false),
                                new SQLValue("nickname", null),
                                new SQLValue("country_chat_prefix", null),
                                new SQLValue("country_tab_prefix", null)
                        )
                ).execute();
            } else {
                this.hidden = set.getBoolean("hidden");
                this.defaultChat = set.getString("default_chat");
                this.nickname = set.getString("nickname");
                this.countryPrefixChat = set.getString("country_chat_prefix");
                this.countryPrefixTab = set.getString("country_tab_prefix");
            }
        }
    }

    public boolean isHidden() {
        return hidden;
    }

    public Chat getDefaultChat() {
        ChatProvider provider = CHAT_PROVIDERS.get(defaultChat.split("_")[0]);
        return provider.getChat(defaultChat.split("_")[1]);
    }

    public Component getNickname() {
        Style style;
        if (player.getRoles().contains(OfflineServerPlayer.Role.ADMIN)) {
            style = Style.style(GOLD, BOLD);
        } else if (player.getRoles().contains(OfflineServerPlayer.Role.MOD)) {
            style = Style.style(DARK_PURPLE, BOLD);
        } else if (player.getRoles().contains(OfflineServerPlayer.Role.STREAMER)) {
            style = Style.style(GREEN);
        } else if (player.getRoles().contains(OfflineServerPlayer.Role.DONOR)) {
            style = Style.style(LIGHT_PURPLE);
        } else if (nickname != null) {
            style = Style.style(YELLOW);
        } else {
            style = Style.style(WHITE);
        }

        return Component.text(
                nickname != null ? nickname : player.getName(),
                style
        ).hoverEvent(
                Component.join(
                        JoinConfiguration.spaces(),
                        player.getLoreWithTitle()
                )
        );
    }

    public Component getCountryPrefixChat() {
        if (countryPrefixChat == null) {
            return Component.text("[INTERNACIONAL]", GRAY);
        }
        return Component.text(countryPrefixChat);
    }

    public Component getCountryPrefixTab() {
        if (countryPrefixTab == null) {
            return Component.text("[INT]", GRAY);
        }
        return Component.text(countryPrefixTab);
    }

    public Chat getCurrentChat() {
        String provider = currentChat.split("_")[0];
        String id = currentChat.split("_")[1];
        return CHAT_PROVIDERS.get(provider).getChat(id);
    }

    @Override
    public void saveValue(String key, Object value) throws SQLException {

    }
}
