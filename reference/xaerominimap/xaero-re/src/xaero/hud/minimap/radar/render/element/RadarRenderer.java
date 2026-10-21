/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.render.element;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import xaero.common.HudMod;
import xaero.common.graphics.CustomRenderTypes;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRendererProvider;
import xaero.common.icon.XaeroIcon;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.common.misc.Misc;
import xaero.common.settings.ModSettings;
import xaero.hud.minimap.BuiltInHudModules;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.config.primary.option.MinimapPrimaryClientConfigOptions;
import xaero.hud.minimap.controls.key.MinimapKeyMappings;
import xaero.hud.minimap.element.render.MinimapElementGraphics;
import xaero.hud.minimap.element.render.MinimapElementRenderInfo;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.element.render.MinimapElementRenderer;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.radar.RadarSession;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.EntityRadarCategoryManager;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;
import xaero.hud.minimap.radar.color.RadarColor;
import xaero.hud.minimap.radar.icon.RadarIconManager;
import xaero.hud.minimap.radar.render.element.RadarElementReader;
import xaero.hud.minimap.radar.render.element.RadarRenderContext;
import xaero.hud.minimap.radar.render.element.RadarRenderProvider;
import xaero.hud.minimap.radar.state.RadarList;
import xaero.hud.minimap.radar.util.RadarUtils;
import xaero.hud.render.util.MultiTextureRenderUtil;
import xaero.hud.render.util.RenderBufferUtil;
import xaero.lib.XaeroLib;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.client.graphics.font.util.FontUtils;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.single.SingleConfigManager;

