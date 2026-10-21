/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import xaero.hud.controls.key.KeyMappingControllerManager;
import xaero.hud.minimap.controls.key.function.MinimapKeyMappingFunctions;

public class MinimapKeyMappings {
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register((Identifier)Identifier.fromNamespaceAndPath((String)"xaerominimap", (String)"controls"));
    public static final KeyMapping ZOOM_IN = new KeyMapping("gui.xaero_zoom_in", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping ZOOM_OUT = new KeyMapping("gui.xaero_zoom_out", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping ADD_WAYPOINT = new KeyMapping("gui.xaero_new_waypoint", 5, CATEGORY);
    public static final KeyMapping WAYPOINT_MENU = new KeyMapping("gui.xaero_waypoints_key", 24, CATEGORY);
    public static final KeyMapping ENLARGE_MAP = new KeyMapping("gui.xaero_enlarge_map", 29, CATEGORY);
    public static final KeyMapping TOGGLE_MAP = new KeyMapping("gui.xaero_toggle_map", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping TOGGLE_WORLD_WAYPOINTS = new KeyMapping("gui.xaero_toggle_waypoints", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping TOGGLE_MAP_WAYPOINTS = new KeyMapping("gui.xaero_toggle_map_waypoints", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping TOGGLE_SLIME_CHUNKS = new KeyMapping("gui.xaero_toggle_slime", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping TOGGLE_GRID = new KeyMapping("gui.xaero_toggle_grid", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping TEMPORARY_WAYPOINT = new KeyMapping("gui.xaero_instant_waypoint", 87, CATEGORY);
    public static final KeyMapping SWITCH_WAYPOINT_SET = new KeyMapping("gui.xaero_switch_waypoint_set", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping RENDER_ALL_SETS = new KeyMapping("gui.xaero_display_all_sets", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping LIGHT_OVERLAY = new KeyMapping("gui.xaero_toggle_light_overlay", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping TOGGLE_RADAR = new KeyMapping("gui.xaero_toggle_entity_radar", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping REVERSE_ENTITY_RADAR = new KeyMapping("gui.xaero_reverse_entity_radar", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping MANUAL_CAVE_MODE = new KeyMapping("gui.xaero_toggle_manual_cave_mode", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping ALTERNATIVE_LIST_PLAYERS = new KeyMapping("gui.xaero_alternative_list_players", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping TOGGLE_TRACKED_PLAYERS_MAP = new KeyMapping("gui.xaero_toggle_tracked_players_on_map", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping TOGGLE_TRACKED_PLAYERS_WORLD = new KeyMapping("gui.xaero_toggle_tracked_players_in_world", InputConstants.UNKNOWN.getValue(), CATEGORY);
    public static final KeyMapping TOGGLE_OPAC_CLAIMS = new KeyMapping("gui.xaero_toggle_pac_chunk_claims", InputConstants.UNKNOWN.getValue(), CATEGORY);

    public static void registerAll(KeyMappingControllerManager controllerManager, Consumer<KeyMapping> registry, Consumer<KeyMapping.Category> categoryRegistry) {
        categoryRegistry.accept(CATEGORY);
        controllerManager.registerController(ZOOM_IN, true, registry);
        controllerManager.registerController(ZOOM_OUT, true, registry);
        controllerManager.registerController(ADD_WAYPOINT, true, registry);
        controllerManager.registerController(WAYPOINT_MENU, true, registry);
        controllerManager.registerController(ENLARGE_MAP, true, registry);
        controllerManager.registerController(TOGGLE_MAP, true, registry);
        controllerManager.registerController(TOGGLE_WORLD_WAYPOINTS, true, registry);
        controllerManager.registerController(TOGGLE_MAP_WAYPOINTS, true, registry);
        controllerManager.registerController(TOGGLE_SLIME_CHUNKS, true, registry);
        controllerManager.registerController(TOGGLE_GRID, true, registry);
        controllerManager.registerController(TEMPORARY_WAYPOINT, true, registry);
        controllerManager.registerController(SWITCH_WAYPOINT_SET, true, registry);
        controllerManager.registerController(RENDER_ALL_SETS, true, registry);
        controllerManager.registerController(LIGHT_OVERLAY, true, registry);
        controllerManager.registerController(TOGGLE_RADAR, true, registry);
        controllerManager.registerController(REVERSE_ENTITY_RADAR, true, registry);
        controllerManager.registerController(MANUAL_CAVE_MODE, true, registry);
        controllerManager.registerController(ALTERNATIVE_LIST_PLAYERS, true, registry);
        controllerManager.registerController(TOGGLE_TRACKED_PLAYERS_MAP, true, registry);
        controllerManager.registerController(TOGGLE_TRACKED_PLAYERS_WORLD, true, registry);
        controllerManager.registerController(TOGGLE_OPAC_CLAIMS, true, registry);
        MinimapKeyMappingFunctions.registerAll(controllerManager);
    }
}

