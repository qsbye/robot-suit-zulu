/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics.shader;

import java.nio.ByteBuffer;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.DynamicGpuDataStorage;
import xaero.lib.client.graphics.shader.CustomUniformValueType;

public class CustomUniformValue<T>
implements DynamicGpuDataStorage.DynamicGpuData {
    private final T value;
    private final CustomUniformValueType<T> valueType;

    public CustomUniformValue(T value, CustomUniformValueType<T> valueType) {
        this.value = value;
        this.valueType = valueType;
    }

    public T getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CustomUniformValue that = (CustomUniformValue)o;
        return Objects.equals(this.value, that.value);
    }

    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    public void write(@Nonnull ByteBuffer buffer) {
        this.valueType.getWriter().accept(buffer, (ByteBuffer)this.value);
    }
}

