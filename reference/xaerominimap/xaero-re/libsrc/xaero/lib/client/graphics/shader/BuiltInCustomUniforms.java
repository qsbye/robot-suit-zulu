/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics.shader;

import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.UniformType;
import org.joml.Vector2f;
import xaero.lib.client.graphics.shader.BuiltInCustomUniformValueTypes;
import xaero.lib.client.graphics.shader.CustomUniform;
import xaero.lib.client.graphics.shader.CustomUniforms;

public class BuiltInCustomUniforms {
    public static final CustomUniform<Float> DISCARD_ALPHA = new CustomUniform<Float>(new BindGroupLayout.UniformDescription("DiscardAlphaBlock", UniformType.UNIFORM_BUFFER), BuiltInCustomUniformValueTypes.FLOAT, 32);
    public static final CustomUniform<Vector2f> FRAME_SIZE = new CustomUniform<Vector2f>(new BindGroupLayout.UniformDescription("FrameSizeBlock", UniformType.UNIFORM_BUFFER), BuiltInCustomUniformValueTypes.VEC_2F, 32);
    public static final CustomUniform<Float> BRIGHTNESS = new CustomUniform<Float>(new BindGroupLayout.UniformDescription("BrightnessBlock", UniformType.UNIFORM_BUFFER), BuiltInCustomUniformValueTypes.FLOAT, 32);
    public static final CustomUniform<Integer> WITH_LIGHT = new CustomUniform<Integer>(new BindGroupLayout.UniformDescription("WithLightBlock", UniformType.UNIFORM_BUFFER), BuiltInCustomUniformValueTypes.INT, 32);

    private static void registerAll() {
        CustomUniforms.register(DISCARD_ALPHA);
        CustomUniforms.register(FRAME_SIZE);
        CustomUniforms.register(BRIGHTNESS);
        CustomUniforms.register(WITH_LIGHT);
    }

    static {
        BuiltInCustomUniforms.registerAll();
    }
}

