/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.player.tracker;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;
import xaero.hud.render.util.RenderBufferUtil;

public class PlayerTrackerIconRenderer {
    public boolean isPlayerUpsideDown(Player player) {
        Component playerCustomName = player.getCustomName();
        return playerCustomName != null && PlayerTrackerIconRenderer.isUpsideDownName(playerCustomName.getString());
    }

    protected static boolean isUpsideDownName(String playerName) {
        return "Dinnerbone".equals(playerName) || "Grumm".equals(playerName);
    }

    public void renderIcon(Minecraft mc, MultiTextureRenderTypeRenderer renderer, PoseStack matrixStack, Player player, Identifier skinTextureLocation, PlayerInfo playerInfo, float alpha) {
        boolean upsideDown = player != null && this.isPlayerUpsideDown(player);
        int textureY = 8 + (!upsideDown ? 8 : 0);
        int textureH = 8 * (!upsideDown ? -1 : 1);
        AbstractTexture texture = mc.getTextureManager().getTexture(skinTextureLocation);
        if (texture == null) {
            return;
        }
        GpuTextureView textureId = texture.getTextureView();
        BufferBuilder bufferbuilder = renderer.begin(textureId);
        RenderBufferUtil.addTexturedColoredRect(matrixStack.last().pose(), (VertexConsumer)bufferbuilder, -4.0f, -4.0f, 8, textureY, 8, 8, 8, textureH, 1.0f, 1.0f, 1.0f, alpha, 64.0f);
        if (playerInfo == null) {
            return;
        }
        if (!playerInfo.showHat()) {
            return;
        }
        textureY = 8 + (!upsideDown ? 8 : 0);
        textureH = 8 * (!upsideDown ? -1 : 1);
        RenderBufferUtil.addTexturedColoredRect(matrixStack.last().pose(), (VertexConsumer)bufferbuilder, -4.0f, -4.0f, 40, textureY, 8, 8, 8, textureH, 1.0f, 1.0f, 1.0f, alpha, 64.0f);
    }
}

