/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.compat.mods;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import xaero.lib.client.graphics.util.ImmediateRenderUtil;

public class ImmediatelyFastHelper {
    public static void triggerBatchingBuffersFlush(PoseStack matrixStack) {
        ImmediateRenderUtil.coloredRectangle((Matrix4f)matrixStack.last().pose(), (float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f, (int)0);
    }
}