public final class RadarRenderer
extends MinimapElementRenderer<Entity, RadarRenderContext> {
    private final RadarIconManager radarIconManager;
    private final Minimap minimap;
    private RadarSession radarSession;
    private EntityRadarCategoryManager categoryManager;
    private RadarList previousList;
    private double maxDistanceSquared;
    private double labelScale;
    private boolean smoothDots;
    private boolean debugEntityIcons;
    private boolean debugEntityVariantIds;
    private int dotsStyle;
    private int heightLimit;
    private boolean heightBasedFade;
    private int startFadingAt;
    private boolean displayNameWhenIconFails;
    private boolean alwaysNameTags;
    private RadarColor radarColor;
    private RadarColor fallbackColor;
    private int displayY;
    private int nameSettingForCategory;
    private boolean namesForCategory;
    private boolean name;
    private boolean iconsAllowed;
    private boolean labelsAllowed;
    private RenderType dotsRenderType;
    private VertexConsumer dotsBufferBuilder;
    private VertexConsumer labelBgBuilder;
    private MultiTextureRenderTypeRenderer iconsRenderer;
    private MinimapRendererHelper helper;
    private final RadarRenderProvider radarRenderProvider;

    private RadarRenderer(RadarIconManager radarIconManager, Minimap minimap, RadarElementReader elementReader, RadarRenderProvider provider, RadarRenderContext context) {
        super(elementReader, provider, context);
        this.radarIconManager = radarIconManager;
        this.minimap = minimap;
        this.radarRenderProvider = provider;
    }

    @Override
    public void preRender(MinimapElementRenderInfo renderInfo, XaeroBufferProvider xaeroBufferProvider, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers) {
        this.radarIconManager.allowPrerender();
        ModSettings settings = HudMod.INSTANCE.getSettings();
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        SingleConfigManager primaryConfigManager = configManager.getPrimaryConfigManager();
        this.iconsAllowed = true;
        this.labelsAllowed = true;
        ((RadarRenderContext)this.context).reversedOrder = MinimapKeyMappings.REVERSE_ENTITY_RADAR.isDown();
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        this.radarSession = session.getRadarSession();
        this.categoryManager = this.radarSession.getCategoryManager();
        this.previousList = null;
        this.labelScale = (Double)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.RADAR_NAME_SCALE) * (Minecraft.getInstance().isEnforceUnicode() ? 2.0 : 1.0);
        this.smoothDots = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.RADAR_SMOOTH_DOTS);
        this.debugEntityIcons = (Boolean)primaryConfigManager.getEffective((ConfigOption)MinimapPrimaryClientConfigOptions.DEBUG_ENTITY_ICONS);
        this.debugEntityVariantIds = (Boolean)primaryConfigManager.getEffective((ConfigOption)MinimapPrimaryClientConfigOptions.DEBUG_ENTITY_VARIANT_IDS);
        this.dotsStyle = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.RADAR_DOTS_STYLE);
        this.dotsRenderType = this.smoothDots ? CustomRenderTypes.GUI_BILINEAR : CustomRenderTypes.GUI_NEAREST;
        this.dotsBufferBuilder = null;
        this.labelBgBuilder = xaeroBufferProvider.getBuffer(CustomRenderTypes.RADAR_NAME_BGS);
        this.iconsRenderer = multiTextureRenderTypeRenderers.getRenderer(CustomRenderTypes.GUI_BILINEAR);
        this.helper = HudMod.INSTANCE.getMinimap().getMinimapFBORenderer().getHelper();
        double playerDimDiv = renderInfo.backgroundCoordinateScale / renderInfo.renderEntityDimensionScale;
        int shapeConfig = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.SHAPE);
        this.maxDistanceSquared = RadarUtils.getMaxDistance(session.getProcessor(), shapeConfig == 1) * playerDimDiv * playerDimDiv;
    }

    @Override
    public boolean renderElement(Entity e, boolean highlighted, boolean outOfBounds, double optionalDepth, float optionalScale, double partialX, double partialY, MinimapElementRenderInfo renderInfo, MinimapElementGraphics guiGraphics, XaeroBufferProvider xaeroBufferProvider) {
        double figureScale;
        if (renderInfo.location == MinimapElementRenderLocation.IN_MINIMAP) {
            double offX = e.getX() - renderInfo.renderEntityPos.x;
            if (offX * offX > this.maxDistanceSquared) {
                return false;
            }
            double offY = e.getZ() - renderInfo.renderEntityPos.z;
            if (offY * offY > this.maxDistanceSquared) {
                return false;
            }
        }
        if (((RadarRenderContext)this.context).radarList == null) {
            EntityRadarCategory rootCategory = this.categoryManager.getRootCategory();
            EntityRadarCategory syncedRootCategory = this.categoryManager.getEffectiveSyncedRootCategory();
            ((RadarRenderContext)this.context).radarList = RadarList.Builder.getDefault().build();
            ((RadarRenderContext)this.context).radarList.setClientCategory(this.categoryManager.getRuleResolver().resolve(rootCategory, e, renderInfo.player));
            if (((RadarRenderContext)this.context).radarList.getClientCategory() == null) {
                if (!((RadarRenderContext)this.context).isMainDot) {
                    return false;
                }
                ((RadarRenderContext)this.context).radarList.setClientCategory(rootCategory);
            }
            if (syncedRootCategory != null) {
                ((RadarRenderContext)this.context).radarList.setSyncedCategory(this.categoryManager.getRuleResolver().resolve(syncedRootCategory, e, renderInfo.player));
                if (!((RadarRenderContext)this.context).isMainDot && ((RadarRenderContext)this.context).radarList.getSyncedCategory() == null) {
                    return false;
                }
            }
            if (((RadarRenderContext)this.context).radarList == null) {
                return false;
            }
        }
        if (((RadarRenderContext)this.context).radarList != this.previousList) {
            this.setupRenderForList(((RadarRenderContext)this.context).radarList);
            this.previousList = ((RadarRenderContext)this.context).radarList;
        }
        this.setupRenderForEntity(e);
        if (e instanceof Player) {
            this.confirmTrackedPlayerRadarRender((Player)e);
        }
        Entity renderEntity = renderInfo.renderEntity;
        boolean cave = renderInfo.cave;
        float optionalScaleAdjust = renderInfo.location == MinimapElementRenderLocation.OVER_MINIMAP ? 0.5f : 1.0f;
        optionalScale *= optionalScaleAdjust;
        PoseStack matrixStack = guiGraphics.pose();
        matrixStack.pushPose();
        boolean icon = this.iconsAllowed && ((RadarRenderContext)this.context).icon;
        boolean name = this.name;
        if (highlighted && this.nameSettingForCategory > 0) {
            name = true;
        }
        XaeroIcon entityIcon = null;
        if (icon) {
            entityIcon = this.radarIconManager.get(e, (float)((RadarRenderContext)this.context).iconScale, this.debugEntityIcons, this.debugEntityVariantIds, guiGraphics, renderInfo.framebuffer);
        }
        if (entityIcon == RadarIconManager.DOT) {
            entityIcon = null;
            icon = false;
        }
        boolean usableIcon = entityIcon != null && entityIcon != RadarIconManager.FAILED;
        float offY = (float)(renderEntity.getY() - e.getY());
        int labelOffsetX = 0;
        int labelOffsetY = 0;
        matrixStack.translate(partialX, partialY, 0.0);
        if (usableIcon) {
            figureScale = ((RadarRenderContext)this.context).iconScale;
            this.renderIcon(entityIcon, optionalScale, figureScale, offY, cave, matrixStack);
        } else {
            boolean smooth = this.smoothDots;
            if (!smooth) {
                optionalScale = (float)Math.ceil(optionalScale);
            }
            double dotActualScale = optionalScale;
            figureScale = ((RadarRenderContext)this.context).dotScale;
            if (this.dotsStyle == 1) {
                if (!smooth) {
                    figureScale = (int)figureScale;
                }
                dotActualScale *= figureScale;
            }
            float dotOffset = this.renderDot(e, renderInfo.player, smooth, optionalScale, figureScale, offY, cave, matrixStack, xaeroBufferProvider);
            if (!smooth) {
                double dotRadius = (double)(-dotOffset) * dotActualScale;
                double dotRadiusPartial = dotRadius - (double)((int)dotRadius);
                labelOffsetX = partialX - dotRadiusPartial <= -0.5 ? -1 : 0;
                int n = labelOffsetY = partialY - dotRadiusPartial < -0.5 ? -1 : 0;
            }
            if (icon && this.displayNameWhenIconFails && entityIcon == RadarIconManager.FAILED) {
                name = true;
            }
        }
        matrixStack.popPose();
        if (!this.labelsAllowed) {
            return true;
        }
        if (!name && this.displayY <= 0) {
            return true;
        }
        matrixStack.translate((double)labelOffsetX, (double)(labelOffsetY += (int)Math.round((double)(usableIcon ? 11 : 5) * figureScale * (double)optionalScale)), optionalDepth + (double)0.1f);
        if (optionalScale < 1.0f) {
            optionalScale = 1.0f;
        }
        this.renderLabel(e, renderEntity, name, optionalScale, matrixStack, xaeroBufferProvider);
        return true;
    }

    @Override
    public void postRender(MinimapElementRenderInfo renderInfo, XaeroBufferProvider xaeroBufferProvider, MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers) {
        if (((RadarRenderContext)this.context).reversedOrder && this.dotsBufferBuilder != null) {
            xaeroBufferProvider.endBatch(this.dotsRenderType);
        }
        multiTextureRenderTypeRenderers.draw(this.iconsRenderer);
        if (!((RadarRenderContext)this.context).reversedOrder && this.dotsBufferBuilder != null) {
            xaeroBufferProvider.endBatch(this.dotsRenderType);
        }
        xaeroBufferProvider.endBatch();
        this.iconsRenderer = null;
        this.previousList = null;
    }

    private void renderIcon(XaeroIcon entityIcon, double optionalScale, double figureScale, float offY, boolean cave, PoseStack matrixStack) {
        double clampedScale = Math.max(1.0, figureScale * optionalScale);
        matrixStack.scale((float)clampedScale, (float)clampedScale, 1.0f);
        float brightness = !this.heightBasedFade ? 1.0f : this.radarSession.getColorHelper().getEntityHeightFade(offY, this.heightLimit, this.startFadingAt);
        float opacity = 1.0f;
        if (cave) {
            opacity = brightness;
            brightness = 1.0f;
        }
        MultiTextureRenderUtil.prepareTexturedColoredRect(matrixStack.last().pose(), -31.0f, -31.0f, entityIcon.getOffsetX() + 1, entityIcon.getOffsetY() + 1, 62.0f, 62.0f, 62.0f, (float)entityIcon.getTextureAtlas().getWidth(), brightness, brightness, brightness, opacity, entityIcon.getTextureAtlas().getTextureView(), this.iconsRenderer);
    }

    private float renderDot(Entity e, Player player, boolean smooth, float optionalScale, double figureScale, float offY, boolean cave, PoseStack matrixStack, XaeroBufferProvider xaeroBufferProvider) {
        matrixStack.scale(optionalScale, optionalScale, 1.0f);
        int color = this.radarSession.getColorHelper().getEntityColor(e, offY, cave, this.heightLimit, this.startFadingAt, this.heightBasedFade, this.radarColor, this.fallbackColor);
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        int dotTextureX = 0;
        int dotTextureY = 0;
        int dotTextureW = 0;
        int dotTextureH = 0;
        float dotOffset = 0.0f;
        if (this.dotsStyle == 1) {
            if (smooth) {
                dotTextureX = 1;
                dotTextureY = 88;
            } else {
                dotTextureX = 9;
                dotTextureY = 77;
            }
            dotOffset = -3.5f;
            dotTextureH = 8;
            dotTextureW = 8;
            matrixStack.scale((float)figureScale, (float)figureScale, 1.0f);
        } else {
            switch (((RadarRenderContext)this.context).dotSize) {
                case 1: {
                    dotOffset = -4.5f;
                    dotTextureY = 108;
                    dotTextureH = 9;
                    dotTextureW = 9;
                    break;
                }
                case 3: {
                    dotOffset = -7.5f;
                    dotTextureY = 128;
                    dotTextureH = 15;
                    dotTextureW = 15;
                    break;
                }
                case 4: {
                    dotOffset = -10.5f;
                    dotTextureY = 160;
                    dotTextureH = 21;
                    dotTextureW = 21;
                    break;
                }
                default: {
                    dotOffset = -5.5f;
                    dotTextureY = 117;
                    dotTextureH = 11;
                    dotTextureW = 11;
                }
            }
        }
        if (this.dotsBufferBuilder == null) {
            this.dotsBufferBuilder = xaeroBufferProvider.getBuffer(this.dotsRenderType);
        }
        RenderBufferUtil.addTexturedColoredRect(matrixStack.last().pose(), this.dotsBufferBuilder, dotOffset, dotOffset, dotTextureX, dotTextureY, dotTextureW, dotTextureH, r, g, b, a, 256.0f);
        return dotOffset;
    }

    private void renderLabel(Entity e, Entity renderEntity, boolean name, double optionalScale, PoseStack matrixStack, XaeroBufferProvider xaeroBufferProvider) {
        double dotNameScale = this.labelScale * optionalScale;
        matrixStack.scale((float)dotNameScale, (float)dotNameScale, 1.0f);
        Object yValueString = null;
        if (this.displayY > 0) {
            int yInt = (int)Math.floor(e.getY());
            int pYInt = (int)Math.floor(renderEntity.getY());
            yValueString = this.displayY == 1 ? "" + yInt : (this.displayY == 2 ? "" + (yInt - pYInt) : "");
            if (((String)(yValueString = (String)yValueString + (yInt > pYInt ? "\u2191" : (yInt != pYInt ? "\u2193" : "")))).length() == 0) {
                yValueString = "-";
            }
        }
        Font font = Minecraft.getInstance().font;
        Object label = null;
        if (name) {
            Component component = Misc.getFixedDisplayName(e);
            if (component == null) {
                return;
            }
            label = component.getString();
            if (this.displayY > 0) {
                label = (String)label + "(" + (String)yValueString + ")";
            }
        } else if (this.displayY > 0) {
            label = yValueString;
        }
        if (label == null) {
            return;
        }
        int labelW = font.width((String)label);
        RenderBufferUtil.addColoredRect(matrixStack.last().pose(), this.labelBgBuilder, -labelW / 2 - 2, -1.0f, labelW + 3, 10, 0.0f, 0.0f, 0.0f, 0.3529412f);
        FontUtils.drawNormalText((PoseStack)matrixStack, (String)label, (float)(-labelW / 2), (float)0.0f, (int)-1, (boolean)false, (XaeroBufferProvider)xaeroBufferProvider);
    }

    private void setupRenderForList(RadarList radarList) {
        if (!this.radarRenderProvider.isUsed()) {
            this.radarRenderProvider.setupContextForList(radarList, (RadarRenderContext)this.context);
        }
        this.heightLimit = radarList.getEffective(EntityRadarCategorySettings.HEIGHT_LIMIT).intValue();
        this.heightBasedFade = radarList.getEffective(EntityRadarCategorySettings.HEIGHT_FADE);
        this.startFadingAt = radarList.getEffective(EntityRadarCategorySettings.START_FADING_AT).intValue();
        this.displayNameWhenIconFails = radarList.getEffective(EntityRadarCategorySettings.ICON_NAME_FALLBACK);
        this.alwaysNameTags = radarList.getEffective(EntityRadarCategorySettings.ALWAYS_NAMETAGS);
        this.radarColor = RadarColor.fromIndex(radarList.getEffective(EntityRadarCategorySettings.COLOR).intValue());
        this.fallbackColor = this.radarSession.getColorHelper().getFallbackColor(radarList);
        this.displayY = radarList.getEffective(EntityRadarCategorySettings.DISPLAY_Y).intValue();
        this.nameSettingForCategory = radarList.getEffective(EntityRadarCategorySettings.NAMES).intValue();
        this.namesForCategory = this.nameSettingForCategory == 1 && ((RadarRenderContext)this.context).playerListDown || this.nameSettingForCategory == 2;
    }

    private void setupRenderForEntity(Entity entity) {
        boolean name;
        if (!this.radarRenderProvider.isUsed()) {
            this.radarRenderProvider.setupContextForEntity(entity, (RadarRenderContext)this.context);
        }
        if (!(name = this.namesForCategory) && !(entity instanceof Player)) {
            name = this.alwaysNameTags && entity.hasCustomName();
        }
        this.name = name;
    }

    private void confirmTrackedPlayerRadarRender(Player e) {
        if (HudMod.INSTANCE.getTrackedPlayerRenderer().getCollector().playerExists(e.getUUID())) {
            HudMod.INSTANCE.getTrackedPlayerRenderer().getCollector().confirmPlayerRadarRender(e);
        }
        if (!HudMod.INSTANCE.getSupportMods().worldmap()) {
            return;
        }
        HudMod.INSTANCE.getSupportMods().worldmapSupport.confirmPlayerRadarRender(e);
    }

    public void renderSingleEntity(Entity entity, boolean cave, boolean highlighted, float optionalScale, boolean allowIcon, boolean allowLabel, MinimapElementRenderLocation location, RenderTarget defaultFramebuffer, MinimapElementGraphics guiGraphics, boolean depth) {
        ((RadarRenderContext)this.context).radarList = null;
        MinimapSession session = BuiltInHudModules.MINIMAP.getCurrentSession();
        LocalPlayer player = Minecraft.getInstance().player;
        MinimapElementRenderInfo renderInfo = new MinimapElementRenderInfo(location, entity, (Player)player, entity.position(), cave, 1.0f, defaultFramebuffer, 1.0, (ResourceKey<Level>)entity.level().dimension());
        MultiTextureRenderTypeRendererProvider multiTextureRenderTypeRenderers = session.getMultiTextureRenderTypeRenderers();
        XaeroBufferProvider xaeroBufferProvider = XaeroLib.INSTANCE.getClient().getBufferProvider();
        ((RadarRenderContext)this.context).isMainDot = entity == Minecraft.getInstance().getCameraEntity();
        this.preRender(renderInfo, xaeroBufferProvider, multiTextureRenderTypeRenderers);
        if (!depth) {
            this.dotsRenderType = CustomRenderTypes.GUI_BILINEAR_NO_DEPTH;
        }
        this.iconsAllowed = allowIcon;
        this.labelsAllowed = allowLabel;
        this.renderElement(entity, highlighted, false, 0.0, optionalScale, 0.0, 0.0, renderInfo, guiGraphics, xaeroBufferProvider);
        this.postRender(renderInfo, xaeroBufferProvider, multiTextureRenderTypeRenderers);
        ((RadarRenderContext)this.context).isMainDot = false;
    }

    @Override
    public boolean shouldRender(MinimapElementRenderLocation location) {
        if (!this.minimap.usingFBO()) {
            return false;
        }
        if (location == MinimapElementRenderLocation.WORLD_MAP) {
            return true;
        }
        if (location == MinimapElementRenderLocation.WORLD_MAP_MENU) {
            return true;
        }
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        return (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_RADAR);
    }

    public static final class Builder {
        private RadarIconManager radarIconManager;
        private Minimap minimap;

        private Builder() {
        }

        public Builder setDefault() {
            this.setRadarIconManager(null);
            return this;
        }

        public Builder setRadarIconManager(RadarIconManager radarIconManager) {
            this.radarIconManager = radarIconManager;
            return this;
        }

        public Builder setMinimap(Minimap minimap) {
            this.minimap = minimap;
            return this;
        }

        public RadarRenderer build() {
            if (this.radarIconManager == null || this.minimap == null) {
                throw new IllegalStateException();
            }
            RadarElementReader elementReader = new RadarElementReader();
            RadarRenderProvider provider = new RadarRenderProvider();
            RadarRenderContext context = new RadarRenderContext();
            return new RadarRenderer(this.radarIconManager, this.minimap, elementReader, provider, context);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

