/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.graphics.renderer.multitexture;

import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.function.Consumer;
import net.minecraft.client.renderer.rendertype.RenderType;
import xaero.common.graphics.renderer.multitexture.MultiTextureRenderTypeRenderer;

public class MultiTextureRenderTypeRendererProvider {
    private Deque<MultiTextureRenderTypeRenderer> availableRenderers = new ArrayDeque<MultiTextureRenderTypeRenderer>();
    private HashSet<MultiTextureRenderTypeRenderer> usedRenderers;

    public MultiTextureRenderTypeRendererProvider(int rendererCount) {
        for (int i = 0; i < rendererCount; ++i) {
            this.availableRenderers.add(new MultiTextureRenderTypeRenderer());
        }
        this.usedRenderers = new HashSet();
    }

    public MultiTextureRenderTypeRenderer getRenderer(RenderType renderType) {
        return this.getRenderer("Sampler0", renderType);
    }

    public MultiTextureRenderTypeRenderer getRenderer(String textureBindTarget, RenderType renderType) {
        return this.getRenderer(textureBindTarget, null, renderType);
    }

    public MultiTextureRenderTypeRenderer getRenderer(Consumer<GpuTextureView> textureFinalizer, RenderType renderType) {
        return this.getRenderer("Sampler0", textureFinalizer, renderType);
    }

    public MultiTextureRenderTypeRenderer getRenderer(String textureBindTarget, Consumer<GpuTextureView> textureFinalizer, RenderType renderType) {
        if (this.availableRenderers.isEmpty()) {
            throw new RuntimeException("No renderers available!");
        }
        MultiTextureRenderTypeRenderer renderer = this.availableRenderers.removeFirst();
        renderer.init(textureBindTarget, textureFinalizer, renderType);
        this.usedRenderers.add(renderer);
        return renderer;
    }

    public void draw(MultiTextureRenderTypeRenderer renderer) {
        if (!this.usedRenderers.remove(renderer)) {
            throw new RuntimeException("The renderer requested for drawing was not provided by this provider!");
        }
        renderer.draw();
        this.availableRenderers.add(renderer);
    }
}

