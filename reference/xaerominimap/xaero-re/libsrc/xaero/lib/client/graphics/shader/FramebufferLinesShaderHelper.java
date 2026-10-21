/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics.shader;

import org.joml.Vector2f;
import xaero.lib.client.graphics.shader.BuiltInCustomUniforms;

public class FramebufferLinesShaderHelper {
    private static Vector2f cachedFrameSize;

    public static void setFrameSize(float width, float height) {
        if (cachedFrameSize != null && FramebufferLinesShaderHelper.cachedFrameSize.x == width && FramebufferLinesShaderHelper.cachedFrameSize.y == height) {
            return;
        }
        cachedFrameSize = new Vector2f(width, height);
        BuiltInCustomUniforms.FRAME_SIZE.setValue(cachedFrameSize);
    }
}

