/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics.shader;

import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import java.util.HashMap;
import java.util.Map;
import xaero.lib.client.graphics.shader.CustomUniform;

public class CustomUniforms {
    private static Map<BindGroupLayout.UniformDescription, CustomUniform<?>> UNIFORMS = new HashMap();

    public static void register(CustomUniform<?> uniform) {
        if (UNIFORMS.containsKey(uniform.getDescription())) {
            throw new IllegalArgumentException("Custom uniform already registered: " + String.valueOf(uniform.getDescription()));
        }
        UNIFORMS.put(uniform.getDescription(), uniform);
    }

    public static GpuBufferSlice getUpdatedUniformBuffer(BindGroupLayout.UniformDescription uniformDescription) {
        CustomUniform<?> uniform = UNIFORMS.get(uniformDescription);
        if (uniform == null) {
            return null;
        }
        return uniform.getBufferSlice();
    }

    public static boolean isCustom(BindGroupLayout.UniformDescription uniform) {
        return UNIFORMS.containsKey(uniform);
    }

    public static void endFrame() {
        for (CustomUniform<?> uniform : UNIFORMS.values()) {
            uniform.endFrame();
        }
    }
}

