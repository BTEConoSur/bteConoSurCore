package pizzaaxx.bteconosur.Events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Chat.Chat;
import pizzaaxx.bteconosur.Countries.Country;
import pizzaaxx.bteconosur.Player.Managers.ScoreboardManager;
import pizzaaxx.bteconosur.Player.ServerPlayer;

import java.awt.*;
import java.sql.SQLException;

public class JoinEvent implements Listener {

    private final BTEConoSur plugin;

    public JoinEvent(BTEConoSur bteConoSur) {
        this.plugin = bteConoSur;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        plugin.getPlayerRegistry().load(event.getPlayer().getUniqueId());
        ServerPlayer serverPlayer = plugin.getPlayerRegistry().get(event.getPlayer().getUniqueId());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setAuthor(plugin.getPlayerRegistry().get(event.getPlayer().getUniqueId()).getName() + " ha entrado al servidor.", null,
                "https://cravatar.eu/helmavatar/" + event.getPlayer().getUniqueId() + "/190.png");
        MessageEmbed embed = embedBuilder.build();
        for (Country country : plugin.getCountryManager().getAllCountries()) {
            country.getGlobalChatChannel().sendMessageEmbeds(embed).queue();
        }
        try {
            Chat defaultChat = serverPlayer.getChatManager().getDefaultChat();
            serverPlayer.getChatManager().setCurrentChat(defaultChat);
            defaultChat.addPlayer(event.getPlayer().getUniqueId());
        } catch (SQLException e) {
            plugin.error("Error loading chat: " + serverPlayer.getChatManager().getCurrentChatName());
        }

        ScoreboardManager scoreboardManager = serverPlayer.getScoreboardManager();
        scoreboardManager.loadDisplay(); // <- If player was loaded before joining.
        if (scoreboardManager.isAuto()) {
            plugin.getScoreboardHandler().registerAuto(serverPlayer.getUUID());
        }
        try {
            scoreboardManager.setDisplay(scoreboardManager.getDisplay());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!serverPlayer.getChatManager().hasCountryPrefix()) {
            try {
                plugin.getConfigCommand().openConfigMenu(event.getPlayer());
            } catch (SQLException ignored) {}
        }
    }
}
