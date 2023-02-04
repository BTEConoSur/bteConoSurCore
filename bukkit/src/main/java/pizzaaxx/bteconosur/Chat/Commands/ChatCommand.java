package pizzaaxx.bteconosur.Chat.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.*;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Inventory.PaginatedInventoryGUI;
import pizzaaxx.bteconosur.Player.Managers.ChatManager;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.Projects.RegionSelectors.MemberProjectSelector;
import pizzaaxx.bteconosur.Utils.StringUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pizzaaxx.bteconosur.Utils.StringUtils.LOWER_CASE;

public class ChatCommand implements CommandExecutor, Prefixable {

    private final BTEConoSur plugin;

    private final Map<String, String> invites = new HashMap<>();

    public ChatCommand(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;
        ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());
        ChatManager chatManager = s.getChatManager();

        if (args.length < 1) {
            try {
                Chat chat = chatManager.getCurrentChat();
                p.sendMessage(getPrefix() + "Tu chat actual es §a" + chat.getDisplayName() + "§f.");
            } catch (SQLException e) {
                p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                return true;
            }
        } else {
            ChatHandler handler = plugin.getChatHandler();
            try {

                if (args[0].equals("invite")) {

                    if (args.length < 2) {
                        p.sendMessage(getPrefix() + "Introduce un jugador.");
                        return true;
                    }

                    Player target = plugin.getOnlinePlayer(args[1]);

                    if (target == null) {
                        p.sendMessage(getPrefix() + "No se ha encontrado al jugador.");
                        return true;
                    }

                    ChatManager targetChatManager = plugin.getPlayerRegistry().get(target.getUniqueId()).getChatManager();

                    if (targetChatManager.getCurrentChatName().equals(chatManager.getCurrentChatName())) {
                        p.sendMessage(getPrefix() + "§a" + target.getName() + "§f ya está en tu chat.");
                        return true;
                    }

                    String code = StringUtils.generateCode(6, invites.keySet(), LOWER_CASE);
                    invites.put(code, chatManager.getCurrentChatName());

                    BukkitRunnable runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            invites.remove(code);
                        }
                    };
                    runnable.runTaskLaterAsynchronously(plugin, 12000);

                    p.sendMessage(getPrefix() + "Invitación enviada a §a" + target.getName() + "§f.");
                    target.sendMessage(getPrefix() + "§a" + p.getName() + "§f te ha invitado al chat " + chatManager.getCurrentChat().getDisplayName() + "§f. Usa el comando §a/c " + code + "§f para unirte.");

                    return true;
                }

                if (args[0].equals("toggle")) {
                    if (chatManager.toggleHidden()) {
                        p.sendMessage(getPrefix() + "Has ocultado el chat.");
                    } else {
                        p.sendMessage(getPrefix() + "Ahora puedes ver el chat.");
                    }
                    return true;
                }

