package pizzaaxx.bteconosur.server.player;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.chats.Chat;

import java.util.ArrayList;
import java.util.List;

import static pizzaaxx.bteconosur.BteConoSur.chatRegistry;

public class ChatManager {
    private final DataManager data;
    private final ServerPlayer serverPlayer;
    private boolean hide;

    public ChatManager(ServerPlayer s) {
        data = s.getDataManager();
        hide = data.getBoolean("chat.hidden");
        serverPlayer = s;
    }

    public Chat getChat() {
        if (data.contains("chat.actual")) {
            return chatRegistry.get(data.getString("chat.actual"));
        }
        return chatRegistry.get("global");
    }

    public void setChat(@NotNull String chat) {
        chatRegistry.movePlayer(serverPlayer, chat);
        data.set("chat.actual", chat);
        data.save();
    }

    public Chat getDefaultChat() {
        if (data.contains("chat.default")) {
            return new Chat(data.getString("chat.default"));
        }
        return new Chat("global");
    }

    public void setDefaultChat(@NotNull String chat) {
        data.set("chat.default", chat);
        data.save();
    }

    public boolean hasCountryPrefix() {
        return data.contains("prefix");
    }

    public String getCountryPrefix() {
        return data.getString("prefix");
    }

    public void setCountryPrefix(String prefix) {
        data.set("prefix", prefix);
        data.save();
    }

    public boolean hasNick() {
        return data.contains("nickname");
    }

    public String getNick() {
        return data.getString("nickname").replace("&", "§");
    }

    public void setNick(String nick) {
        if (nick == null || nick.equals(serverPlayer.getName())) {
            data.set("nickname", null);
        } else {
            data.set("nickname", nick.replace("§", "&"));
        }
        data.save();
        ScoreboardManager sManager = serverPlayer.getScoreboardManager();
        if (sManager.getType() == ScoreboardManager.ScoreboardType.ME) {
            serverPlayer.getScoreboardManager().update();
        }
    }

    public String getDisplayName() {
        String normalName = serverPlayer.getName();
        String nick = getNick();
        return (nick.equals(normalName) ? normalName : nick);
    }

    public List<String> getSecondaryPrefixes() {
        List<String> prefixes = new ArrayList<>();
        serverPlayer.getGroupsManager().getSecondaryGroups().forEach(group -> prefixes.add(group.getAsPrefix()));
        return prefixes;
    }

    public String getMainPrefix() {
        return serverPlayer.getGroupsManager().getPrimaryGroup().getAsPrefix();
    }

    public List<String> getAllPrefixes() {
        List<String> prefixes = new ArrayList<>();
        prefixes.add(getMainPrefix());
        prefixes.addAll(getSecondaryPrefixes());
        prefixes.add(getCountryPrefix());
        return prefixes;
    }

    public boolean toggleChat() {
        hide = !hide;
        data.set("chat.hidden", hide);
        data.save();
        return hide;
    }

    public boolean isHidden() {
        return hide;
    }
}
