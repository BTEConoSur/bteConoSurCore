package pizzaaxx.bteconosur.Projects.Actions;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Cities.City;
import pizzaaxx.bteconosur.Player.Managers.ProjectManager;
import pizzaaxx.bteconosur.Player.ServerPlayer;
import pizzaaxx.bteconosur.Projects.Project;
import pizzaaxx.bteconosur.SQL.Columns.SQLColumnSet;
import pizzaaxx.bteconosur.SQL.Conditions.*;
import pizzaaxx.bteconosur.SQL.Values.SQLValue;
import pizzaaxx.bteconosur.SQL.Values.SQLValuesSet;
import pizzaaxx.bteconosur.Utils.StringUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import static pizzaaxx.bteconosur.Utils.StringUtils.LOWER_CASE;

public class ReviewProjectAction {

    public enum ReviewAction {
        DENY, CONTINUE, ACCEPT
    }

    private final BTEConoSur plugin;
    private final ReviewAction action;
    private final Project project;
    private final UUID moderator;

    public ReviewProjectAction(BTEConoSur plugin, ReviewAction action, Project project, UUID moderator) {
        this.plugin = plugin;
        this.action = action;
        this.project = project;
        this.moderator = moderator;
    }

    public void execute() throws SQLException, IOException {
        ServerPlayer moderator = plugin.getPlayerRegistry().get(this.moderator);
        switch (action) {
            case DENY: {

                for (UUID memberUUID : project.getAllMembers()) {
                    ServerPlayer member = plugin.getPlayerRegistry().get(memberUUID);
                    member.sendNotification(
                            "§f[§dPROYECTO§f] §7>> §fTu proyecto §a" + project.getDisplayName() + "§f ha sido rechazado.",
                            "**[PROYECTOS]** » Tu proyecto **" + project.getDisplayName() + "** ha sido rechazado."
                    );
                }
                project.getCountry().getLogsChannel().sendMessage(
                        ":mag_right: **" + moderator.getName() + "** ha rechazado el proyecto `" + project.getId() + "`."
                ).queue();

                project.emptyProject().execute();

                break;
            }
            case CONTINUE: {

                for (UUID memberUUID : project.getAllMembers()) {
                    ServerPlayer member = plugin.getPlayerRegistry().get(memberUUID);
                    member.sendNotification(
                            "§f[§dPROYECTO§f] §7>> §fTu proyecto §a" + project.getDisplayName() + "§f ha sido continuado. Puedes seguir construyendo.",
                            "**[PROYECTOS]** » Tu proyecto **" + project.getDisplayName() + "** ha sido rechazado. Puedes seguir construyendo."
                    );
                }

                project.setPending(false).execute();
                project.getCountry().getLogsChannel().sendMessage(
                        ":mag_right: **" + moderator.getName() + "** ha continuado el proyecto `" + project.getId() + "`."
                ).queue();

                break;

            }
            case ACCEPT: {

                assert project.getPending() != null;

                Date date = new Date(project.getPending());

                String id = StringUtils.generateCode(
                        8,
                        plugin.getFinishedProjectsRegistry().getIds(),
                        LOWER_CASE
                );

                plugin.getSqlManager().insert(
                        "finished_projects",
                        new SQLValuesSet(
                                new SQLValue(
                                        "finished_date", date
                                ),
                                new SQLValue(
                                        "sent_form", false
                                ),
                                new SQLValue(
                                        "id", id
                                ),
                                new SQLValue(
                                        "original_name", project.getDisplayName()
                                ),
                                new SQLValue(
                                        "country", project.getCountry().getName()
                                ),
                                new SQLValue(
                                        "cities", project.getCities()
                                ),
                                new SQLValue(
                                        "type", project.getType().getName()
                                ),
                                new SQLValue(
                                        "points", project.getPoints()
                                ),
                                new SQLValue(
                                        "members", project.getMembers()
                                ),
                                new SQLValue(
                                        "owner", project.getOwner()
                                ),
                                new SQLValue(
                                        "tag", project.getTag()
                                ),
                                new SQLValue(
                                        "region_points", project.getRegion().getPoints()
                                )
                        )
                ).execute();

                project.getCountry().getLogsChannel().sendMessage(
                        ":mag_right: **" + moderator.getName() + "** ha aceptado el proyecto `" + project.getId() + "`."
                ).queue();

                for (UUID uuid : project.getAllMembers()) {

                    ServerPlayer s = plugin.getPlayerRegistry().get(uuid);
                    ProjectManager manager = s.getProjectManager();

                    manager.removeProject(project);

                    double base = project.getPoints();
                    double boost = 0;
                    double futureBoosts = 0;

                    ResultSet pastProjectsSet = plugin.getSqlManager().select(
                            "finished_projects",
                            new SQLColumnSet("COUNT(id) AS total"),
                            new SQLANDConditionSet(
                                    new SQLORConditionSet(
                                            new SQLOperatorCondition(
                                                    "owner", "=", uuid
                                            ),
                                            new SQLJSONArrayCondition(
                                                    "members", uuid
                                            )
                                    ),
                                    new SQLOperatorCondition(
                                            "id", "!=", id
                                    ),
                                    new SQLBetweenCondition(
                                            "finished_date",
                                            new Date(project.getPending() - 604800000),
                                            new Date(project.getPending())
                                    )
                            )
                    ).retrieve();

                    pastProjectsSet.next();
                    double boostPercentage = 0.05 * pastProjectsSet.getInt("total");
                    boost += boostPercentage * project.getPoints();

                    ResultSet futureProjectsSet = plugin.getSqlManager().select(
                            "finished_projects",
                            new SQLColumnSet("points"),
                            new SQLANDConditionSet(
                                    new SQLORConditionSet(
                                            new SQLOperatorCondition(
                                                    "owner", "=", uuid
                                            ),
                                            new SQLJSONArrayCondition(
                                                    "members", uuid
                                            )
                                    ),
                                    new SQLOperatorCondition(
                                            "id", "!=", id
                                    ),
                                    new SQLBetweenCondition(
                                            "finished_date",
                                            new Date(project.getPending()),
                                            new Date(project.getPending() + 604800000)
                                    )
                            )
                    ).retrieve();

                    while (futureProjectsSet.next()) {
                        futureBoosts += futureProjectsSet.getInt("points") * 0.05;
                    }

                    double total = base + boost + futureBoosts;

                    manager.addPoints(project.getCountry(), total);
                    s.sendNotification(
                            project.getPrefix() + "Tu proyecto §a" + project.getDisplayName() + "§f ha sido aceptado.",
                            "**[PROYECTOS]** » Tu proyecto **" + project.getDisplayName() + "** ha sido aceptado."
                    );

                    s.sendNotification(
                            project.getPrefix() + "Has obtenido §a" + (int) total + "§f puntos. §7(Proyecto: " + (int) base + " / Boost (x" + boostPercentage + "): " + boost + (futureBoosts > 0 ? " / Boost futuro: " + futureBoosts : "") + ")",
                            "**[PROYECTOS]** » Has obtenido **" + (int) total + "** puntos. **(Proyecto: " + (int) base + " / Boost (x" + boostPercentage + "): " + boost + (futureBoosts > 0 ? " / Boost futuro: " + futureBoosts : "") + ")**"
                    );
                }

                ServerPlayer owner = plugin.getPlayerRegistry().get(project.getOwner());
                boolean sent;
                if (owner.getDiscordManager().isLinked()) {
                    sent = true;
                    plugin.getBot().retrieveUserById(owner.getDiscordManager().getId()).queue(
                            user -> user.openPrivateChannel().queue(
                                    channel -> channel.sendMessage(
                                            "**[PROYECTOS]** » Felicidades por haber completado el proyecto **" + project.getDisplayName() + "**. Por favor, completa el siguiente formulario para que tu proyecto aparezca en <#" + project.getCountry().getProjectsForumChannelID() + "> y otros lugares."
                                    ).addActionRow(
                                            Button.of(ButtonStyle.SUCCESS, "projectForm?id=" + id, "Formulario", Emoji.fromUnicode("U+1F4D1"))
                                    ).queue()
                            )
                    );
                } else {
                    sent = false;
                }

                plugin.getSqlManager().update(
                        "finished_projects",
                        new SQLValuesSet(
                                new SQLValue(
                                        "sent_form", sent
                                )
                        ),
                        new SQLANDConditionSet(
                                new SQLOperatorCondition(
                                        "id", "=", id
                                )
                        )
                ).execute();

                plugin.getSqlManager().delete(
                        "projects",
                        new SQLANDConditionSet(
                                new SQLOperatorCondition(
                                        "id", "=", project.getId()
                                )
                        )
                ).execute();

                plugin.getRegionManager().removeRegion("project_" + project.getId());

                plugin.getProjectRegistry().unload(project.getId());
                plugin.getProjectRegistry().unregisterID(project.getId());

                break;
            }
        }

    }

}
