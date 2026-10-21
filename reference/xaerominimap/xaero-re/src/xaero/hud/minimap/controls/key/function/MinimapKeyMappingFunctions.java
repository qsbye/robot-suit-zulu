/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.controls.key.function;

import xaero.hud.controls.key.KeyMappingControllerManager;
import xaero.hud.controls.key.function.KeyMappingFunction;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.controls.key.MinimapKeyMappings;
import xaero.hud.minimap.controls.key.function.AddWaypointFunction;
import xaero.hud.minimap.controls.key.function.HeldEnlargeMapFunction;
import xaero.hud.minimap.controls.key.function.LightOverlayFunction;
import xaero.hud.minimap.controls.key.function.ManualCaveModeFunction;
import xaero.hud.minimap.controls.key.function.OpacClaimsFunction;
import xaero.hud.minimap.controls.key.function.SwitchWaypointSetFunction;
import xaero.hud.minimap.controls.key.function.TemporaryWaypointFunction;
import xaero.hud.minimap.controls.key.function.ToggleGridFunction;
import xaero.hud.minimap.controls.key.function.ToggleMapFunction;
import xaero.hud.minimap.controls.key.function.ToggleSettingFunction;
import xaero.hud.minimap.controls.key.function.ToggleSlimeChunksFunction;
import xaero.hud.minimap.controls.key.function.ToggledEnlargeMapFunction;
import xaero.hud.minimap.controls.key.function.WaypointMenuFunction;
import xaero.hud.minimap.controls.key.function.ZoomFunction;

public class MinimapKeyMappingFunctions {
    public static final KeyMappingFunction ZOOM_IN = new ZoomFunction(true);
    public static final KeyMappingFunction ZOOM_OUT = new ZoomFunction(false);
    public static final KeyMappingFunction TOGGLE_RADAR = new ToggleSettingFunction(() -> MinimapProfiledConfigOptions.DISPLAY_RADAR);
    public static final KeyMappingFunction TOGGLE_TRACKED_PLAYER_MAP = new ToggleSettingFunction(() -> MinimapProfiledConfigOptions.TRACKED_PLAYERS_ON_MINIMAP);
    public static final KeyMappingFunction TOGGLE_TRACKED_PLAYER_WORLD = new ToggleSettingFunction(() -> MinimapProfiledConfigOptions.TRACKED_PLAYERS_IN_WORLD);
    public static final KeyMappingFunction ADD_WAYPOINT = new AddWaypointFunction();
    public static final KeyMappingFunction WAYPOINT_MENU = new WaypointMenuFunction();
    public static final KeyMappingFunction HELD_ENLARGE_MAP = new HeldEnlargeMapFunction();
    public static final KeyMappingFunction TOGGLED_ENLARGE_MAP = new ToggledEnlargeMapFunction();
    public static final KeyMappingFunction TOGGLE_MAP = new ToggleMapFunction();
    public static final KeyMappingFunction TOGGLE_WORLD_WAYPOINTS = new ToggleSettingFunction(() -> MinimapProfiledConfigOptions.WAYPOINTS_IN_WORLD);
    public static final KeyMappingFunction TOGGLE_MAP_WAYPOINTS = new ToggleSettingFunction(() -> MinimapProfiledConfigOptions.WAYPOINTS_ON_MINIMAP);
    public static final KeyMappingFunction TOGGLE_SLIME_CHUNKS = new ToggleSlimeChunksFunction();
    public static final KeyMappingFunction TOGGLE_GRID = new ToggleGridFunction();
    public static final KeyMappingFunction TEMPORARY_WAYPOINT = new TemporaryWaypointFunction();
    public static final KeyMappingFunction SWITCH_WAYPOINT_SET = new SwitchWaypointSetFunction();
    public static final KeyMappingFunction RENDER_ALL_SETS = new ToggleSettingFunction(() -> MinimapProfiledConfigOptions.WAYPOINTS_ALL_SETS);
    public static final KeyMappingFunction LIGHT_OVERLAY = new LightOverlayFunction();
    public static final KeyMappingFunction MANUAL_CAVE_MODE = new ManualCaveModeFunction();
    public static final KeyMappingFunction TOGGLE_OPAC_CLAIMS = new OpacClaimsFunction();

    public static void registerAll(KeyMappingControllerManager controllerManager) {
        controllerManager.registerFunction(MinimapKeyMappings.ZOOM_IN, ZOOM_IN);
        controllerManager.registerFunction(MinimapKeyMappings.ZOOM_OUT, ZOOM_OUT);
        controllerManager.registerFunction(MinimapKeyMappings.TOGGLE_RADAR, TOGGLE_RADAR);
        controllerManager.registerFunction(MinimapKeyMappings.TOGGLE_TRACKED_PLAYERS_MAP, TOGGLE_TRACKED_PLAYER_MAP);
        controllerManager.registerFunction(MinimapKeyMappings.TOGGLE_TRACKED_PLAYERS_WORLD, TOGGLE_TRACKED_PLAYER_WORLD);
        controllerManager.registerFunction(MinimapKeyMappings.ADD_WAYPOINT, ADD_WAYPOINT);
        controllerManager.registerFunction(MinimapKeyMappings.WAYPOINT_MENU, WAYPOINT_MENU);
        controllerManager.registerFunction(MinimapKeyMappings.ENLARGE_MAP, HELD_ENLARGE_MAP);
        controllerManager.registerFunction(MinimapKeyMappings.ENLARGE_MAP, TOGGLED_ENLARGE_MAP);
        controllerManager.registerFunction(MinimapKeyMappings.TOGGLE_MAP, TOGGLE_MAP);
        controllerManager.registerFunction(MinimapKeyMappings.TOGGLE_MAP_WAYPOINTS, TOGGLE_MAP_WAYPOINTS);
        controllerManager.registerFunction(MinimapKeyMappings.TOGGLE_WORLD_WAYPOINTS, TOGGLE_WORLD_WAYPOINTS);
        controllerManager.registerFunction(MinimapKeyMappings.TOGGLE_SLIME_CHUNKS, TOGGLE_SLIME_CHUNKS);
        controllerManager.registerFunction(MinimapKeyMappings.TOGGLE_GRID, TOGGLE_GRID);
        controllerManager.registerFunction(MinimapKeyMappings.TEMPORARY_WAYPOINT, TEMPORARY_WAYPOINT);
        controllerManager.registerFunction(MinimapKeyMappings.SWITCH_WAYPOINT_SET, SWITCH_WAYPOINT_SET);
        controllerManager.registerFunction(MinimapKeyMappings.RENDER_ALL_SETS, RENDER_ALL_SETS);
        controllerManager.registerFunction(MinimapKeyMappings.LIGHT_OVERLAY, LIGHT_OVERLAY);
        controllerManager.registerFunction(MinimapKeyMappings.MANUAL_CAVE_MODE, MANUAL_CAVE_MODE);
        controllerManager.registerFunction(MinimapKeyMappings.TOGGLE_OPAC_CLAIMS, TOGGLE_OPAC_CLAIMS);
    }
}

