/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import xaero.hud.minimap.radar.icon.creator.render.form.model.RadarIconModelPrerenderer;
import xaero.hud.render.TextureLocations;
import xaero.lib.XaeroLib;
import xaero.lib.client.graphics.ITextureBinding;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.client.graphics.XaeroRenderType;

public class CustomRenderTypes {
    public static final RenderType GUI_NEAREST;
    public static final RenderType GUI_NEAREST_PRE;
    public static final RenderType GUI_BILINEAR;
    public static final RenderType GUI_BILINEAR_PRE;
    public static final RenderType GUI_BILINEAR_NO_DEPTH;
    public static final RenderType COLORED_WAYPOINTS_BGS;
    public static final RenderType MAP_CHUNK_OVERLAY;
    public static final RenderType MAP_LINES;
    public static final RenderType MINIMAP_WORLD_MAP;
    public static final RenderType MINIMAP_WORLD_MAP_ZOOM;
    public static final RenderType MINIMAP;
    public static final RenderType MINIMAP_ZOOM;
    public static final RenderType RADAR_NAME_BGS;
    public static final RenderType DEPTH_CLEAR;

    public static RenderType entityIconRenderType(Map<String, ITextureBinding> textures, TextureTransform textureTransform, boolean useOverlay, boolean affectsCrumbling, boolean sortOnUpload, LayeringTransform layeringTransform, RenderPipeline renderPipeline) {
        RenderSetup.RenderSetupBuilder builder = RenderSetup.builder((RenderPipeline)renderPipeline);
        textures.forEach((name, binding) -> builder.withTexture(name, binding.xaero_lib_getLocation(), binding.xaero_lib_getSampler()));
        builder.withTexture("Sampler2", RadarIconModelPrerenderer.LIGHTMAP, () -> RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
        builder.setTextureTransform(textureTransform);
        if (useOverlay) {
            builder.useOverlay();
        }
        if (affectsCrumbling) {
            builder.affectsCrumbling();
        }
        if (sortOnUpload) {
            builder.sortOnUpload();
        }
        builder.setLayeringTransform(layeringTransform);
        return XaeroRenderType.createRenderType((String)"xaero_entity_icon", (RenderSetup.RenderSetupBuilder)builder);
    }

    public static void applyFixedOrder() {
        XaeroBufferProvider bufferProvider = XaeroLib.INSTANCE.getClient().getBufferProvider();
        bufferProvider.addToFixedOrder(GUI_NEAREST);
        bufferProvider.addToFixedOrder(GUI_BILINEAR);
        bufferProvider.addToFixedOrder(GUI_BILINEAR_PRE);
        bufferProvider.addToFixedOrder(GUI_BILINEAR_NO_DEPTH);
        bufferProvider.addToFixedOrder(COLORED_WAYPOINTS_BGS);
        bufferProvider.addToFixedOrder(MAP_CHUNK_OVERLAY);
        bufferProvider.addToFixedOrder(MAP_LINES);
        bufferProvider.addToFixedOrder(RADAR_NAME_BGS);
    }

    static {
        Supplier<GpuSampler> NEAREST_NO_MIPMAPS = () -> RenderSystem.getDevice().createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST, FilterMode.NEAREST, 1, OptionalDouble.empty());
        Supplier<GpuSampler> NEAREST_MIPMAPS = () -> RenderSystem.getDevice().createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST, FilterMode.NEAREST, 1, OptionalDouble.of(4.0));
        Supplier<GpuSampler> BILINEAR_NO_MIPMAPS = () -> RenderSystem.getDevice().createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.of(1.0));
        Supplier<GpuSampler> BILINEAR_MIPMAPS = () -> RenderSystem.getDevice().createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.of(5.0));
        Supplier<GpuSampler> MIN_LINEAR_MAG_NEAREST = () -> RenderSystem.getDevice().createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.NEAREST, 1, OptionalDouble.of(1.0));
        GUI_NEAREST = XaeroRenderType.createRenderType((String)"xaero_gui_nearest", (RenderSetup.RenderSetupBuilder)RenderSetup.builder((RenderPipeline)XaeroRenderType.RP_POSITION_COLOR_TEX_TRANSLUCENT).withTexture("Sampler0", TextureLocations.GUI_TEXTURES, NEAREST_NO_MIPMAPS));
        GUI_NEAREST_PRE = XaeroRenderType.createRenderType((String)"xaero_gui_nearest", (RenderSetup.RenderSetupBuilder)RenderSetup.builder((RenderPipeline)XaeroRenderType.RP_POSITION_COLOR_TEX_PREMULTIPLIED).withTexture("Sampler0", TextureLocations.GUI_TEXTURES, NEAREST_NO_MIPMAPS));
        GUI_BILINEAR = XaeroRenderType.createRenderType((String)"xaero_gui_bilinear", (RenderSetup.RenderSetupBuilder)RenderSetup.builder((RenderPipeline)XaeroRenderType.RP_POSITION_COLOR_TEX_TRANSLUCENT).withTexture("Sampler0", TextureLocations.GUI_TEXTURES, BILINEAR_MIPMAPS));
        GUI_BILINEAR_NO_DEPTH = XaeroRenderType.createRenderType((String)"xaero_gui_no_depth", (RenderSetup.RenderSetupBuilder)RenderSetup.builder((RenderPipeline)XaeroRenderType.RP_POSITION_COLOR_TEX_TRANSLUCENT_NO_DEPTH).withTexture("Sampler0", TextureLocations.GUI_TEXTURES, BILINEAR_MIPMAPS));
        GUI_BILINEAR_PRE = XaeroRenderType.createRenderType((String)"xaero_gui_bilinear_pre", (RenderSetup.RenderSetupBuilder)RenderSetup.builder((RenderPipeline)XaeroRenderType.RP_POSITION_COLOR_TEX_PREMULTIPLIED).withTexture("Sampler0", TextureLocations.GUI_TEXTURES, BILINEAR_MIPMAPS));
        COLORED_WAYPOINTS_BGS = XaeroRenderType.createRenderType((String)"xaero_colored_waypoints", (RenderSetup.RenderSetupBuilder)RenderSetup.builder((RenderPipeline)XaeroRenderType.RP_POSITION_COLOR_TRANSLUCENT).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING_FORWARD));
        RADAR_NAME_BGS = XaeroRenderType.createRenderType((String)"xaero_radar_name_bg", (RenderSetup.RenderSetupBuilder)RenderSetup.builder((RenderPipeline)XaeroRenderType.RP_POSITION_COLOR_TRANSLUCENT).setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING_FORWARD));
        MAP_CHUNK_OVERLAY = XaeroRenderType.createRenderType((String)"xaero_chunk_overlay", (RenderSetup.RenderSetupBuilder)RenderSetup.builder((RenderPipeline)XaeroRenderType.RP_POSITION_COLOR_TRANSLUCENT));
        MAP_LINES = XaeroRenderType.createRenderType((String)"xaero_lines", (RenderSetup.RenderSetupBuilder)RenderSetup.builder((RenderPipeline)XaeroRenderType.RP_LINES));
        DEPTH_CLEAR = XaeroRenderType.createRenderType((String)"xaero_depth_clear", (RenderSetup.RenderSetupBuilder)RenderSetup.builder((RenderPipeline)XaeroRenderType.RP_DEPTH_CLEAR));
        MINIMAP_WORLD_MAP = XaeroRenderType.createRenderType((String)"xaero_minimap_worldmap", (RenderSetup.RenderSetupBuilder)RenderSetup.builder((RenderPipeline)XaeroRenderType.RP_MINIMAP_MAP).withTexture("Sampler0", TextureLocations.GUI_TEXTURES, MIN_LINEAR_MAG_NEAREST));
        MINIMAP_WORLD_MAP_ZOOM = XaeroRenderType.createRenderType((String)"xaero_minimap_worldmap_zoom", (RenderSetup.RenderSetupBuilder)RenderSetup.builder((RenderPipeline)XaeroRenderType.RP_MINIMAP_MAP).withTexture("Sampler0", TextureLocations.GUI_TEXTURES, BILINEAR_MIPMAPS));
        MINIMAP = XaeroRenderType.createRenderType((String)"xaero_minimap", (RenderSetup.RenderSetupBuilder)RenderSetup.builder((RenderPipeline)XaeroRenderType.RP_POSITION_COLOR_TEX_PREMULTIPLIED).withTexture("Sampler0", TextureLocations.GUI_TEXTURES, MIN_LINEAR_MAG_NEAREST));
        MINIMAP_ZOOM = XaeroRenderType.createRenderType((String)"xaero_minimap_zoom", (RenderSetup.RenderSetupBuilder)RenderSetup.builder((RenderPipeline)XaeroRenderType.RP_POSITION_COLOR_TEX_PREMULTIPLIED).withTexture("Sampler0", TextureLocations.GUI_TEXTURES, BILINEAR_MIPMAPS));
    }
}