                if (args[0].equals("default")) {

                    if (args.length < 2) {

                        if (chatManager.getCurrentChatName().equals(chatManager.getDefaultChatName())) {
                            p.sendMessage(getPrefix() + "Ya estás en tu chat por defecto.");
                            return true;
                        }

                        Chat oldChat = chatManager.getCurrentChat();
                        oldChat.removePlayer(p.getUniqueId());

                        Chat defaultChat = chatManager.getDefaultChat();
                        chatManager.setCurrentChat(defaultChat);
                        defaultChat.addPlayer(p.getUniqueId());
                        p.sendMessage(getPrefix() + "Te has unido a tu chat por defecto: §a" + defaultChat.getDisplayName() + "§f.");
                    } else {
                        if (args[1].equals("global")) {

                            if (chatManager.getCurrentChatName().equals("global")) {
                                p.sendMessage(getPrefix() + "Tu chat por defecto ya es el chat §aGlobal§f.");
                                return true;
                            }

                            chatManager.setDefaultChat(handler.getChat("global"));
                            p.sendMessage(getPrefix() + "Has establecido tu chat por defecto en el chat §aGlobal§f.");
                            return true;
                        }

                        Country country = plugin.getCountryManager().get(args[1]);

                        if (country != null) {

                            if (chatManager.getCurrentChatName().equals(country.getName())) {
                                p.sendMessage(getPrefix() + "Tu chat por defecto ya es el chat de §a" + country.getDisplayName() + "§f.");
                                return true;
                            }

                            chatManager.setDefaultChat(handler.getChat(country.getName()));
                            p.sendMessage(getPrefix() + "Has establecido tu chat por defecto en el chat de §a" + country.getDisplayName() + "§f.");
                            return true;
                        }

                        if (args[1].equals("project") || args[1].equals("proyecto")) {
                            List<String> projectIDs = plugin.getProjectRegistry().getProjectsAt(p.getLocation(), new MemberProjectSelector(p.getUniqueId()));

                            if (projectIDs.size() == 0) {
                                p.sendMessage(getPrefix() + "No hay proyectos de los que seas miembro en este lugar.");
                            } else if (projectIDs.size() == 1) {
                                String projectID = projectIDs.get(0);

                                if (chatManager.getCurrentChatName().equals("project_" + projectID)) {
                                    p.sendMessage(getPrefix() + "Tu chat por defecto ya es el chat de este proyecto.");
                                    return true;
                                }

                                Project project = plugin.getProjectRegistry().get(projectID);
                                if (!handler.isLoaded("project_" + projectID)) {
                                    handler.registerChat(new ProjectChat(project, handler));
                                }

                                Chat chat = handler.getChat("project_" + projectID);
                                chatManager.setDefaultChat(chat);
                                p.sendMessage(getPrefix() + "Has establecido tu chat por defecto en chat del proyecto §a" + project.getDisplayName() + "§f.");
                                return true;
                            } else {
                                PaginatedInventoryGUI gui = new PaginatedInventoryGUI(
                                        6,
                                        "Elige un proyecto"
                                );
                                for (String id : projectIDs) {
                                    Project project = plugin.getProjectRegistry().get(id);
                                    gui.add(
                                            project.getItem(),
                                            event -> {
                                                event.closeGUI();
                                                try {
                                                    if (chatManager.getCurrentChatName().equals("project_" + id)) {
                                                        p.sendMessage(getPrefix() + "Tu chat por defecto ya es el chat de este proyecto.");
                                                        return;
                                                    }

                                                    if (!handler.isLoaded("project_" + id)) {
                                                        handler.registerChat(new ProjectChat(project, handler));
                                                    }

                                                    Chat chat = handler.getChat("project_" + id);
                                                    chatManager.setDefaultChat(chat);
                                                    p.sendMessage(getPrefix() + "Has establecido tu chat por defecto en chat del proyecto §a" + project.getDisplayName() + "§f.");
                                                } catch (SQLException e) {
                                                    p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                                                }
                                            },
                                            null,
                                            null,
                                            null
                                    );
                                }
                                gui.openTo(p, plugin);
                            }
                            return true;
                        }

                        p.sendMessage(getPrefix() + "Introduce un chat válido.");
                    }
                    return true;
                }

                if (args[0].equals("global")) {

                    if (chatManager.getCurrentChatName().equals("global")) {
                        p.sendMessage(getPrefix() + "Ya estás en el chat §aGlobal§f.");
                        return true;
                    }

                    Chat oldChat = chatManager.getCurrentChat();
                    oldChat.removePlayer(p.getUniqueId());

                    Chat chat = handler.getChat("global");
                    chatManager.setCurrentChat(chat);
                    chat.addPlayer(p.getUniqueId());
                    p.sendMessage(getPrefix() + "Te has unido al chat §aGlobal§f.");
                    return true;
                }

                Country country = plugin.getCountryManager().get(args[0]);

                if (country != null) {

                    if (chatManager.getCurrentChatName().equals(country.getName())) {
                        p.sendMessage(getPrefix() + "Ya estás en el chat de §a" + country.getDisplayName() + "§f.");
                        return true;
                    }

                    Chat oldChat = chatManager.getCurrentChat();
                    oldChat.removePlayer(p.getUniqueId());

                    Chat chat = handler.getChat(country.getName());
                    chatManager.setCurrentChat(chat);
                    chat.addPlayer(p.getUniqueId());
                    p.sendMessage(getPrefix() + "Te has unido al chat de §a" + country.getDisplayName() + "§f.");
                    return true;
                }

