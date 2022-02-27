package pizzaaxx.bteconosur.chats;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.country.Country;
import pizzaaxx.bteconosur.serverPlayer.ChatManager;
import pizzaaxx.bteconosur.serverPlayer.PointsManager;
import pizzaaxx.bteconosur.serverPlayer.ServerPlayer;
import pizzaaxx.bteconosur.player.data.PlayerData;
import pizzaaxx.bteconosur.projects.Project;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static pizzaaxx.bteconosur.chats.Command.chatsPrefix;

public class Chat {

    private final String name;
    private final Set<UUID> membersUUID = new HashSet<>();

    public Chat(String name) {
        this.name = name;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (new PlayerData(player).getData("chat").equals(this.name)) {
                membersUUID.add(player.getUniqueId());
            }
        }
    }

    public String toString() {
        return name;
    }

    public int getMembersAmount() {
        return membersUUID.size();
    }

    public void removeMember(Player player) {
        if (membersUUID.contains(player.getUniqueId())) {
            membersUUID.remove(player.getUniqueId());
            String name = new ServerPlayer(player).getName();
            broadcast(chatsPrefix + "§a" + name + "§f ha abandonado el chat.");
        }

    }

    // GETTER

    public Set<Player> getMembers() {
        Set<Player> members = new HashSet<>();
        membersUUID.forEach(uuid -> members.add(Bukkit.getPlayer(uuid)));
        return members;
    }

    public String getName() {
        return name;
    }

    public String getFormattedName() {
        if (this.name.equals("argentina") || this.name.equals("bolivia") || this.name.equals("chile") || this.name.equals("paraguay") || this.name.equals("peru") || this.name.equals("uruguay") || this.name.equals("global")) {
            return this.name.toUpperCase();
        } else if (this.name.startsWith("project_")) {
            try {
                Project project = new Project(this.name.replace("project_", ""));

                return "Proyecto " + project.getName(true);
            } catch (Exception exception) {
                return null;
            }
        }
        return null;
    }

    public boolean equals(Chat chat) {
        return (getName().equals(chat.getName()));
    }

    public void addMember(Player player) {
        if (!membersUUID.contains(player.getUniqueId())) {
            String name = new ServerPlayer(player).getName();
            broadcast(chatsPrefix + "§a" + name + "§f se ha unido al chat.");
            membersUUID.add(player.getUniqueId());
        }

    }

    // [CHAT] CHILE >> [ADMIN] [CHILE] <PIZZAAXX> TEST

    public void sendMessage(String message, ServerPlayer serverPlayer) {
        ChatManager cManager = serverPlayer.getChatManager();
        PointsManager pManager = serverPlayer.getPointsManager();
        Country country = null;
        String cName = getName();
        if (!cName.equals("global")) {
            if (cName.startsWith("project_")) {
                try {
                    Project project = new Project(cName.replace("project_", ""));
                    country = project.getCountry();
                } catch (Exception e) {
                    Bukkit.getConsoleSender().sendMessage("No se pudo encontrar el proyecto " + cName.replace("project_", "").toUpperCase());
                    return;
                }
            } else {
                country = new Country(cName);
            }
        }
        Chat pChat = cManager.getChat();
        String msg = (!this.equals(pChat) ? chatsPrefix + " " + pChat.getFormattedName() + " §r>> " : "" ) + String.join(" ", cManager.getAllPrefixes()) + " " + (country != null && !country.getCountry().equals("argentina") ? PointsManager.BuilderRank.getFrom(pManager.getPoints(country)) : "") + "§r <" + cManager.getDisplayName() + "§r> " + message;
        membersUUID.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            ServerPlayer s = new ServerPlayer(player);
            if (!s.getChatManager().isHidden()) {
                player.sendMessage(msg);
            }
        });
    }

    public void broadcast(String message) {
        membersUUID.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            ServerPlayer s = new ServerPlayer(player);
            if (!s.getChatManager().isHidden()) {
                player.sendMessage(message);
            }
        });
    }
}
