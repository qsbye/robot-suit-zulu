/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.compass.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import xaero.common.HudMod;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.compass.render.CardinalDirection;
import xaero.hud.minimap.element.render.over.MinimapElementOverMapRendererHandler;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.render.util.RenderBufferUtil;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.client.graphics.font.util.FontUtils;
import xaero.lib.common.config.option.ConfigOption;

public class CompassRenderer {
    private final HudMod modMain;
    private final Minecraft mc;
    private double[] partialDest;

    public CompassRenderer(HudMod modMain, Minecraft mc) {
        this.modMain = modMain;
        this.mc = mc;
        this.partialDest = new double[2];
    }

    public void drawCompass(PoseStack matrixStack, int specW, int specH, double ps, double pc, double zoom, boolean circle, float minimapScale, boolean background, XaeroBufferProvider textRenderTypeBuffer, VertexConsumer nameBgBuilder) {
        ClientConfigManager configManager = this.modMain.getHudConfigs().getClientConfigManager();
        int compassLocationConfig = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.COMPASS_LOCATION);
        if (compassLocationConfig == 0) {
            return;
        }
        int shadowColorConfig = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.COMPASS_SHADOW_COLOR);
        WaypointColor defaultColor = WaypointColor.fromIndex(shadowColorConfig);
        MinimapRendererHelper rendererHelper = this.modMain.getMinimap().getMinimapFBORenderer().getHelper();
        int northShadowColorConfig = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.COMPASS_NORTH_SHADOW_COLOR);
        int effectiveNorthShadowColorConfig = northShadowColorConfig == -1 ? shadowColorConfig : northShadowColorConfig;
        for (int i = 0; i < 4; ++i) {
            WaypointColor effectiveColor;
            double offX = (i & 1) * (i == 1 ? 10000 : -10000);
            double offY = (i + 1 & 1) * (i == 2 ? 10000 : -10000);
            matrixStack.pushPose();
            MinimapElementOverMapRendererHandler.translatePosition(matrixStack, specW, specH, specW, specH, ps, pc, offX, offY, zoom, circle, this.partialDest);
            matrixStack.translate(-1.0f, -1.0f, 0.0f);
            matrixStack.scale(minimapScale, minimapScale, 1.0f);
            Component initials = CardinalDirection.values()[i].getInitials();
            int halfW = this.mc.font.width((FormattedText)initials) / 2 - 1;
            WaypointColor waypointColor = effectiveColor = i == 0 ? WaypointColor.fromIndex(effectiveNorthShadowColorConfig) : defaultColor;
            if (background) {
                int addedFrame = halfW > 3 ? halfW - 3 : 0;
                RenderBufferUtil.addColoredRect(matrixStack.last().pose(), nameBgBuilder, -4 - addedFrame, -4 - addedFrame, 9 + 2 * addedFrame, 9 + 2 * addedFrame, 0x90000000 | effectiveColor.getHex() & 0xFFFFFF);
            }
            FontUtils.drawNormalText((PoseStack)matrixStack, (Component)initials, (float)(-halfW + 1), (float)-2.0f, (int)effectiveColor.getHex(), (boolean)false, (XaeroBufferProvider)textRenderTypeBuffer);
            matrixStack.translate(0.0f, 0.0f, 1.0f);
            FontUtils.drawNormalText((PoseStack)matrixStack, (Component)initials, (float)(-halfW), (float)-3.0f, (int)-1, (boolean)false, (XaeroBufferProvider)textRenderTypeBuffer);
            matrixStack.popPose();
        }
        matrixStack.translate(0.0f, 0.0f, 2.0f);
    }
}

