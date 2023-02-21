package pizzaaxx.bteconosur.Projects.Listeners;

import com.sk89q.worldedit.BlockVector2D;
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
import pizzaaxx.bteconosur.Utils.CoordinatesUtils;
import pizzaaxx.bteconosur.Utils.DiscordUtils;
import pizzaaxx.bteconosur.Utils.RegionUtils;
import pizzaaxx.bteconosur.Utils.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {

        if (event.getButton().getId() == null) {
            return;
        }

        if (event.getButton().getId().startsWith("projectForm")) {

            Map<String, String> values = StringUtils.getQuery(event.getButton().getId().replace("projectForm?", ""));
            String id = values.get("id");

            FinishedProject project = plugin.getFinishedProjectsRegistry().get(id);

            Modal modal = Modal.create(
                    "projectForm?id=" + id,
                    "Publicar proyecto " + project.getOriginalName()
            )
                    .addActionRows(
                            ActionRow.of(
                                    TextInput.create(
                                            "name",
                                            "Nombre del proyecto",
                                            TextInputStyle.SHORT
                                    ).setRequired(true).setPlaceholder("Un nombre que represente al proyecto").build()
                            ),
                            ActionRow.of(
                                    TextInput.create(
                                            "description",
                                            "Descripción del proyecto",
                                            TextInputStyle.PARAGRAPH
                                    ).setRequired(true).setMaxLength(1000).build()
                            ),
                            ActionRow.of(
                                    TextInput.create(
                                            "imageURL",
                                            "Imagen de portada",
                                            TextInputStyle.SHORT
                                    ).setRequired(false).setPlaceholder("Imagen de portada de la publicación. Se mostrará un mapa si no se introduce.").build()
                            )
                    )
                    .build();

            event.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {

        if (event.getModalId().startsWith("projectForm")) {

            Map<String, String> values = StringUtils.getQuery(event.getModalId().replace("projectForm?", ""));
            String id = values.get("id");

            FinishedProject project = plugin.getFinishedProjectsRegistry().get(id);

            ModalMapping nameMapping = event.getValue("name");
            assert nameMapping != null;
            String name = nameMapping.getAsString();

            ModalMapping descriptionMapping = event.getValue("description");
            assert descriptionMapping != null;
            String description = descriptionMapping.getAsString();

            ModalMapping imageMapping = event.getValue("description");
            assert imageMapping != null;
            String image = imageMapping.getAsString();

            List<String> cityNames = new ArrayList<>();
            for (String cityName : project.getCities()) {
                cityNames.add(plugin.getCityManager().getDisplayName(cityName));
            }

            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(project.getType().getColor());
            embed.addField(":crown: Líder:", plugin.getPlayerRegistry().get(project.getOwner()).getName(), false);
            if (!project.getMembers().isEmpty()) {
                List<String> names = new ArrayList<>();
                for (UUID memberUUID : project.getMembers()) {
                    names.add(plugin.getPlayerRegistry().get(memberUUID).getName().replace("_", "\\_"));
                }
                embed.addField(":busts_in_silhouette: Miembros:", String.join(", ", names), false);
            }
            embed.addField(":game_die: Tipo:", project.getType().getDisplayName() + " (" + project.getPoints() + " puntos)", true);

            BlockVector2D coord = RegionUtils.getAveragePoint(project.getRegionPoints());
            Location loc = CoordinatesUtils.blockVector2DtoLocation(plugin, coord);

            embed.addField(":round_pushpin: Coordenadas:", loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ(), true);

            embed.addField(":speech_balloon: Descripción:", description, false);

            ForumPostAction action = project.getCountry().getProjectsForumChannel().createForumPost(
                    name + (cityNames.isEmpty() ? "" : " - " + String.join(", ", cityNames)),
                    MessageCreateData.fromEmbeds(
                            embed.build()
                    )
            );

            if (!image.equals("")) {
                try {
                    URL url = new URL(image);
                    action.addFiles(
                            FileUpload.fromData(url.openStream(), "image")
                    );
                } catch (IOException e) {
                    DiscordUtils.respondError(event, "Introduce una URL válida.");
                }
            } else {

            }

        }

    }
}
