package pizzaaxx.bteconosur.Projects.Listeners;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.util.net.HttpRequest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.requests.restaction.ForumPostAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Projects.Finished.FinishedProject;
import pizzaaxx.bteconosur.SQL.Conditions.SQLANDConditionSet;
import pizzaaxx.bteconosur.SQL.Conditions.SQLOperatorCondition;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Utils.CoordinatesUtils;
import pizzaaxx.bteconosur.Utils.DiscordUtils;
import pizzaaxx.bteconosur.Utils.RegionUtils;
import pizzaaxx.bteconosur.Utils.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProjectFormListener extends ListenerAdapter {

    private final BTEConoSur plugin;

    public ProjectFormListener(BTEConoSur plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {}

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {}
}
