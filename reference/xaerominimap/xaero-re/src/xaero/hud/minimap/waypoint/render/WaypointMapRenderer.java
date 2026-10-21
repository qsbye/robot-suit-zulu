/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.waypoint.render;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.common.effect.Effects;
import xaero.common.graphics.CustomRenderTypes;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointUtil;
import xaero.common.misc.Misc;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.config.util.MinimapConfigClientUtils;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderer;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.waypoint.WaypointSession;
import xaero.hud.minimap.waypoint.render.WaypointDeleter;
import xaero.hud.minimap.waypoint.render.WaypointMapRenderContext;
import xaero.hud.minimap.waypoint.render.WaypointMapRenderProvider;
import xaero.hud.minimap.waypoint.render.WaypointMapRenderReader;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.render.TextureLocations;
import xaero.hud.render.util.RenderBufferUtil;
import xaero.lib.XaeroLib;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.client.graphics.font.util.FontUtils;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.IndexedConfigOption;

public final class WaypointMapRenderer
extends MinimapElementRenderer<Waypoint, WaypointMapRenderContext> {
    private MinimapRendererHelper helper;
    private int scale;
    private boolean temporaryWaypointsGlobal;
    private double waypointsDistance;
    private boolean dimensionScaleDistance;
    private int opacity;
    private XaeroBufferProvider minimapBufferSource;
    private VertexConsumer texturedIconConsumer;
    private VertexConsumer waypointBackgroundConsumer;

    private WaypointMapRenderer(WaypointMapRenderReader elementReader, WaypointMapRenderProvider provider, WaypointMapRenderContext context) {
        super(elementReader, provider, context);
    }

    @Override
    public boolean renderElement(Waypoint w, boolean highlighted, boolean outOfBounds, double optionalDepth, float optionalScale, double partialX, double partialY, MinimapElementRenderInfo renderInfo, MinimapElementGraphics guiGraphics, XaeroBufferProvider xaeroBufferProvider) {
        double waypointPosDivider = renderInfo.backgroundCoordinateScale / ((WaypointMapRenderContext)this.context).dimCoordinateScale;
        double wX = (double)w.getX(waypointPosDivider) + 0.5;
        double wZ = (double)w.getZ(waypointPosDivider) + 0.5;
        double offX = wX - renderInfo.renderPos.x;
        double offZ = wZ - renderInfo.renderPos.z;
        double distance2D = Math.sqrt(offX * offX + offZ * offZ);
        double distanceScale = this.dimensionScaleDistance ? renderInfo.backgroundCoordinateScale : 1.0;
        double scaledDistance2D = distance2D * distanceScale;
        if (!(w.isDestination() || w.getPurpose() == WaypointPurpose.DEATH || w.isGlobal() || w.isTemporary() && this.temporaryWaypointsGlobal || this.waypointsDistance == 0.0 || !(scaledDistance2D > this.waypointsDistance))) {
            return false;
        }
        PoseStack matrixStack = guiGraphics.pose();
        MinimapElementRenderLocation location = renderInfo.location;
        matrixStack.translate(-1.0, -1.0, optionalDepth);
        if (this.scale <= 0 || location != MinimapElementRenderLocation.OVER_MINIMAP) {
            matrixStack.scale(optionalScale, optionalScale, 1.0f);
        } else {
            matrixStack.scale((float)this.scale, (float)this.scale, 1.0f);
        }
        this.drawIcon(guiGraphics, this.helper, w, 0, 0, this.opacity, this.minimapBufferSource, this.waypointBackgroundConsumer, this.texturedIconConsumer);
        return true;
    }

    @Override
    public void preRender(MinimapElementRenderInfo renderInfo, XaeroBufferProvider xaeroBufferProvider, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers) {
        xaeroBufferProvider.endBatch();
        this.minimapBufferSource = XaeroLib.INSTANCE.getClient().getBufferProvider();
        this.waypointBackgroundConsumer = this.minimapBufferSource.getBuffer(CustomRenderTypes.COLORED_WAYPOINTS_BGS);
        this.texturedIconConsumer = this.minimapBufferSource.getBuffer(CustomRenderTypes.GUI_NEAREST);
        this.helper = HudMod.INSTANCE.getMinimap().getMinimapFBORenderer().getHelper();
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        MinimapWorld currentWorld = session.getWorldManager().getCurrentWorld();
        ((WaypointMapRenderContext)this.context).dimCoordinateScale = session.getDimensionHelper().getDimCoordinateScale(currentWorld);
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        this.scale = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_ICON_SCALE_ON_MINIMAP);
        if (this.scale > 0) {
            this.scale = (int)MinimapConfigClientUtils.getUIScale(configManager, (IndexedConfigOption<Integer>)MinimapProfiledConfigOptions.WAYPOINT_ICON_SCALE_ON_MINIMAP);
        }
        this.temporaryWaypointsGlobal = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.TEMPORARY_WAYPOINTS_GLOBAL);
        this.waypointsDistance = ((Integer)configManager.getEffective(MinimapProfiledConfigOptions.WAYPOINT_MAX_DISTANCE)).intValue();
        this.dimensionScaleDistance = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_MAX_DISTANCE_DIMENSION_SCALE);
        this.opacity = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.WAYPOINT_OPACITY_ON_MINIMAP);
    }

    @Override
    public void postRender(MinimapElementRenderInfo renderInfo, XaeroBufferProvider xaeroBufferProvider, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers) {
        this.minimapBufferSource.endBatch();
        this.waypointBackgroundConsumer = null;
    }

    public void drawIcon(MinimapElementGraphics guiGraphics, MinimapRendererHelper rendererHelper, Waypoint w, int drawX, int drawY, int opacity, XaeroBufferProvider renderTypeBuffer, VertexConsumer waypointBackgroundConsumer, VertexConsumer texturedIconConsumer) {
        int color = w.getWaypointColor().getHex();
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        float a = (float)opacity / 100.0f;
        int initialsWidth = w.getPurpose() == WaypointPurpose.DEATH ? 7 : Minecraft.getInstance().font.width(w.getInitials());
        int addedFrame = WaypointUtil.getAddedMinimapIconFrame(initialsWidth);
        int rectX1 = drawX - 4 - addedFrame;
        int rectY1 = drawY - 4;
        int rectX2 = drawX + 5 + addedFrame;
        int rectY2 = drawY + 5;
        this.drawIcon(guiGraphics, w, drawX, drawY, rectX1, rectY1, rectX2, rectY2, r, g, b, a, initialsWidth, renderTypeBuffer, waypointBackgroundConsumer, texturedIconConsumer);
    }

    public void drawIconGUI(GuiGraphicsExtractor guiGraphics, Waypoint w, int drawX, int drawY, int opacity) {
        int color = w.getWaypointColor().getHex();
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        float a = (float)opacity / 100.0f;
        int initialsWidth = w.getPurpose() == WaypointPurpose.DEATH ? 7 : Minecraft.getInstance().font.width(w.getInitials());
        int addedFrame = WaypointUtil.getAddedMinimapIconFrame(initialsWidth);
        int rectX1 = drawX - 4 - addedFrame;
        int rectY1 = drawY - 4;
        int rectX2 = drawX + 5 + addedFrame;
        int rectY2 = drawY + 5;
        this.drawIconGUI(guiGraphics, w, drawX, drawY, rectX1, rectY1, rectX2, rectY2, r, g, b, a, initialsWidth);
    }

    private void drawIcon(MinimapElementGraphics guiGraphics, Waypoint w, int drawX, int drawY, int rectX1, int rectY1, int rectX2, int rectY2, int r, int g, int b, float a, int initialsWidth, XaeroBufferProvider renderTypeBuffer, VertexConsumer waypointBackgroundConsumer, VertexConsumer texturedIconConsumer) {
        PoseStack matrixStack = guiGraphics.pose();
        RenderBufferUtil.addColoredRect(matrixStack.last().pose(), waypointBackgroundConsumer, rectX1, rectY1, rectX2 - rectX1, rectY2 - rectY1, (float)r / 255.0f, (float)g / 255.0f, (float)b / 255.0f, a);
        if (w.getPurpose() == WaypointPurpose.DEATH) {
            RenderBufferUtil.addTexturedColoredRect(matrixStack.last().pose(), texturedIconConsumer, rectX1 + 1, rectY1 + 1, 0, 87, 9, 9, 9, -9, 0.2431f, 0.2431f, 0.2431f, 1.0f, 256.0f);
            RenderBufferUtil.addTexturedColoredRect(matrixStack.last().pose(), texturedIconConsumer, rectX1, rectY1, 0, 87, 9, 9, 9, -9, 0.9882f, 0.9882f, 0.9882f, 1.0f, 256.0f);
            return;
        }
        FontUtils.drawNormalText((PoseStack)matrixStack, (String)w.getInitials(), (float)(drawX + 1 - initialsWidth / 2), (float)(drawY - 3), (int)-1, (boolean)true, (XaeroBufferProvider)renderTypeBuffer);
    }

    private void drawIconGUI(GuiGraphicsExtractor guiGraphics, Waypoint w, int drawX, int drawY, int rectX1, int rectY1, int rectX2, int rectY2, int r, int g, int b, float a, int initialsWidth) {
        int aByte = (int)(a * 255.0f);
        int color = aByte << 24 | r << 16 | g << 8 | b;
        guiGraphics.fill(rectX1, rectY1, rectX2, rectY2, color);
        if (w.getPurpose() == WaypointPurpose.DEATH) {
            int shadowColor = -12698050;
            int skullColor = -197380;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TextureLocations.GUI_TEXTURES, rectX1 + 1, rectY1 + 1, 0.0f, 78.0f, 9, 9, 9, 9, 256, 256, shadowColor);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TextureLocations.GUI_TEXTURES, rectX1, rectY1, 0.0f, 78.0f, 9, 9, 9, 9, 256, 256, skullColor);
            return;
        }
        guiGraphics.text(Minecraft.getInstance().font, w.getInitials(), drawX + 1 - initialsWidth / 2, drawY - 3, -1, true);
    }

    public void drawSetChange(MinimapSession session, GuiGraphicsExtractor guiGraphics, Window res) {
        MinimapWorld minimapWorld = session.getWorldManager().getCurrentWorld();
        if (minimapWorld == null) {
            return;
        }
        WaypointSession waypointSession = session.getWaypointSession();
        if (waypointSession.getSetChangedTime() == 0L) {
            return;
        }
        int passed = (int)(System.currentTimeMillis() - waypointSession.getSetChangedTime());
        if (passed >= 1500) {
            waypointSession.setSetChangedTime(0L);
            return;
        }
        int fadeTime = 300;
        boolean fading = passed > 1500 - fadeTime;
        float fadeFactor = fading ? (float)(1500 - passed) / (float)fadeTime : 1.0f;
        int alpha = 3 + (int)(252.0f * fadeFactor);
        int c = 0xFFFFFF | alpha << 24;
        guiGraphics.centeredText(Minecraft.getInstance().font, I18n.get((String)minimapWorld.getCurrentWaypointSet().getName(), (Object[])new Object[0]), res.getGuiScaledWidth() / 2, res.getGuiScaledHeight() / 2 + 50, c);
    }

    @Override
    public boolean shouldRender(MinimapElementRenderLocation location) {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        if (!(location != MinimapElementRenderLocation.OVER_MINIMAP && location != MinimapElementRenderLocation.IN_MINIMAP || ((Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.WAYPOINTS_ON_MINIMAP)).booleanValue())) {
            return false;
        }
        return !Misc.hasEffect(Effects.NO_WAYPOINTS) && !Misc.hasEffect(Effects.NO_WAYPOINTS_HARMFUL);
    }

    @Override
    public int getOrder() {
        return 100;
    }

    public static final class Builder {
        private WaypointDeleter waypointDeleter;
        private final IXaeroMinimap modMain;

        private Builder(IXaeroMinimap modMain) {
            this.modMain = modMain;
        }

        private Builder setDefault() {
            this.setWaypointDeleter(null);
            return this;
        }

        public Builder setWaypointDeleter(WaypointDeleter waypointDeleter) {
            this.waypointDeleter = waypointDeleter;
            return this;
        }

        public WaypointMapRenderer build() {
            if (this.waypointDeleter == null) {
                throw new IllegalStateException();
            }
            WaypointMapRenderContext context = new WaypointMapRenderContext();
            return new WaypointMapRenderer(new WaypointMapRenderReader(), new WaypointMapRenderProvider(), context);
        }

        public static Builder begin(IXaeroMinimap modMain) {
            return new Builder(modMain).setDefault();
        }
    }
}

