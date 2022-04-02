package pizzaaxx.bteconosur.chats;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.country.OldCountry;
import pizzaaxx.bteconosur.server.player.ChatManager;
import pizzaaxx.bteconosur.server.player.PointsManager;
import pizzaaxx.bteconosur.server.player.ServerPlayer;
import pizzaaxx.bteconosur.projects.Project;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static pizzaaxx.bteconosur.BteConoSur.chatRegistry;
import static pizzaaxx.bteconosur.chats.ChatCommand.CHAT_PREFIX;

public class Chat {

    private final String name;
    private Set<UUID> membersUUID = new HashSet<>();

    public Chat(String name) {
        this.name = name;
        if (chatRegistry.contains(name)) {
            this.membersUUID = chatRegistry.get(name).membersUUID;
        } else {
            chatRegistry.register(this);
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
            chatRegistry.register(this);
            String name = new ServerPlayer(player).getName();
            broadcast(CHAT_PREFIX + "§a" + name + "§f ha abandonado el chat.");
        }

    }

    // GETTER

    public Set<Player> getMembers() {
        Set<Player> members = new HashSet<>();

        for (UUID id : membersUUID) {
            members.add(Bukkit.getPlayer(id));
        }

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
            String name = new ServerPlayer(player).getName();
            broadcast(CHAT_PREFIX + "§a" + name + "§f se ha unido al chat.");
            membersUUID.add(player.getUniqueId());
            chatRegistry.register(this);
    }

    public void sendMessage(String message, ServerPlayer serverPlayer) {
        ChatManager cManager = serverPlayer.getChatManager();
        PointsManager pManager = serverPlayer.getPointsManager();
        OldCountry country = null;
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
                country = new OldCountry(cName);
            }
        }
        Chat pChat = cManager.getChat();
        String msg = (!this.equals(pChat) ? CHAT_PREFIX + " " + pChat.getFormattedName() + " §r>> " : "") + String.join(" ", cManager.getAllPrefixes()) + " " + (country != null && !country.getName().equals("argentina") ? PointsManager.BuilderRank.getFrom(pManager.getPoints(country)) : "") + "§r <" + cManager.getDisplayName() + "§r> " + message;
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