                if (args[0].equals("project") || args[0].equals("proyecto")) {
                    List<String> projectIDs = plugin.getProjectRegistry().getProjectsAt(p.getLocation(), new MemberProjectSelector(p.getUniqueId()));

                    if (projectIDs.size() == 0) {
                        p.sendMessage(getPrefix() + "No hay proyectos de los que seas miembro en este lugar.");
                    } else if (projectIDs.size() == 1) {
                        String projectID = projectIDs.get(0);

                        if (chatManager.getCurrentChatName().equals("project_" + projectID)) {
                            p.sendMessage(getPrefix() + "Ya estás en el chat de este proyecto.");
                            return true;
                        }

                        Project project = plugin.getProjectRegistry().get(projectID);
                        if (!handler.isLoaded("project_" + projectID)) {
                            handler.registerChat(new ProjectChat(project, handler));
                        }

                        Chat oldChat = chatManager.getCurrentChat();
                        oldChat.removePlayer(p.getUniqueId());

                        Chat chat = handler.getChat("project_" + projectID);
                        chatManager.setCurrentChat(chat);
                        chat.addPlayer(p.getUniqueId());
                        p.sendMessage(getPrefix() + "Te has unido al chat del proyecto §a" + project.getDisplayName() + "§f.");
                        return true;
                    } else {
                        PaginatedInventoryGUI gui = new PaginatedInventoryGUI(
                                6,
                                "Elige un proyecto"
                        );
                        for (String id : projectIDs) {
                            Project project = plugin.getProjectRegistry().get(id);
                            gui.add(
                                    project.getItem(),
                                    event -> {
                                        event.closeGUI();
                                        try {
                                            if (chatManager.getCurrentChatName().equals("project_" + id)) {
                                                p.sendMessage(getPrefix() + "Ya estás en el chat de este proyecto.");
                                                return;
                                            }

                                            if (!handler.isLoaded("project_" + id)) {
                                                handler.registerChat(new ProjectChat(project, handler));
                                            }

                                            Chat oldChat = chatManager.getCurrentChat();
                                            oldChat.removePlayer(p.getUniqueId());

                                            Chat chat = handler.getChat("project_" + id);
                                            chatManager.setCurrentChat(chat);
                                            chat.addPlayer(p.getUniqueId());
                                            p.sendMessage(getPrefix() + "Te has unido al chat del proyecto §a" + project.getDisplayName() + "§f.");
                                        } catch (SQLException e) {
                                            p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                                        }
                                    },
                                    null,
                                    null,
                                    null
                            );
                        }
                        gui.openTo(p, plugin);
                    }
                    return true;
                }

                if (args[0].matches("[a-z]{6}")) { // TODO TEST INVITES
                    if (!invites.containsKey(args[0])) {
                        p.sendMessage(getPrefix() + "La invitación introducida no existe.");
                        return true;
                    }

                    String targetChatName = invites.get(args[0]);

                    if (!handler.isLoaded(targetChatName)) {
                        if (targetChatName.startsWith("project_")) {
                            String projectID = targetChatName.replace("project_", "");
                            if (plugin.getProjectRegistry().exists(projectID)) {
                                Project project = plugin.getProjectRegistry().get(projectID);
                                handler.registerChat(new ProjectChat(project, handler));
                            } else {
                                p.sendMessage(getPrefix() + "El chat de la invitación ya no está disponible.");
                                return true;
                            }
                        } else {
                            p.sendMessage(getPrefix() + "El chat de la invitación ya no está disponible.");
                            return true;
                        }
                    }

                    Chat oldChat = chatManager.getCurrentChat();
                    oldChat.removePlayer(p.getUniqueId());

                    Chat targetChat = handler.getChat(targetChatName);
                    chatManager.setCurrentChat(targetChat);
                    targetChat.addPlayer(p.getUniqueId());

                    p.sendMessage(getPrefix() + "Te has unido al chat §a" + targetChat.getDisplayName() + "§f.");
                    return true;
                }

                Player target = plugin.getOnlinePlayer(args[0]);
                if (target != null) {
                    ChatManager targetChatManager = plugin.getPlayerRegistry().get(target.getUniqueId()).getChatManager();
                    try {
                        Chat chat = targetChatManager.getCurrentChat();
                        p.sendMessage(getPrefix() + "El chat actual de §a" + target.getName() + "§f es §a" + chat.getDisplayName() + "§f.");
                    } catch (SQLException e) {
                        p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                        return true;
                    }
                }
            } catch (SQLException e) {
                p.sendMessage(getPrefix() + "Ha ocurrido un error en la base de datos.");
                return true;
            }
        }
        return true;
    }

    @Override
    public String getPrefix() {
        return "§f[§aCHAT§f] §7>> §f";
    }
}
