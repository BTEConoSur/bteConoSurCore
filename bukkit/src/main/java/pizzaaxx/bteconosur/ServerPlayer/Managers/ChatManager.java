package pizzaaxx.bteconosur.ServerPlayer.Managers;

import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.Chat.ChatException;
import pizzaaxx.bteconosur.Chat.IChat;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class ChatManager {

    private final BteConoSur plugin;
    private final ServerPlayer serverPlayer;
    private final DataManager data;
    private final pizzaaxx.bteconosur.Chat.ChatManager manager;
    private boolean hidden;

    public ChatManager(@NotNull ServerPlayer s, @NotNull BteConoSur plugin) {
        this.plugin = plugin;
        this.manager = plugin.getChatManager();

        this.serverPlayer = s;
        this.data = s.getDataManager();
        this.hidden = data.getBoolean("chat.hidden");

        if (!plugin.getChatManager().exists(data.getString("chat.actual"))) {
            data.set("chat.actual", "global");
        }

        if (!plugin.getChatManager().exists(data.getString("chat.default"))) {
            data.set("chat.default", "global");
        }

    }

    public void setGlobal() {
        data.set("chat.actual", "global");
        data.save();
        plugin.getChatManager().getGlobalChat().receiveMember(this.serverPlayer.getId());
    }

    public IChat getChat() throws ChatException {
        if (!data.contains("chat.actual")) {
            data.set("chat.actual", "global");
            data.save();
        }
        return manager.getChat(data.getString("chat.actual"));
    }

    public void setChat(@NotNull IChat chat) {
        try {
            getChat().sendMember(this.serverPlayer.getId(), chat);
        } catch (ChatException e) {
            chat.receiveMember(this.serverPlayer.getId());
        }
        data.set("chat.actual", chat.getId());
        data.save();

        if (serverPlayer.getScoreboardManager().getType() == ScoreboardManager.ScoreboardType.PLAYER) {
            serverPlayer.getScoreboardManager().update();
        }

    }

    public IChat getDefaultChat() throws ChatException {
        if (!data.contains("chat.default")) {
            data.set("chat.default", "global");
            data.save();
        }
        return plugin.getChatManager().getChat(data.getString("chat.default"));
    }

    public void setDefaultChat(@NotNull IChat chat) {
        data.set("chat.default", chat.getId());
        data.save();

    }

    public boolean hasCountryPrefix() {
        return data.contains("prefix");
    }

    public String getCountryPrefix() {
        if (data.contains("prefix")) {
            return data.getString("prefix");
        }
        return null;
    }

    public void setCountryPrefix(String prefix) {
        data.set("prefix", prefix);
        data.save();
    }

    public boolean hasNick() {
        return data.contains("nickname");
    }

    public String getNick() {
        if (data.contains("nickname")) {
            return data.getString("nickname").replace("&", "§");
        }
        return null;
    }

    public void setNick(String nick) {
        if (nick == null || nick.equals(serverPlayer.getName())) {
            data.set("nickname", null);
        } else {
            data.set("nickname", nick.replace("§", "&"));
        }
        data.save();
        ScoreboardManager sManager = serverPlayer.getScoreboardManager();
        if (sManager.getType() == ScoreboardManager.ScoreboardType.PLAYER) {
            serverPlayer.getScoreboardManager().update();
        }
    }

    public String getDisplayName() {
        String nick = getNick();
        if (nick != null) {
            return nick;
        }
        return serverPlayer.getName();
    }

    public List<String> getSecondaryPrefixes() {
        List<String> prefixes = new ArrayList<>();
        serverPlayer.getGroupsManager().getSecondaryGroups().forEach(group -> prefixes.add(group.getAsPrefix()));
        return prefixes;
    }

    public String getMainPrefix() {
        return serverPlayer.getGroupsManager().getPrimaryGroup().getAsPrefix();
    }

    public boolean toggleChat() {
        hidden = !hidden;
        data.set("chat.hidden", hidden);
        data.save();
        return hidden;
    }

    public boolean isHidden() {
        return hidden;
    }
}
