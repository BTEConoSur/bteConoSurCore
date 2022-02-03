package pizzaaxx.bteconosur.country;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pizzaaxx.bteconosur.ServerPlayer;
import pizzaaxx.bteconosur.chats.Chat;
import pizzaaxx.bteconosur.yaml.YamlManager;

import javax.xml.soap.Text;
import java.util.*;

import static pizzaaxx.bteconosur.Config.*;
import static pizzaaxx.bteconosur.Config.logsPe;
import static pizzaaxx.bteconosur.bteConoSur.mainWorld;
import static pizzaaxx.bteconosur.bteConoSur.pluginFolder;
import static pizzaaxx.bteconosur.worldguard.worldguard.getWorldGuard;

public class Country {
    private String country;

    public static List<String> countryRegionNames = Arrays.asList("argentina", "bolivia", "chile_idp", "chile_cont", "paraguay", "peru", "uruguay");

    // CONSTRUCTOR
    public Country(String country) {
        if (country.matches("[a-z]{2}")) {
            if (country.equals("ar")) {
                this.country = "argentina";
            }
            if (country.equals("bo")) {
                this.country = "bolivia";
            }
            if (country.equals("cl")) {
                this.country = "chile";
            }
            if (country.equals("py")) {
                this.country = "paraguay";
            }
            if (country.equals("pe")) {
                this.country = "peru";
            }
            if (country.equals("uy")) {
                this.country = "uruguay";
            }
            if (country.equals("gl")) {
                this.country = "global";
            }
        } else {
            this.country = country;
        }
    }

    public Country(Location loc) {
        RegionManager regionManager = getWorldGuard().getRegionManager(mainWorld);
        Set<ProtectedRegion> regions = regionManager.getApplicableRegions(loc).getRegions();
        if (regions.contains(regionManager.getRegion("argentina"))) {
            this.country = "argentina";
        } else if (regions.contains(regionManager.getRegion("bolivia"))) {
            this.country = "bolivia";
        } else if (regions.contains(regionManager.getRegion("chile_cont")) || regions.contains(regionManager.getRegion("chile_idp"))) {
            this.country = "chile";
        } else if (regions.contains(regionManager.getRegion("paraguay"))) {
            this.country = "paraguay";
        } else if (regions.contains(regionManager.getRegion("peru"))) {
            this.country = "peru";
        } else if (regions.contains(regionManager.getRegion("uruguay"))) {
            this.country = "uruguay";
        } else {
            this.country = "global";
        }
    }

    public Country(BlockVector2D vector) {
        Location location = new Location(mainWorld, vector.getX(), 100.0, vector.getZ());
        RegionManager regionManager = getWorldGuard().getRegionManager(mainWorld);
        Set<ProtectedRegion> regions = regionManager.getApplicableRegions(location).getRegions();
        if (regions.contains(regionManager.getRegion("argentina"))) {
            this.country = "argentina";
        } else if (regions.contains(regionManager.getRegion("bolivia"))) {
            this.country = "bolivia";
        } else if (regions.contains(regionManager.getRegion("chile_cont")) || regions.contains(regionManager.getRegion("chile_idp"))) {
            this.country = "chile";
        } else if (regions.contains(regionManager.getRegion("paraguay"))) {
            this.country = "paraguay";
        } else if (regions.contains(regionManager.getRegion("peru"))) {
            this.country = "peru";
        } else if (regions.contains(regionManager.getRegion("uruguay"))) {
            this.country = "uruguay";
        } else {
            this.country = "global";
        }
    }

    // GETTERS

    public String getCountry() {
        return this.country;
    }

    public Chat getChat() {
        return new Chat(this.country);
    }

    public Set<Player> getPlayers() {
        Set<Player> players = new HashSet<>();
        RegionManager regionManager = getWorldGuard().getRegionManager(mainWorld);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Set<ProtectedRegion> regions = regionManager.getApplicableRegions(player.getLocation()).getRegions();
            if (this.country.equals("chile") && (regions.contains(regionManager.getRegion("chile_cont")) || regions.contains(regionManager.getRegion("chile_idp")))) {
                players.add(player);
            } else if (regions.contains(regionManager.getRegion(this.country))) {
                players.add(player);
            }
        }
        return players;
    }

    public TextChannel getLogs() {
        if (this.country.equals("argentina")) {
            return logsAr;
        }
        if (this.country.equals("bolivia")) {
            return logsBo;
        }
        if (this.country.equals("chile")) {
            return logsCl;
        }
        if (this.country.equals("peru")) {
            return logsPe;
        }
        return null;
    }

    public TextChannel getRequests() {
        if (this.country.equals("argentina")) {
            return requestsAr;
        }
        if (this.country.equals("bolivia")) {
            return requestsBo;
        }
        if (this.country.equals("chile")) {
            return requestsCl;
        }
        if (this.country.equals("peru")) {
            return requestsPe;
        }
        return null;
    }

    public String getAbbreviation() {
        if (this.country.equals("argentina")) {
            return "ar";
        }
        if (this.country.equals("bolivia")) {
            return "bo";
        }
        if (this.country.equals("chile")) {
            return "cl";
        }
        if (this.country.equals("paraguay")) {
            return "py";
        }
        if (this.country.equals("peru")) {
            return "pe";
        }
        if (this.country.equals("uruguay")) {
            return "uy";
        }
        return null;
    }

    public List<ServerPlayer> getScoreboard() {
        List<ServerPlayer> scoreboard = new ArrayList<>();

        YamlManager yaml = new YamlManager(pluginFolder, "points/max.yml");
        for (String uuid : (List<String>) yaml.getList(getAbbreviation() + "_max")) {
            scoreboard.add(new ServerPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid))));
        }

        return scoreboard;
    }

    public String getPrefix(Boolean abb) {
        if (abb) {
            if (this.country.equals("argentina")) {
                return "§b[§fA§eR§fG§b]§r";
            }
            if (this.country.equals("bolivia")) {
                return "§c[B§eO§2L]§r";
            }
            if (this.country.equals("chile")) {
                return "§9[C§fH§cI]§r";
            }
            if (this.country.equals("paraguay")) {
                return "§4[§fPGY§9]§r";
            }
            if (this.country.equals("peru")) {
                return "§4[PER§4]§r";
            }
            if (this.country.equals("uruguay")) {
                return "§e[§fU§9R§fY§9]§r";
            }
            if (this.country.equals("global")) {
                return "§7[INT]§r";
            }
        } else {
            if (this.country.equals("argentina")) {
                return "§b[AR§fGE§eN§fTI§bNA]§r";
            }
            if (this.country.equals("bolivia")) {
                return "§4[BO§eLIV§2IA]§r";
            }
            if (this.country.equals("chile")) {
                return "§9[C§fHIL§cE]§r";
            }
            if (this.country.equals("paraguay")) {
                return "§4[P§fAR§7AG§fUA§1Y]§r";
            }
            if (this.country.equals("peru")) {
                return "§4[P§fER§4Ú]§r";
            }
            if (this.country.equals("uruguay")) {
                return "§f[§eU§fR§9U§fG§9U§fA§9Y§f]§r";
            }
            if (this.country.equals("global")) {
                return "§7[INTERNACIONAL]§r";
            }
        }
        return null;
    }
}
