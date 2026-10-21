/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.textures.GpuTexture;

public interface IPlatformRenderDeviceHelper {
    default public GpuDevice getRealDevice() {
        return RenderSystem.getDevice();
    }

    default public GpuTexture getRealTexture(GpuTexture texture) {
        return texture;
    }
}

