/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.OptionalDouble;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.PreparedRenderType;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import xaero.lib.client.graphics.GpuTextureAndView;
import xaero.lib.client.graphics.util.TextureUtils;

public class MinimapTexture
extends SimpleTexture {
    public final ByteBuffer buffer = TextureUtils.allocateByteBuffer((int)0x100000, (ByteOrder)ByteOrder.LITTLE_ENDIAN);
    public final IntBuffer intBuffer = this.buffer.asIntBuffer();
    boolean loaded = false;
    private GpuTextureAndView textureAndView;
    private PreparedRenderType.Texture textureAndSampler;

    public void loadIfNeeded() throws IOException {
        if (!this.loaded) {
            this.load(Minecraft.getInstance().getResourceManager());
            this.loaded = true;
        }
    }

    public MinimapTexture(Identifier location) throws IOException {
        super(location);
    }

    public void load(ResourceManager resourceManager_1) throws IOException {
        this.texture = RenderSystem.getDevice().createTexture("minimap_safe_mode", 5, GpuFormat.RGBA8_UNORM, 512, 512, 1, 1);
        this.textureView = RenderSystem.getDevice().createTextureView(this.texture);
        this.textureAndView = new GpuTextureAndView(this.texture, this.textureView);
        this.textureAndSampler = new PreparedRenderType.Texture("Sampler0", this.textureView, RenderSystem.getDevice().createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.of(1.0)));
    }

    public GpuTextureAndView getTextureAndView() {
        return this.textureAndView;
    }

    public PreparedRenderType.Texture getTas() {
        return this.textureAndSampler;
    }
}

