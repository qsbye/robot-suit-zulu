/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.info;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.info.InfoDisplay;
import xaero.hud.minimap.info.widget.InfoDisplayCommonWidgetFactories;
import xaero.hud.minimap.info.widget.InfoDisplayCycleWidgetFactory;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.MinimapWorldManager;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.value.io.serialization.BuiltInConfigValueIOCodecs;
import xaero.lib.common.config.option.value.io.serialization.ConfigValueIOCodec;

public class BuiltInInfoDisplays {
    private static List<InfoDisplay<?>> ALL = new ArrayList();
    public static final InfoDisplay<Boolean> COORDINATES = InfoDisplay.Builder.begin().setId("coords").setName((Component)Component.translatable((String)"gui.xaero_infodisplay_coords")).setDefaultState(true).setCodec((ConfigValueIOCodec<Boolean>)BuiltInConfigValueIOCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.OFF_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getEffectiveState()).booleanValue()) {
            return;
        }
        String coords = playerPos.getX() + ", " + playerPos.getY() + ", " + playerPos.getZ();
        if (Minecraft.getInstance().font.width(coords) >= availableWidth) {
            String stringLevel = "" + playerPos.getY();
            coords = playerPos.getX() + ", " + playerPos.getZ();
            compiler.addLine(coords);
            compiler.addLine(stringLevel);
        } else {
            compiler.addLine(coords);
        }
    }).setLegacyGetter(legacySettings -> {
        Boolean result = legacySettings.showCoordsLegacy;
        legacySettings.showCoordsLegacy = null;
        return result;
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> OVERWORLD_COORDINATES = InfoDisplay.Builder.begin().setId("overworld_coords").setName((Component)Component.translatable((String)"gui.xaero_infodisplay_overworld_coords")).setDefaultState(false).setCodec((ConfigValueIOCodec<Boolean>)BuiltInConfigValueIOCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.OFF_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getEffectiveState()).booleanValue()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        double coordinateScale = mc.level.dimensionType().coordinateScale();
        if (coordinateScale == 1.0) {
            return;
        }
        int overworldPlayerX = Mth.floor((double)((double)playerPos.getX() * coordinateScale));
        int overworldPlayerZ = Mth.floor((double)((double)playerPos.getZ() * coordinateScale));
        String coords = "Xo: " + overworldPlayerX + ", Zo: " + overworldPlayerZ;
        compiler.addWords(coords);
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> CHUNK_COORDINATES = InfoDisplay.Builder.begin().setId("chunk_coords").setName((Component)Component.translatable((String)"gui.xaero_infodisplay_chunk_coords")).setDefaultState(false).setCodec((ConfigValueIOCodec<Boolean>)BuiltInConfigValueIOCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.OFF_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getEffectiveState()).booleanValue()) {
            return;
        }
        int chunkX = playerPos.getX() >> 4;
        int chunkZ = playerPos.getZ() >> 4;
        int insideX = playerPos.getX() & 0xF;
        int insideZ = playerPos.getZ() & 0xF;
        String coords = "C " + chunkX + ", " + chunkZ + " (" + insideX + "; " + insideZ + ")";
        compiler.addWords(coords);
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> ANGLES = InfoDisplay.Builder.begin().setId("angles").setName((Component)Component.translatable((String)"gui.xaero_infodisplay_angles")).setDefaultState(false).setCodec((ConfigValueIOCodec<Boolean>)BuiltInConfigValueIOCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.OFF_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getEffectiveState()).booleanValue()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        compiler.addLine((Component)Component.literal((String)String.format("%.1f / %.1f", Float.valueOf(Mth.wrapDegrees((float)mc.getCameraEntity().getYRot())), Float.valueOf(Mth.wrapDegrees((float)mc.getCameraEntity().getXRot())))));
    }).setLegacyGetter(legacySettings -> {
        Boolean result = legacySettings.showAnglesLegacy;
        legacySettings.showAnglesLegacy = null;
        return result;
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> DIMENSION = InfoDisplay.Builder.begin().setId("dimension").setName((Component)Component.translatable((String)"gui.xaero_infodisplay_dimension")).setDefaultState(false).setCodec((ConfigValueIOCodec<Boolean>)BuiltInConfigValueIOCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.OFF_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getEffectiveState()).booleanValue()) {
            return;
        }
        Identifier dimensionId = Minecraft.getInstance().level.dimension().identifier();
        if (dimensionId != null) {
            String dimensionName = dimensionId.getNamespace().equals("minecraft") ? dimensionId.getPath() : dimensionId.toString();
            compiler.addLine(dimensionName);
        }
    }).setLegacyGetter(legacySettings -> {
        Boolean result = legacySettings.showDimensionNameLegacy;
        legacySettings.showDimensionNameLegacy = null;
        return result;
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> BIOME = InfoDisplay.Builder.begin().setId("biome").setName((Component)Component.translatable((String)"gui.xaero_infodisplay_biome")).setDefaultState(false).setCodec((ConfigValueIOCodec<Boolean>)BuiltInConfigValueIOCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.OFF_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getEffectiveState()).booleanValue()) {
            return;
        }
        Holder biomeHolder = Minecraft.getInstance().level.getBiome(playerPos);
        Biome biome = biomeHolder == null ? null : (Biome)biomeHolder.value();
        Identifier biomeRL = biome == null ? null : Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.BIOME).getKey((Object)biome);
        String biomeText = biomeRL == null ? I18n.get((String)"gui.xaero_unknown_biome", (Object[])new Object[0]) : I18n.get((String)("biome." + biomeRL.getNamespace() + "." + biomeRL.getPath()), (Object[])new Object[0]);
        compiler.addWords(biomeText);
    }).setLegacyGetter(legacySettings -> {
        Boolean result = legacySettings.showBiomeLegacy;
        legacySettings.showBiomeLegacy = null;
        return result;
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> WEATHER = InfoDisplay.Builder.begin().setId("weather").setName((Component)Component.translatable((String)"gui.xaero_infodisplay_weather")).setDefaultState(false).setCodec((ConfigValueIOCodec<Boolean>)BuiltInConfigValueIOCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.OFF_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getEffectiveState()).booleanValue()) {
            return;
        }
        if (Minecraft.getInstance().level.isThundering()) {
            compiler.addLine((Component)Component.translatable((String)"gui.xaero_weather_thundering"));
        } else if (Minecraft.getInstance().level.isRaining()) {
            compiler.addLine((Component)Component.translatable((String)"gui.xaero_weather_raining"));
        }
    }).setLegacyGetter(legacySettings -> {
        Boolean result = legacySettings.displayWeatherInfoLegacy;
        legacySettings.displayWeatherInfoLegacy = null;
        return result;
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Integer> LIGHT_LEVEL = InfoDisplay.Builder.begin().setId("light_level").setName((Component)Component.translatable((String)"gui.xaero_infodisplay_light_level")).setDefaultState(0).setCodec((ConfigValueIOCodec<Integer>)BuiltInConfigValueIOCodecs.INTEGER).setWidgetFactory(new InfoDisplayCycleWidgetFactory(Lists.newArrayList((Object[])new Integer[]{0, 1, 2, 3, 4}), Lists.newArrayList((Object[])new Component[]{Component.translatable((String)"gui.xaero_off"), Component.translatable((String)"gui.xaero_light_block"), Component.translatable((String)"gui.xaero_light_sky"), Component.translatable((String)"gui.xaero_light_all"), Component.translatable((String)"gui.xaero_light_both2")}))).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        int showLightLevel = (Integer)displayInfo.getEffectiveState();
        if (showLightLevel == 0) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        int blockLight = mc.level.getBrightness(LightLayer.BLOCK, playerPos);
        int skyLight = mc.level.getBrightness(LightLayer.SKY, playerPos);
        if (showLightLevel == 1) {
            compiler.addLine((Component)Component.translatable((String)"gui.xaero_block_light_value", (Object[])new Object[]{blockLight}));
        } else if (showLightLevel == 2) {
            compiler.addLine((Component)Component.translatable((String)"gui.xaero_sky_light_value", (Object[])new Object[]{skyLight}));
        } else if (showLightLevel == 3) {
            compiler.addLine((Component)Component.translatable((String)"gui.xaero_all_light_value", (Object[])new Object[]{Math.max(blockLight, skyLight)}));
        } else {
            compiler.addLine((Component)Component.translatable((String)"gui.xaero_both_light_value", (Object[])new Object[]{blockLight, skyLight}));
        }
    }).setLegacyGetter(legacySettings -> {
        Integer result = legacySettings.showLightLevelLegacy;
        legacySettings.showLightLevelLegacy = null;
        return result;
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Integer> TIME = InfoDisplay.Builder.begin().setId("time").setName((Component)Component.translatable((String)"gui.xaero_infodisplay_time")).setDefaultState(0).setCodec((ConfigValueIOCodec<Integer>)BuiltInConfigValueIOCodecs.INTEGER).setWidgetFactory(new InfoDisplayCycleWidgetFactory(Lists.newArrayList((Object[])new Integer[]{0, 1, 2, 3, 4}), Lists.newArrayList((Object[])new Component[]{Component.translatable((String)"gui.xaero_off"), Component.translatable((String)"%s+%s", (Object[])new Object[]{Component.translatable((String)"gui.xaero_day"), Component.translatable((String)"gui.xaero_24h")}), Component.translatable((String)"%s+%s", (Object[])new Object[]{Component.translatable((String)"gui.xaero_day"), Component.translatable((String)"gui.xaero_12h")}), Component.translatable((String)"gui.xaero_24h"), Component.translatable((String)"gui.xaero_12h")}))).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        int showTime = (Integer)displayInfo.getEffectiveState();
        if (showTime <= 0) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        long totalTime = 6000L + mc.level.getOverworldClockTime();
        int dayNumber = (int)(totalTime / 24000L) + 1;
        int dayTimeSinceMidnight = (int)(totalTime % 24000L);
        int timeHours = dayTimeSinceMidnight / 1000;
        int minutes = (int)((double)(dayTimeSinceMidnight % 1000) / 1000.0 * 60.0);
        if ((showTime - 1) % 2 == 0) {
            if (showTime < 3) {
                compiler.addWords(I18n.get((String)"gui.xaero_day", (Object[])new Object[0]) + String.format(" %d, %02d:%02d", dayNumber, timeHours, minutes));
            } else {
                compiler.addWords(String.format("%02d:%02d", timeHours, minutes));
            }
        } else {
            String half = "AM";
            if (timeHours >= 12) {
                timeHours -= 12;
                half = "PM";
            }
            if (timeHours == 0) {
                timeHours = 12;
            }
            if (showTime < 3) {
                compiler.addWords(I18n.get((String)"gui.xaero_day", (Object[])new Object[0]) + String.format(" %d, %02d:%02d %s", dayNumber, timeHours, minutes, half));
            } else {
                compiler.addWords(String.format("%02d:%02d %s", timeHours, minutes, half));
            }
        }
    }).setLegacyGetter(legacySettings -> {
        Integer result = legacySettings.showTimeLegacy;
        legacySettings.showTimeLegacy = null;
        return result;
    }).setDestination(ALL::add).build();
    private static final Calendar CALENDAR = Calendar.getInstance();
    public static final InfoDisplay<Integer> REAL_TIME = InfoDisplay.Builder.begin().setId("real_time").setName((Component)Component.translatable((String)"gui.xaero_infodisplay_real_time")).setDefaultState(0).setCodec((ConfigValueIOCodec<Integer>)BuiltInConfigValueIOCodecs.INTEGER).setWidgetFactory(new InfoDisplayCycleWidgetFactory(Lists.newArrayList((Object[])new Integer[]{0, 1, 2}), Lists.newArrayList((Object[])new Component[]{Component.translatable((String)"gui.xaero_off"), Component.translatable((String)"gui.xaero_24h"), Component.translatable((String)"gui.xaero_12h")}))).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        int showTime = (Integer)displayInfo.getEffectiveState();
        if (showTime <= 0) {
            return;
        }
        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        int timeHours = CALENDAR.get(11);
        int minutes = CALENDAR.get(12);
        if (showTime == 1) {
            compiler.addWords(String.format("%02d:%02d IRL", timeHours, minutes));
        } else {
            String half = "AM";
            if (timeHours >= 12) {
                timeHours -= 12;
                half = "PM";
            }
            if (timeHours == 0) {
                timeHours = 12;
            }
            compiler.addWords(String.format("%02d:%02d %s IRL", timeHours, minutes, half));
        }
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> HIGHLIGHTS = InfoDisplay.Builder.begin().setId("highlights").setName((Component)Component.translatable((String)"gui.xaero_infodisplay_highlights")).setDefaultState(true).setCodec((ConfigValueIOCodec<Boolean>)BuiltInConfigValueIOCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.ALWAYS_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getEffectiveState()).booleanValue()) {
            return;
        }
        if (session.getProcessor().getMinimapWriter().getDimensionHighlightHandler() != null) {
            session.getProcessor().getMinimapWriter().getDimensionHighlightHandler().addBlockHighlightTooltips(compiler, playerPos.getX(), playerPos.getZ(), availableWidth, true);
        }
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> LIGHT_OVERLAY_INDICATOR = InfoDisplay.Builder.begin().setId("light_overlay_indicator").setName((Component)Component.translatable((String)"gui.xaero_infodisplay_light_overlay_indicator")).setDefaultState(true).setCodec((ConfigValueIOCodec<Boolean>)BuiltInConfigValueIOCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.ALWAYS_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getEffectiveState()).booleanValue()) {
            return;
        }
        if ((Integer)session.getModMain().getHudConfigs().getClientConfigManager().getEffective((ConfigOption)MinimapProfiledConfigOptions.LIGHT_OVERLAY_TYPE) > 0) {
            compiler.addWords(I18n.get((String)"gui.xaero_light_overlay_status", (Object[])new Object[0]));
        }
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> MANUAL_CAVE_MODE_INDICATOR = InfoDisplay.Builder.begin().setId("manual_cave_mode_indicator").setName((Component)Component.translatable((String)"gui.xaero_infodisplay_manual_cave_mode_indicator")).setDefaultState(true).setCodec((ConfigValueIOCodec<Boolean>)BuiltInConfigValueIOCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.ALWAYS_ON).setCompiler((displayInfo, compiler, session, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getEffectiveState()).booleanValue()) {
            return;
        }
        if (session.getProcessor().isManualCaveMode()) {
            compiler.addWords(I18n.get((String)"gui.xaero_manual_cave_mode", (Object[])new Object[0]));
        }
    }).setDestination(ALL::add).build();
    public static final InfoDisplay<Boolean> CUSTOM_SUB_WORLD = InfoDisplay.Builder.begin().setId("custom_sub_world").setName((Component)Component.translatable((String)"gui.xaero_infodisplay_custom_sub_world")).setDefaultState(true).setCodec((ConfigValueIOCodec<Boolean>)BuiltInConfigValueIOCodecs.BOOLEAN).setWidgetFactory(InfoDisplayCommonWidgetFactories.ALWAYS_ON).setCompiler((displayInfo, compiler, hudSession, availableWidth, playerPos) -> {
        if (!((Boolean)displayInfo.getEffectiveState()).booleanValue()) {
            return;
        }
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        MinimapWorldManager minimapWorldManager = session.getWorldManager();
        MinimapWorld currentWorld = minimapWorldManager.getCurrentWorld();
        if (currentWorld != null && minimapWorldManager.getAutoWorld() != currentWorld) {
            compiler.addWords(I18n.get((String)"gui.xaero_using_custom_subworld", (Object[])new Object[]{currentWorld.getContainer().getSubName()}));
        }
    }).setDestination(ALL::add).build();

    public static void forEach(Consumer<InfoDisplay<?>> action) {
        ALL.forEach(action);
    }
}

