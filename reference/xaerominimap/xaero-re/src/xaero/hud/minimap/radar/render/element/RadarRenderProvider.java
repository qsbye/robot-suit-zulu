/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.render.element;

import java.lang.reflect.Field;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.Entity;
import xaero.common.HudMod;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.controls.key.MinimapKeyMappings;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderProvider;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;
import xaero.hud.minimap.radar.render.element.RadarRenderContext;
import xaero.hud.minimap.radar.state.RadarList;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.reflection.util.ReflectionUtils;

public final class RadarRenderProvider
extends MinimapElementRenderProvider<Entity, RadarRenderContext> {
    private static final Field SCREEN_FIELD = ReflectionUtils.getFieldReflection(Gui.class, (String)"screen", (String)"Lnet/minecraft/client/gui/screens/Screen;");
    private boolean used;
    private Entity renderEntity;
    private Iterator<RadarList> entityLists;
    private RadarList currentList;
    private RadarList listForContext;
    private int currentListIndex;

    @Override
    public void begin(MinimapElementRenderLocation location, RadarRenderContext context) {
        MinimapSession minimapSession = BuiltInHudModules.MINIMAP.getCurrentSession();
        this.used = true;
        this.renderEntity = Minecraft.getInstance().getCameraEntity();
        context.reversedOrder = MinimapKeyMappings.REVERSE_ENTITY_RADAR.isDown();
        Screen screenBU = Minecraft.getInstance().gui.screen();
        ReflectionUtils.setReflectFieldValue((Object)Minecraft.getInstance().gui, (Field)SCREEN_FIELD, null);
        context.playerListDown = Minecraft.getInstance().options.keyPlayerList.isDown() || MinimapKeyMappings.ALTERNATIVE_LIST_PLAYERS.isDown();
        ReflectionUtils.setReflectFieldValue((Object)Minecraft.getInstance().gui, (Field)SCREEN_FIELD, (Object)screenBU);
        this.entityLists = minimapSession.getRadarSession().getState().getRadarLists().iterator();
        this.currentList = null;
        this.listForContext = null;
        this.currentListIndex = 0;
    }

    private void ensureList(MinimapElementRenderLocation location, RadarRenderContext context) {
        block0: while (this.currentList == null || this.currentListIndex >= this.currentList.size() || this.currentListIndex < 0) {
            do {
                if (!this.entityLists.hasNext()) {
                    this.currentList = null;
                    this.currentListIndex = 0;
                    break block0;
                }
                this.currentList = this.entityLists.next();
                int n = this.currentListIndex = context.reversedOrder ? this.currentList.size() - 1 : 0;
            } while ((location == MinimapElementRenderLocation.IN_MINIMAP || location == MinimapElementRenderLocation.OVER_MINIMAP) && location == MinimapElementRenderLocation.OVER_MINIMAP != this.shouldRenderOverMinimap(context));
        }
    }

    private boolean shouldRenderOverMinimap(RadarRenderContext context) {
        int settingValue = this.currentList.getEffective(EntityRadarCategorySettings.RENDER_OVER_MINIMAP).intValue();
        return settingValue == 2 || settingValue == 1 && context.playerListDown;
    }

    @Override
    public boolean hasNext(MinimapElementRenderLocation location, RadarRenderContext context) {
        this.ensureList(location, context);
        if (this.currentList == null) {
            return false;
        }
        return !context.reversedOrder && this.currentListIndex < this.currentList.size() || context.reversedOrder && this.currentListIndex >= 0;
    }

    @Override
    public Entity setupContextAndGetNext(MinimapElementRenderLocation location, RadarRenderContext context) {
        Entity result;
        this.ensureList(location, context);
        if (this.listForContext != this.currentList) {
            context.radarList = this.currentList;
            this.setupContextForList(this.currentList, context);
            this.listForContext = this.currentList;
        }
        if ((result = this.getNext(location, context)) == null) {
            return null;
        }
        this.setupContextForEntity(result, context);
        return result;
    }

    @Override
    public Entity getNext(MinimapElementRenderLocation location, RadarRenderContext context) {
        Entity result = this.currentList.get(this.currentListIndex);
        this.currentListIndex += context.reversedOrder ? -1 : 1;
        if (this.renderEntity == result) {
            return null;
        }
        return result;
    }

    @Override
    public void end(MinimapElementRenderLocation location, RadarRenderContext context) {
        this.used = false;
        this.renderEntity = null;
        context.radarList = null;
    }

    public void setupContextForList(RadarList radarList, RadarRenderContext context) {
        context.iconScale = radarList.getEffective(EntityRadarCategorySettings.ICON_SCALE);
        context.dotSize = context.isMainDot ? ((Integer)HudMod.INSTANCE.getHudConfigs().getClientConfigManager().getEffective((ConfigOption)MinimapProfiledConfigOptions.RADAR_MAIN_DOT_SIZE)).intValue() : radarList.getEffective(EntityRadarCategorySettings.DOT_SIZE).intValue();
        context.dotScale = 1.0 + 0.5 * (double)(context.dotSize - 1);
        int icons = radarList.getEffective(EntityRadarCategorySettings.ICONS).intValue();
        context.iconsForCategory = icons == 1 && context.playerListDown || icons == 2;
    }

    public void setupContextForEntity(Entity entity, RadarRenderContext context) {
        context.icon = context.iconsForCategory;
    }

    public boolean isUsed() {
        return this.used;
    }
}

