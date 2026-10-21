/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics.shader;

import java.nio.ByteBuffer;
import org.joml.Vector2f;
import xaero.lib.client.graphics.shader.CustomUniformValueType;

public class BuiltInCustomUniformValueTypes {
    public static final CustomUniformValueType<Float> FLOAT = new CustomUniformValueType<Float>(4, ByteBuffer::putFloat);
    public static final CustomUniformValueType<Vector2f> VEC_2F = new CustomUniformValueType<Vector2f>(8, (buffer, value) -> {
        buffer.putFloat(value.x);
        buffer.putFloat(value.y);
    });
    public static final CustomUniformValueType<Integer> INT = new CustomUniformValueType<Integer>(4, ByteBuffer::putInt);
}

