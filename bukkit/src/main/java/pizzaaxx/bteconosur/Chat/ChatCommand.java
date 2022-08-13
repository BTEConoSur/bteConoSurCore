package pizzaaxx.bteconosur.Chat;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BteConoSur;
import pizzaaxx.bteconosur.ServerPlayer.ChatManager;
import pizzaaxx.bteconosur.ServerPlayer.ServerPlayer;
import pizzaaxx.bteconosur.country.cities.projects.Project;
import pizzaaxx.bteconosur.country.cities.projects.ProjectSelector.MemberProjectSelector;
import pizzaaxx.bteconosur.country.cities.projects.ProjectSelector.NoProjectsFoundException;
import pizzaaxx.bteconosur.methods.CodeGenerator;

import java.util.HashMap;
import java.util.Map;

import static pizzaaxx.bteconosur.Chat.IChat.CHAT_PREFIX;

public class ChatCommand implements CommandExecutor {

    private final BteConoSur plugin;
    private final pizzaaxx.bteconosur.Chat.ChatManager chatManager;
    private final Map<String, String> chatInvites = new HashMap<>();

    public ChatCommand(@NotNull BteConoSur plugin) {
        this.plugin = plugin;
        this.chatManager = plugin.getChatManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;
        ServerPlayer s = plugin.getPlayerRegistry().get(p.getUniqueId());
        ChatManager sChatManager = s.getChatManager();
        try {
            IChat pChat = sChatManager.getChat();

            if (args.length == 0) {
                p.sendMessage(CHAT_PREFIX + "Estás en el chat §a" + pChat.getDisplayName() + "§f.");
            } else {
                if (args[0].equalsIgnoreCase("argentina") || args[0].equalsIgnoreCase("bolivia") || args[0].equalsIgnoreCase("chile") || args[0].equalsIgnoreCase("paraguay") || args[0].equalsIgnoreCase("peru") || args[0].equalsIgnoreCase("uruguay")) {
                    if (!pChat.getId().equals("country_" + args[0])) {
                        try {
                            sChatManager.setChat(plugin.getChatManager().getChat("country_" + args[0]));
                        } catch (ChatException e) {
                            p.sendMessage("Ha ocurrido un error. Has vuelto al chat global.");
                            sChatManager.setGlobal();
                        }
                    } else {
                        p.sendMessage(CHAT_PREFIX + "Ya estás en este chat.");
                    }
                } else if (args[0].equalsIgnoreCase("global")) {
                    if (!pChat.getId().equals("global")) {
                        sChatManager.setChat(chatManager.getGlobalChat());
                    }
                } else if (Bukkit.getOfflinePlayer(args[0]).isOnline()) {

                    ServerPlayer target = plugin.getPlayerRegistry().get(Bukkit.getPlayerUniqueId(args[0]));
                    p.sendMessage(CHAT_PREFIX + "§a" + target.getName() + "§f está en el chat §a" + target.getChatManager().getChat().getDisplayName() + "§f.");

                } else if (args[0].equalsIgnoreCase("project") || args[0].equalsIgnoreCase("proyecto")) {
                    try {
                        Project project = plugin.getProjectsManager().getProjectAt(p.getLocation(), new MemberProjectSelector(p.getUniqueId(), plugin));

                        if (!pChat.getId().equals("project_" + project.getId())) {
                            sChatManager.setChat(plugin.getChatManager().getChat(project));
                        } else {
                            p.sendMessage(CHAT_PREFIX + "Ya estás en este chat.");
                        }
                    } catch (NoProjectsFoundException exception) {
                        p.sendMessage(CHAT_PREFIX + "No estás dentro de ningún proyecto del que seas miembro.");
                    }
                } else if (args[0].equals("toggle") || args[0].equals("alternar")) {
                    if (sChatManager.toggleChat()) {
                        p.sendMessage(CHAT_PREFIX + "Has ocultado el chat.");
                    } else {
                        p.sendMessage(CHAT_PREFIX + "Ahora puedes ver el chat.");
                    }
                } else if (args[0].equals("default") || args[0].equals("predeterminado")) {
                    try {
                        IChat defaultChat = sChatManager.getDefaultChat();

                        if (args.length > 1) {

                            if (args[1].equalsIgnoreCase("argentina") || args[1].equalsIgnoreCase("bolivia") || args[1].equalsIgnoreCase("chile") || args[1].equalsIgnoreCase("paraguay") || args[1].equalsIgnoreCase("peru") || args[1].equalsIgnoreCase("uruguay")) {
                                if (!defaultChat.getId().equals("country_" + args[1])) {
                                    try {
                                        sChatManager.setDefaultChat(plugin.getChatManager().getChat("country_" + args[1]));
                                        p.sendMessage(CHAT_PREFIX + "Chat predeterminado establecido en el chat de §a" + args[1].toUpperCase() + "§f.");
                                    } catch (ChatException e) {
                                        p.sendMessage("Ha ocurrido un error al cargar el chat.");
                                    }
                                } else {
                                    p.sendMessage(CHAT_PREFIX + "Este ya es tu chat predeterminado.");
                                }
                            } else if (args[1].equalsIgnoreCase("global")) {
                                if (!defaultChat.getId().equals("global")) {
                                    sChatManager.setDefaultChat(chatManager.getGlobalChat());
                                    p.sendMessage(CHAT_PREFIX + "Chat predeterminado establecido en el chat §aGLOBAL§f.");
                                } else {
                                    p.sendMessage(CHAT_PREFIX + "Este ya es tu chat predeterminado.");
                                }
                            } else if (args[1].equalsIgnoreCase("project") || args[1].equals("proyecto")) {
                                try {
                                    Project project = plugin.getProjectsManager().getProjectAt(p.getLocation(), new MemberProjectSelector(p.getUniqueId(), plugin));

                                    if (!defaultChat.getId().equals("project_" + project.getId())) {
                                        sChatManager.setDefaultChat(project.getChat());

                                        p.sendMessage(CHAT_PREFIX + "Chat predeterminado establecido en el chat del proyecto §a" + project.getName() + "§f.");
                                    } else {
                                        p.sendMessage(CHAT_PREFIX + "Este ya es tu chat predeterminado.");
                                    }
                                } catch (NoProjectsFoundException exception) {
                                    p.sendMessage(CHAT_PREFIX + "No estás dentro de ningún proyecto del que seas miembro. Solo puedes establecer tu chat predeterminado al chat de proyectos de los que eres miembro.");
                                }
                            }

                        } else {
                            if (!pChat.getId().equals(defaultChat.getId())) {
                                sChatManager.setChat(defaultChat);
                            } else {
                                p.sendMessage(CHAT_PREFIX + "Ya estás en tu chat predeterminado.");
                            }
                        }
                    } catch (ChatException e) {
                        p.sendMessage(CHAT_PREFIX + "Tu chat predeterminado ya no está disponible. Lo hemos establecido de vuelta al chat global.");
                        sChatManager.setDefaultChat(chatManager.getGlobalChat());
                    }
                } else if (args[0].equals("invite") || args[0].equals("invitar")) {
                    if (args.length > 1) {
                        if (Bukkit.getOfflinePlayer(args[1]).isOnline()) {
                            Player target = Bukkit.getPlayer(args[1]);

                            ServerPlayer t = plugin.getPlayerRegistry().get(target.getUniqueId());

                            if (t.getChatManager().getChat().getId().equals(pChat.getId())) {
                                p.sendMessage(CHAT_PREFIX + "El jugador ya se encuentra en tu chat.");
                                return true;
                            }

                            String code = CodeGenerator.generateCode(6, chatInvites.keySet());

                            chatInvites.put(code, pChat.getId());

                            p.sendMessage(CHAT_PREFIX + "Has invitado a §a" + t.getName() + "§f a tu chat.");

                            target.sendMessage(CHAT_PREFIX + "§a" + s.getName() + "§f te ha invitado a su chat (" + pChat.getDisplayName() + "). Usa §a/chat " + code + "§f para unirte.");
                        } else {
                            p.sendMessage(CHAT_PREFIX + "El jugador no está online.");
                        }
                    } else {
                        p.sendMessage(CHAT_PREFIX + "Introduce un jugador a invitar.");
                    }
                } else {
                    if (args[0].matches("[a-z]{6}") && chatInvites.containsKey(args[0])) {

                        try {
                            IChat target = plugin.getChatManager().getChat(chatInvites.get(args[0]));

                            if (!pChat.getId().equals(target.getId())) {
                                sChatManager.setChat(target);
                            } else {
                                p.sendMessage(CHAT_PREFIX + "Ya estás en este chat.");
                            }
                        } catch (ChatException e) {
                            p.sendMessage(CHAT_PREFIX + "El chat de la invitación ya no está disponible.");
                        }
                    } else {
                        p.sendMessage(CHAT_PREFIX + "Código de invitación inválido.");
                    }
                }
            }

        } catch (ChatException e) {
            if (e.getType() == ChatException.Type.IdNotFound) {
                p.sendMessage(CHAT_PREFIX + "No se ha encontrado tu chat actual. Te hemos devuelto al chat global.");
            }
        }
        return true;
    }
}
