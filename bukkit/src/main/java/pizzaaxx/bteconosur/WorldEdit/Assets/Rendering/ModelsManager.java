package pizzaaxx.bteconosur.WorldEdit.Assets.Rendering;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import org.jetbrains.annotations.NotNull;
import pizzaaxx.bteconosur.BTEConoSur;
import pizzaaxx.bteconosur.Utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelsManager {

    private final BTEConoSur plugin;
    private final RenderableModel[][] normal = new RenderableModel[253][16];

    // ID DATA NORTH EAST SOUTH WEST
    private final RenderableModel[][][][][][] fences = new RenderableModel[11][16][2][2][2][2];

    private final List<Integer> fencesIDs = Arrays.asList(85, 113, 188, 189, 190, 191, 192, 101, 102, 139, 160);

    // ID HALF DIRECTION SHAPE
    private final RenderableModel[][][][] stairs = new RenderableModel[14][2][4][5];

    private final List<Integer> stairsIDs = Arrays.asList(53, 67, 108, 109, 114, 128, 134, 135, 136, 156, 163, 164, 180, 203);

    public ModelsManager(@NotNull BTEConoSur plugin) {
        this.plugin = plugin;
    }

    public void init() throws IOException {
        File file = new File(plugin.getDataFolder(), "rendering/legacy.json");

        Map<String, String> legacy = plugin.getJSONMapper().readValue(file, HashMap.class);

        for (int id = 0; id < 253; id++) {
            for (int data = 0; data < 16; data++) {
                if (legacy.containsKey(id + ":" + data)) {
                    String modelPath = legacy.get(id + ":" + data);
                    if (modelPath.startsWith("fence=") || modelPath.startsWith("stairs=")) {
                        continue;
                    }

                    String[] models = modelPath.split("&");
                    RenderableModel renderableModel = new RenderableModel(plugin);
                    for (String model : models) {

                        if (model.contains("?")) {
                            String modelString = model.split("\\?")[0];
                            String queryString = model.split("\\?")[1];
                            Map<String, String> query = StringUtils.getQuery(queryString);

                            File modelFile = new File(plugin.getDataFolder(), "rendering/models/block/" + modelString + ".json");

                            if (query.containsKey("x")) {
                                renderableModel.load(modelFile, Double.parseDouble(query.get("x")), 0, 0);
                            } else if (query.containsKey("y")) {
                                renderableModel.load(modelFile, 0, Double.parseDouble(query.get("y")), 0);
                            } else if (query.containsKey("z")) {
                                renderableModel.load(modelFile, 0, 0, Double.parseDouble(query.get("z")));
                            } else {
                                renderableModel.load(modelFile, 0, 0, 0);
                            }
                        } else {
                            File modelFile = new File(plugin.getDataFolder(), "rendering/models/block/" + model + ".json");
                            renderableModel.load(modelFile, 0, 0, 0);
                        }
                    }
                    this.normal[id][data] = renderableModel;
                }
            }
        }

        String[][][][][][] fences = new String[11][16][2][2][2][2];
        {
            int counter = 0;
            for (String wood : new String[] {"oak", "nether_brick", "spruce", "birch", "jungle", "dark_oak", "acacia"}) {
                for (int data = 0; data < 16; data++) {
                    fences[counter][data][0][0][0][0] = "oak_fence_post".replace("oak", wood);
                    fences[counter][data][0][0][0][1] = "oak_fence_post&oak_fence_side?y=270".replace("oak", wood);
                    fences[counter][data][0][0][1][0] = "oak_fence_post&oak_fence_side?y=180".replace("oak", wood);
                    fences[counter][data][0][0][1][1] = "oak_fence_post&oak_fence_side?y=270&oak_fence_side?y=180".replace("oak", wood);
                    fences[counter][data][0][1][0][0] = "oak_fence_post&oak_fence_side?y=90".replace("oak", wood);
                    fences[counter][data][0][1][0][1] = "oak_fence_post&oak_fence_side?y=270&oak_fence_side?y=90".replace("oak", wood);
                    fences[counter][data][0][1][1][0] = "oak_fence_post&oak_fence_side?y=180&oak_fence_side?y=90".replace("oak", wood);
                    fences[counter][data][0][1][1][1] = "oak_fence_post&oak_fence_side?y=270&oak_fence_side?y=180&oak_fence_side?y=90".replace("oak", wood);
                    fences[counter][data][1][0][0][0] = "oak_fence_post&oak_fence_side".replace("oak", wood);
                    fences[counter][data][1][0][0][1] = "oak_fence_post&oak_fence_side?y=270&oak_fence_side".replace("oak", wood);
                    fences[counter][data][1][0][1][0] = "oak_fence_post&oak_fence_side?y=180&oak_fence_side".replace("oak", wood);
                    fences[counter][data][1][0][1][1] = "oak_fence_post&oak_fence_side?y=270&oak_fence_side?y=180&oak_fence_side".replace("oak", wood);
                    fences[counter][data][1][1][0][0] = "oak_fence_post&oak_fence_side?y=90&oak_fence_side".replace("oak", wood);
                    fences[counter][data][1][1][0][1] = "oak_fence_post&oak_fence_side?y=270&oak_fence_side?y=90&oak_fence_side".replace("oak", wood);
                    fences[counter][data][1][1][1][0] = "oak_fence_post&oak_fence_side?y=180&oak_fence_side?y=90&oak_fence_side".replace("oak", wood);
                    fences[counter][data][1][1][1][1] = "oak_fence_post&oak_fence_side?y=270&oak_fence_side?y=180&oak_fence_side?y=90&oak_fence_side".replace("oak", wood);
                }
                counter++;
            }

            for (int data = 0; data < 16; data++) {
                fences[7][data][0][0][0][0] = "iron_bars_post_ends&iron_bars_post";
                fences[7][data][0][0][0][1] = "iron_bars_post_ends&iron_bars_side_alt?y=90&iron_bars_cap_alt?y=90";
                fences[7][data][0][0][1][0] = "iron_bars_post_ends&iron_bars_side_alt&iron_bars_cap_alt";
                fences[7][data][0][0][1][1] = "iron_bars_post_ends&iron_bars_side_alt?y=90&iron_bars_side_alt";
                fences[7][data][0][1][0][0] = "iron_bars_post_ends&iron_bars_side?y=90&iron_bars_cap?y=90";
                fences[7][data][0][1][0][1] = "iron_bars_post_ends&iron_bars_side_alt?y=90&iron_bars_side?y=90";
                fences[7][data][0][1][1][0] = "iron_bars_post_ends&iron_bars_side_alt&iron_bars_side?y=90";
                fences[7][data][0][1][1][1] = "iron_bars_post_ends&iron_bars_side_alt?y=90&iron_bars_side_alt&iron_bars_side?y=90";
                fences[7][data][1][0][0][0] = "iron_bars_post_ends&iron_bars_side&iron_bars_cap";
                fences[7][data][1][0][0][1] = "iron_bars_post_ends&iron_bars_side_alt?y=90&iron_bars_side";
                fences[7][data][1][0][1][0] = "iron_bars_post_ends&iron_bars_side_alt&iron_bars_side";
                fences[7][data][1][0][1][1] = "iron_bars_post_ends&iron_bars_side_alt?y=90&iron_bars_side_alt&iron_bars_side";
                fences[7][data][1][1][0][0] = "iron_bars_post_ends&iron_bars_side?y=90&iron_bars_side";
                fences[7][data][1][1][0][1] = "iron_bars_post_ends&iron_bars_side_alt?y=90&iron_bars_side?y=90&iron_bars_side";
                fences[7][data][1][1][1][0] = "iron_bars_post_ends&iron_bars_side_alt&iron_bars_side?y=90&iron_bars_side";
                fences[7][data][1][1][1][1] = "iron_bars_post_ends&iron_bars_side_alt?y=90&iron_bars_side_alt&iron_bars_side?y=90&iron_bars_side";
            }

            for (int data = 0; data < 16; data++) {
                fences[8][data][0][0][0][0] = "";
                fences[8][data][0][0][0][1] = "";
                fences[8][data][0][0][1][0] = "";
                fences[8][data][0][0][1][1] = "";
                fences[8][data][0][1][0][0] = "";
                fences[8][data][0][1][0][1] = "";
                fences[8][data][0][1][1][0] = "";
                fences[8][data][0][1][1][1] = "";
                fences[8][data][1][0][0][0] = "";
                fences[8][data][1][0][0][1] = "";
                fences[8][data][1][0][1][0] = "";
                fences[8][data][1][0][1][1] = "";
                fences[8][data][1][1][0][0] = "";
                fences[8][data][1][1][0][1] = "";
                fences[8][data][1][1][1][0] = "";
                fences[8][data][1][1][1][1] = "";
            }
        }

        for (int a = 0; a < 11; a++) {
            for (int b = 0; b < 16; b++) {
                for (int c = 0; c < 2; c++) {
                    for (int d = 0; d < 2; d++) {
                        for (int e = 0; e < 2; e++) {
                            for (int f = 0; f < 2; f++) {
                                String modelPath = fences[a][b][c][d][e][f];

                                if (modelPath == null) {
                                    continue;
                                }

                                String[] models = modelPath.split("&");
                                RenderableModel renderableModel = new RenderableModel(plugin);
                                for (String model : models) {

                                    if (model.contains("?")) {
                                        String modelString = model.split("\\?")[0];
                                        String queryString = model.split("\\?")[1];
                                        Map<String, String> query = StringUtils.getQuery(queryString);

                                        File modelFile = new File(plugin.getDataFolder(), "rendering/models/block/" + modelString + ".json");

                                        if (query.containsKey("x")) {
                                            renderableModel.load(modelFile, Double.parseDouble(query.get("x")), 0, 0);
                                        } else if (query.containsKey("y")) {
                                            renderableModel.load(modelFile, 0, Double.parseDouble(query.get("y")), 0);
                                        } else if (query.containsKey("z")) {
                                            renderableModel.load(modelFile, 0, 0, Double.parseDouble(query.get("z")));
                                        } else {
                                            renderableModel.load(modelFile, 0, 0, 0);
                                        }
                                    } else {
                                        File modelFile = new File(plugin.getDataFolder(), "rendering/models/block/" + model + ".json");
                                        renderableModel.load(modelFile, 0, 0, 0);
                                    }
                                }
                                this.fences[a][b][c][d][e][f] = renderableModel;
                            }
                        }
                    }
                }
            }
        }

        String[][][][] stairs = new String[14][2][4][5];

        for (int a = 0; a < 14; a++) {
            for (int b = 0; b < 14; b++) {
                for (int c = 0; c < 14; c++) {
                    for (int d = 0; d < 14; d++) {
                        {
                            String modelPath = stairs[a][b][c][d];

                            if (modelPath == null) {
                                continue;
                            }

                            String[] models = modelPath.split("&");
                            RenderableModel renderableModel = new RenderableModel(plugin);
                            for (String model : models) {

                                if (model.contains("?")) {
                                    String modelString = model.split("\\?")[0];
                                    String queryString = model.split("\\?")[1];
                                    Map<String, String> query = StringUtils.getQuery(queryString);

                                    File modelFile = new File(plugin.getDataFolder(), "rendering/models/block/" + modelString + ".json");

                                    if (query.containsKey("x")) {
                                        renderableModel.load(modelFile, Double.parseDouble(query.get("x")), 0, 0);
                                    } else if (query.containsKey("y")) {
                                        renderableModel.load(modelFile, 0, Double.parseDouble(query.get("y")), 0);
                                    } else if (query.containsKey("z")) {
                                        renderableModel.load(modelFile, 0, 0, Double.parseDouble(query.get("z")));
                                    } else {
                                        renderableModel.load(modelFile, 0, 0, 0);
                                    }
                                } else {
                                    File modelFile = new File(plugin.getDataFolder(), "rendering/models/block/" + model + ".json");
                                    renderableModel.load(modelFile, 0, 0, 0);
                                }
                            }
                            this.stairs[a][b][c][d] = renderableModel;
                        }
                    }
                }
            }
        }
    }

    public RenderableModel[][][] getClipboard(@NotNull Clipboard clipboard) {

        Vector dimensions = clipboard.getDimensions();
        RenderableModel[][][] result = new RenderableModel[dimensions.getBlockX()][dimensions.getBlockY()][dimensions.getBlockZ()];



    }
}
