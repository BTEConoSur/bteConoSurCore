package pizzaaxx.bteconosur.chats;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.playerData.PlayerData;
import pizzaaxx.bteconosur.projects.Project;

import java.util.HashSet;
import java.util.Set;

import static pizzaaxx.bteconosur.chats.command.chatsPrefix;

public class Chat {

    private String name;
    private Set<Player> members = new HashSet<>();

    public static String sectionLine = "§7>+--------------+[-< ~ >-]+--------------+<";

    public Chat(String name) {
        this.name = name;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (new PlayerData(player).getData("chat").equals(this.name)) {
                members.add(player);
            }
        }
    }

    // GETTER

    public Set<Player> getMembers() {
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

    // SETTER

    public void addPlayer(Player player) {
        if (!(this.members.contains(player))) {
            for (Player memberOld : new ServerPlayer(player).getChat().getMembers()) {
                if (memberOld != player) {
                    memberOld.sendMessage(chatsPrefix + "§a" + new ServerPlayer(player).getName() + "§f ha abandonado el chat.");
                }
            }

            for (Player member : this.members) {
                member.sendMessage(chatsPrefix + "§a" + new ServerPlayer(player).getName() + "§f se ha unido al chat.");
            }

            members.add(player);
        }
    }

    // SENDER

    public void sendMessage(String message) {
        for (Player member : this.members) {
            if (!(new ServerPlayer(member).isChatHidden())) {
                member.sendMessage(message);
            }
        }
    }
}
