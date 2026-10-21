/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.render.util;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.lang.reflect.Method;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import xaero.lib.client.graphics.IGameRenderer;
import xaero.lib.common.reflection.util.ReflectionUtils;

public class GuiRenderUtil {
    private static Method submitBlitMethod;

    public static void flushGUI() {
        IGameRenderer gameRenderer = (IGameRenderer)Minecraft.getInstance().gameRenderer;
        gameRenderer.xaero_lib_getGuiRenderer().render();
    }

    public static void submitBlit(GuiGraphicsExtractor guiGraphics, RenderPipeline pipeline, GpuTextureView texture, GpuSampler gpuSampler, int x0, int y0, int x1, int y1, float u0, float u1, float v0, float v1, int color) {
        if (submitBlitMethod == null) {
            submitBlitMethod = ReflectionUtils.getMethodReflection(GuiGraphicsExtractor.class, "innerBlit", "(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lcom/mojang/blaze3d/textures/GpuTextureView;Lcom/mojang/blaze3d/textures/GpuSampler;IIIIFFFFI)V", RenderPipeline.class, GpuTextureView.class, GpuSampler.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE);
        }
        ReflectionUtils.getReflectMethodValue(guiGraphics, submitBlitMethod, pipeline, texture, gpuSampler, x0, y0, x1, y1, Float.valueOf(u0), Float.valueOf(u1), Float.valueOf(v0), Float.valueOf(v1), color);
    }
}

