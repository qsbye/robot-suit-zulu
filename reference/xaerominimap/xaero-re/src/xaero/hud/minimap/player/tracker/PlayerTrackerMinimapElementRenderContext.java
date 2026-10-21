/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.player.tracker;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;

public class PlayerTrackerMinimapElementRenderContext {
    public VertexConsumer coloredBackgroundConsumer;
    public MultiTextureRenderTypeRenderer uniqueTextureUIObjectRenderer;
    public ResourceKey<Level> renderEntityDimId;
    public ResourceKey<Level> mapDimId;
    public float iconScale = 1.0f;
}

