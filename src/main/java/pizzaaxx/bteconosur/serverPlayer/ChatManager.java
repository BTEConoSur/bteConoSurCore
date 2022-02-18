package pizzaaxx.bteconosur.serverPlayer;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.chats.Chat;

public class ChatManager {
    private final DataManager data;
    private final ServerPlayer serverPlayer;

    public ChatManager(ServerPlayer s) {
        data = s.getDataManager();
        serverPlayer = s;
    }

    public Chat getChat() {
        if (data.contains("chat")) {
            return new Chat(data.getString("chat"));
        }
        return new Chat("global");
    }

    public void setChat(@NotNull Chat chat) {
        data.set("chat", chat.getName());
        data.save();
    }

    public Chat getDefaultChat() {
        if (data.contains("defaultChat")) {
            return new Chat(data.getString("defaultChat"));
        }
        return new Chat("global");
    }

    public void setDefaultChat(@NotNull Chat chat) {
        data.set("defaultChat", chat.getName());
        data.save();
    }

    public boolean hasCountryPrefix() {
        return data.contains("countryPrefix");
    }

    public String getCountryPrefix() {
        return data.getString("countryPrefix");
    }

    public void setCountryPrefix(String prefix) {
        data.set("countryPrefix", prefix);
        data.save();
    }

    public String getNick() {
        return data.getString("nickname").replace("&", "§");
    }

    public void setNick(String nick) {
        data.set("nickname", nick.replace("§", "&"));
        data.save();
    }

    public String getDisplayName() {
        String normalName = serverPlayer.getName();
        String nick = getNick();
        return (nick.equals(normalName) ? normalName : nick);
    }
}
