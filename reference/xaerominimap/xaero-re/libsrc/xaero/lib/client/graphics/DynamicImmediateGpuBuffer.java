/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.device.GpuDevice;
import java.util.ArrayList;
import java.util.List;
import xaero.lib.XaeroLib;
import xaero.lib.client.graphics.util.OpenGLUtil;

public class DynamicImmediateGpuBuffer {
    private GpuBuffer buffer;
    private final List<GpuBuffer> buffersToDelete;
    private long position;
    private final int usageFlags;
    private final boolean usesImmutableBufferStorage;

    public DynamicImmediateGpuBuffer(int initialSize, int usageFlags) {
        this.usageFlags = usageFlags;
        this.buffersToDelete = new ArrayList<GpuBuffer>();
        this.usesImmutableBufferStorage = RenderSystem.getDevice().getDeviceInfo().backendName().equals("OpenGL") ? OpenGLUtil.usesImmutableStorage() : true;
        this.ensureBufferSize(initialSize);
    }

    private void ensureBufferSize(long requiredSize) {
        if (this.buffer != null && this.buffer.size() >= requiredSize) {
            return;
        }
        long newSize = this.buffer == null ? requiredSize : Math.max(requiredSize, this.buffer.size() * 2L);
        GpuDevice gpuDevice = RenderSystem.getDevice();
        int additionalUsageFlags = this.usesImmutableBufferStorage ? 0 : 6;
        GpuBuffer resizedBuffer = gpuDevice.createBuffer(null, this.usageFlags | 8 | additionalUsageFlags, newSize);
        if (this.buffer != null) {
            this.buffersToDelete.add(this.buffer);
            XaeroLib.LOGGER.info("Resized a dynamic immediate buffer to {}!", (Object)newSize);
        }
        this.buffer = resizedBuffer;
        this.position = 0L;
    }

    public GpuBufferSlice allocateSlice(long size) {
        this.ensureBufferSize(this.position + size);
        GpuBufferSlice slice = this.buffer.slice(this.position, size);
        this.position += size;
        return slice;
    }

    public void clear() {
        this.position = 0L;
        for (GpuBuffer gpuBuffer : this.buffersToDelete) {
            gpuBuffer.close();
        }
        this.buffersToDelete.clear();
    }

    public GpuBuffer getGpuBuffer() {
        return this.buffer;
    }
}

