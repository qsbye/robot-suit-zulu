/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics.shader;

import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.UniformType;
import net.minecraft.client.renderer.DynamicGpuDataStorageMapped;
import xaero.lib.client.graphics.shader.CustomUniformValue;
import xaero.lib.client.graphics.shader.CustomUniformValueType;

public class CustomUniform<T> {
    private final BindGroupLayout.UniformDescription description;
    private final CustomUniformValueType<T> valueType;
    private final int initialBlockCount;
    private DynamicGpuDataStorageMapped<CustomUniformValue<T>> storage;
    private CustomUniformValue<T> value;

    public CustomUniform(BindGroupLayout.UniformDescription description, CustomUniformValueType<T> valueType, int initialBlockCount) {
        this.description = description;
        this.valueType = valueType;
        this.initialBlockCount = initialBlockCount;
    }

    public BindGroupLayout.UniformDescription getDescription() {
        return this.description;
    }

    public String name() {
        return this.description.name();
    }

    public UniformType type() {
        return this.description.type();
    }

    public T getValue() {
        return this.value == null ? null : (T)this.value.getValue();
    }

    public void setValue(T value) {
        Object currentValue;
        if (value == null) {
            throw new IllegalArgumentException();
        }
        Object t = currentValue = this.value == null ? null : (Object)this.value.getValue();
        if (currentValue != null && this.valueType.checkEquation(currentValue, value)) {
            return;
        }
        this.value = new CustomUniformValue<T>(value, this.valueType);
    }

    public GpuBufferSlice getBufferSlice() {
        if (this.storage == null) {
            this.storage = new DynamicGpuDataStorageMapped(this.description.name(), this.valueType.getSize(), 128, this.initialBlockCount);
        }
        return this.storage.writeData(this.value);
    }

    public void endFrame() {
        if (this.storage != null) {
            this.storage.endFrame();
        }
    }
}

