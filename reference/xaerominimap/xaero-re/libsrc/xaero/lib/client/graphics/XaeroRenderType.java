/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.BlendFactor;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.UniformType;
import com.mojang.renderpearl.api.textures.AddressMode;
import com.mojang.renderpearl.api.textures.FilterMode;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.BiFunction;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import xaero.lib.client.graphics.IRenderSetup;
import xaero.lib.client.graphics.ITextureBinding;
import xaero.lib.client.graphics.shader.BuiltInCustomUniforms;
import xaero.lib.client.graphics.shader.LibShaders;
import xaero.lib.common.reflection.util.ReflectionUtils;

public class XaeroRenderType {
    public static final VertexFormat POSITION_COLOR_TEX = VertexFormat.builder((int)0).addAttribute("Position", GpuFormat.RGB32_FLOAT).addAttribute("Color", GpuFormat.RGBA8_UNORM).addAttribute("UV0", GpuFormat.RG32_FLOAT).build();
    protected static final BlendFunction DEFAULT_TRANSLUCENT_TRANSPARENCY = new BlendFunction(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA, BlendFactor.ONE, BlendFactor.ONE_MINUS_SRC_ALPHA);
    protected static final BlendFunction LINES_TRANSPARENCY = new BlendFunction(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA, BlendFactor.ONE, BlendFactor.ONE_MINUS_SRC_ALPHA);
    protected static final BlendFunction PREMULTIPLIED_TRANSPARENCY = new BlendFunction(BlendFactor.ONE, BlendFactor.ONE_MINUS_SRC_ALPHA, BlendFactor.ONE, BlendFactor.ONE_MINUS_SRC_ALPHA);
    protected static final BlendFunction NEGATIVE_TRANSPARENCY = new BlendFunction(BlendFactor.ONE_MINUS_DST_COLOR, BlendFactor.ZERO, BlendFactor.ZERO, BlendFactor.ONE);
    protected static final BlendFunction DEST_TRANSPARENCY = new BlendFunction(BlendFactor.ONE, BlendFactor.ZERO, BlendFactor.ZERO, BlendFactor.ONE);
    protected static final BlendFunction REPLACE_TRANSPARENCY = new BlendFunction(BlendFactor.SRC_ALPHA, BlendFactor.ZERO, BlendFactor.ONE, BlendFactor.ZERO);
    protected static final BlendFunction ADD_ALPHA_TRANSPARENCY = new BlendFunction(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA, BlendFactor.ONE, BlendFactor.ONE);
    protected static final ColorTargetState CT_DEFAULT_TRANSLUCENT_TRANSPARENCY = new ColorTargetState(DEFAULT_TRANSLUCENT_TRANSPARENCY);
    protected static final ColorTargetState CT_LINES_TRANSPARENCY = new ColorTargetState(LINES_TRANSPARENCY);
    protected static final ColorTargetState CT_PREMULTIPLIED_TRANSPARENCY = new ColorTargetState(PREMULTIPLIED_TRANSPARENCY);
    protected static final ColorTargetState CT_NEGATIVE_TRANSPARENCY = new ColorTargetState(NEGATIVE_TRANSPARENCY);
    protected static final ColorTargetState CT_DEST_TRANSPARENCY = new ColorTargetState(DEST_TRANSPARENCY);
    protected static final ColorTargetState CT_REPLACE_TRANSPARENCY = new ColorTargetState(REPLACE_TRANSPARENCY);
    protected static final ColorTargetState CT_ADD_ALPHA_TRANSPARENCY = new ColorTargetState(ADD_ALPHA_TRANSPARENCY);
    protected static final BiFunction<FilterMode, FilterMode, GpuSampler> SIMPLER_SAMPLER = Util.memoize((minFilter, magFilter) -> RenderSystem.getDevice().createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, minFilter, magFilter, 1, OptionalDouble.of(1.0)));
    public static final RenderPipeline RP_POSITION_TEX_ALPHA;
    public static final RenderPipeline RP_POSITION_TEX_ALPHA_PRE;
    public static final RenderPipeline RP_POSITION_TEX_ALPHA_NO_BLEND;
    public static final RenderPipeline RP_POSITION_TEX_NO_ALPHA;
    public static final RenderPipeline RP_POSITION_TEX_NO_ALPHA_PRE;
    public static final RenderPipeline RP_POSITION_TEX_NO_ALPHA_NO_DEPTH;
    public static final RenderPipeline RP_POSITION_TEX_NO_ALPHA_NO_BLEND;
    public static final RenderPipeline RP_POSITION_TEX_ALPHA_REPLACE;
    public static final RenderPipeline RP_POSITION_TEX_ALPHA_NO_CULL;
    public static final RenderPipeline RP_POSITION;
    public static final RenderPipeline RP_POSITION_NO_DEPTH;
    public static final RenderPipeline RP_NEGATIVE_COLOR;
    public static final RenderPipeline RP_ICON_OUTLINE;
    public static final RenderPipeline RP_POSITION_COLOR_TEX_TRANSLUCENT;
    public static final RenderPipeline RP_POSITION_COLOR_TEX_TRANSLUCENT_NO_DEPTH;
    public static final RenderPipeline RP_POSITION_COLOR_TEX_TRANSLUCENT_CULL;
    public static final RenderPipeline RP_POSITION_COLOR_TEX_PREMULTIPLIED;
    public static final RenderPipeline RP_POSITION_COLOR_TEX_NO_BLEND;
    public static final RenderPipeline RP_POSITION_COLOR_TRANSLUCENT;
    public static final RenderPipeline RP_POSITION_COLOR_NO_BLEND;
    public static final RenderPipeline RP_POSITION_COLOR_NO_CULL;
    public static final RenderPipeline RP_LINES;
    public static final RenderPipeline RP_DEPTH_CLEAR;
    public static final RenderPipeline RP_MAP;
    public static final RenderPipeline RP_MINIMAP_MAP;
    public static final RenderPipeline RP_MAP_BRANCH;
    public static final RenderPipeline RP_MAP_FRAME;
    public static final RenderPipeline RP_COLOR_FILLER;
    public static final RenderPipeline RP_MIPMAP;
    private static final Method renderTypeCreateMethod;

    public static RenderType createRenderType(String name, RenderSetup.RenderSetupBuilder renderSetupBuilder) {
        RenderSetup renderSetup = renderSetupBuilder.createRenderSetup();
        return (RenderType)ReflectionUtils.getReflectMethodValue(null, renderTypeCreateMethod, name, renderSetup);
    }

    public static GpuSampler getSimpleSampler(FilterMode filterMode) {
        return SIMPLER_SAMPLER.apply(filterMode, filterMode);
    }

    public static GpuSampler getSimpleSampler(FilterMode minFilter, FilterMode magFilter) {
        return SIMPLER_SAMPLER.apply(minFilter, magFilter);
    }

    public static RenderPipeline getBasicRenderPipeline() {
        return RenderPipelines.ENTITY_TRANSLUCENT;
    }

    public static Map<String, ITextureBinding> getSetupTextures(RenderSetup setup) {
        return ((IRenderSetup)setup).xaero_lib_getTextures();
    }

    public static TextureTransform getSetupTextureTransform(RenderSetup setup) {
        return ((IRenderSetup)setup).xaero_lib_getTextureTransform();
    }

    public static boolean getSetupUseOverlay(RenderSetup setup) {
        return ((IRenderSetup)setup).xaero_lib_getUseOverlay();
    }

    public static LayeringTransform getSetupLayeringTransform(RenderSetup setup) {
        return ((IRenderSetup)setup).xaero_lib_getLayeringTransform();
    }

    static {
        renderTypeCreateMethod = ReflectionUtils.getMethodReflection(RenderType.class, "create", "(Ljava/lang/String;Lnet/minecraft/client/renderer/rendertype/RenderSetup;)Lnet/minecraft/client/renderer/rendertype/RenderType;", String.class, RenderSetup.class);
        RenderPipeline.Snippet MATRICES_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withBindGroupLayout(BindGroupLayout.builder().withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER).withUniform("Projection", UniformType.UNIFORM_BUFFER).build()).buildSnippet();
        RenderPipeline.Snippet MATRICES_FOG_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withBindGroupLayout(BindGroupLayout.builder().withUniform("Fog", UniformType.UNIFORM_BUFFER).build()).buildSnippet();
        RenderPipeline.Snippet POSITION_TEX_NO_ALPHA_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withVertexShader(LibShaders.POSITION_TEX_NO_ALPHA_TEST).withFragmentShader(LibShaders.POSITION_TEX_NO_ALPHA_TEST).withVertexBinding(0, DefaultVertexFormat.POSITION_TEX).withPrimitiveTopology(PrimitiveTopology.QUADS).withBindGroupLayout(BindGroupLayout.builder().withUniform("Sampler0", UniformType.COMBINED_IMAGE_SAMPLER).build()).buildSnippet();
        RP_POSITION_TEX_NO_ALPHA = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_TEX_NO_ALPHA_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_tex_no_alpha")).withColorTargetState(CT_DEFAULT_TRANSLUCENT_TRANSPARENCY).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RP_POSITION_TEX_NO_ALPHA_PRE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_TEX_NO_ALPHA_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_tex_no_alpha_pre")).withColorTargetState(CT_PREMULTIPLIED_TRANSPARENCY).build();
        RP_POSITION_TEX_NO_ALPHA_NO_DEPTH = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_TEX_NO_ALPHA_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_tex_no_alpha_no_depth")).withColorTargetState(CT_DEFAULT_TRANSLUCENT_TRANSPARENCY).build();
        RP_POSITION_TEX_NO_ALPHA_NO_BLEND = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_TEX_NO_ALPHA_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_tex_no_alpha_no_blend")).withColorTargetState(ColorTargetState.DEFAULT).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RenderPipeline.Snippet RP_POSITION_TEX_ALPHA_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withVertexShader(LibShaders.POSITION_TEX_ALPHA_TEST).withFragmentShader(LibShaders.POSITION_TEX_ALPHA_TEST).withBindGroupLayout(BindGroupLayout.builder().withUniform(BuiltInCustomUniforms.DISCARD_ALPHA.name(), BuiltInCustomUniforms.DISCARD_ALPHA.type()).withUniform("Sampler0", UniformType.COMBINED_IMAGE_SAMPLER).build()).withVertexBinding(0, DefaultVertexFormat.POSITION_TEX).withPrimitiveTopology(PrimitiveTopology.QUADS).buildSnippet();
        RP_POSITION_TEX_ALPHA = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RP_POSITION_TEX_ALPHA_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_tex_alpha")).withColorTargetState(CT_DEFAULT_TRANSLUCENT_TRANSPARENCY).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RP_POSITION_TEX_ALPHA_PRE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RP_POSITION_TEX_ALPHA_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_tex_alpha_pre")).withColorTargetState(CT_PREMULTIPLIED_TRANSPARENCY).build();
        RP_ICON_OUTLINE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RP_POSITION_TEX_ALPHA_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/icon_outline")).withColorTargetState(CT_ADD_ALPHA_TRANSPARENCY).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RP_POSITION_TEX_ALPHA_NO_BLEND = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RP_POSITION_TEX_ALPHA_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_tex_alpha_no_blend")).withColorTargetState(ColorTargetState.DEFAULT).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RP_POSITION_TEX_ALPHA_REPLACE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RP_POSITION_TEX_ALPHA_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_tex_alpha_replace")).withColorTargetState(CT_REPLACE_TRANSPARENCY).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RP_POSITION_TEX_ALPHA_NO_CULL = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RP_POSITION_TEX_ALPHA_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_tex_alpha_no_cull")).withColorTargetState(CT_DEFAULT_TRANSLUCENT_TRANSPARENCY).withCull(false).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RP_POSITION = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_FOG_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos")).withVertexShader("core/position").withFragmentShader("core/position").withVertexBinding(0, DefaultVertexFormat.POSITION).withPrimitiveTopology(PrimitiveTopology.QUADS).withColorTargetState(CT_DEFAULT_TRANSLUCENT_TRANSPARENCY).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RP_POSITION_NO_DEPTH = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_FOG_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos")).withVertexShader("core/position").withFragmentShader("core/position").withVertexBinding(0, DefaultVertexFormat.POSITION).withPrimitiveTopology(PrimitiveTopology.QUADS).withColorTargetState(CT_DEFAULT_TRANSLUCENT_TRANSPARENCY).build();
        RP_NEGATIVE_COLOR = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_FOG_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_negative_color")).withVertexShader("core/position").withFragmentShader("core/position").withVertexBinding(0, DefaultVertexFormat.POSITION).withPrimitiveTopology(PrimitiveTopology.QUADS).withColorTargetState(CT_NEGATIVE_TRANSPARENCY).withColorTargetState(CT_NEGATIVE_TRANSPARENCY).build();
        RenderPipeline.Snippet POSITION_COLOR_TEX_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withVertexShader(LibShaders.POSITION_COLOR_TEX).withFragmentShader(LibShaders.POSITION_COLOR_TEX).withVertexBinding(0, POSITION_COLOR_TEX).withPrimitiveTopology(PrimitiveTopology.QUADS).withBindGroupLayout(BindGroupLayout.builder().withUniform("Sampler0", UniformType.COMBINED_IMAGE_SAMPLER).build()).buildSnippet();
        RenderPipeline.Snippet POSITION_COLOR_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withVertexShader(LibShaders.POSITION_COLOR).withFragmentShader(LibShaders.POSITION_COLOR).withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR).withPrimitiveTopology(PrimitiveTopology.QUADS).buildSnippet();
        RenderPipeline.Snippet POSITION_COLOR_NO_ALPHA_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withVertexShader(LibShaders.POSITION_COLOR_NO_ALPHA_TEST).withFragmentShader(LibShaders.POSITION_COLOR_NO_ALPHA_TEST).withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR).withPrimitiveTopology(PrimitiveTopology.QUADS).buildSnippet();
        RP_POSITION_COLOR_TEX_TRANSLUCENT = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_TEX_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_col_tex_translucent")).withColorTargetState(CT_DEFAULT_TRANSLUCENT_TRANSPARENCY).withCull(false).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RP_POSITION_COLOR_TEX_TRANSLUCENT_CULL = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_TEX_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_col_tex_translucent_cull")).withColorTargetState(CT_DEFAULT_TRANSLUCENT_TRANSPARENCY).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RP_POSITION_COLOR_TEX_TRANSLUCENT_NO_DEPTH = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_TEX_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_col_tex_translucent_no_depth")).withColorTargetState(CT_DEFAULT_TRANSLUCENT_TRANSPARENCY).withCull(false).build();
        RP_POSITION_COLOR_TEX_PREMULTIPLIED = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_TEX_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_col_tex_premultiplied")).withColorTargetState(CT_PREMULTIPLIED_TRANSPARENCY).withCull(false).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RP_POSITION_COLOR_TEX_NO_BLEND = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_TEX_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_col_tex_no_blend")).withColorTargetState(ColorTargetState.DEFAULT).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RP_POSITION_COLOR_TRANSLUCENT = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_col_translucent")).withColorTargetState(CT_DEFAULT_TRANSLUCENT_TRANSPARENCY).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RP_POSITION_COLOR_NO_BLEND = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_col_no_blend")).withColorTargetState(ColorTargetState.DEFAULT).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RP_POSITION_COLOR_NO_CULL = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/pos_col_translucent")).withColorTargetState(CT_DEFAULT_TRANSLUCENT_TRANSPARENCY).withCull(false).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RP_LINES = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/lines")).withVertexShader(LibShaders.FRAMEBUFFER_LINES).withFragmentShader(LibShaders.FRAMEBUFFER_LINES).withBindGroupLayout(BindGroupLayout.builder().withUniform(BuiltInCustomUniforms.FRAME_SIZE.name(), BuiltInCustomUniforms.FRAME_SIZE.type()).build()).withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH).withPrimitiveTopology(PrimitiveTopology.LINES).withColorTargetState(CT_LINES_TRANSPARENCY).withDepthStencilState(DepthStencilState.DEFAULT).build();
        RP_DEPTH_CLEAR = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_NO_ALPHA_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/depth_clear")).withColorTargetState(new ColorTargetState(Optional.of(DEFAULT_TRANSLUCENT_TRANSPARENCY), GpuFormat.RGBA8_UNORM, 0)).withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN, true)).build();
        RenderPipeline.Snippet RP_MAP_SNIPPET = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withVertexShader(LibShaders.WORLD_MAP).withFragmentShader(LibShaders.WORLD_MAP).withVertexBinding(0, DefaultVertexFormat.POSITION_TEX).withPrimitiveTopology(PrimitiveTopology.QUADS).withBindGroupLayout(BindGroupLayout.builder().withUniform("Sampler0", UniformType.COMBINED_IMAGE_SAMPLER).withUniform(BuiltInCustomUniforms.BRIGHTNESS.name(), BuiltInCustomUniforms.BRIGHTNESS.type()).withUniform(BuiltInCustomUniforms.WITH_LIGHT.name(), BuiltInCustomUniforms.WITH_LIGHT.type()).build()).withColorTargetState(CT_DEST_TRANSPARENCY).withCull(false).withDepthStencilState(DepthStencilState.DEFAULT).buildSnippet();
        RP_MAP = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RP_MAP_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/map")).withColorTargetState(CT_DEST_TRANSPARENCY).build();
        RP_MINIMAP_MAP = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{RP_MAP_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/minimap_map")).withColorTargetState(CT_PREMULTIPLIED_TRANSPARENCY).build();
        RP_MAP_BRANCH = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/map_branch")).withVertexShader(LibShaders.WORLD_MAP_BRANCH).withFragmentShader(LibShaders.WORLD_MAP_BRANCH).withVertexBinding(0, DefaultVertexFormat.POSITION_TEX).withPrimitiveTopology(PrimitiveTopology.QUADS).withBindGroupLayout(BindGroupLayout.builder().withUniform("Sampler0", UniformType.COMBINED_IMAGE_SAMPLER).build()).withColorTargetState(ColorTargetState.DEFAULT).withCull(false).build();
        RP_MAP_FRAME = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_TEX_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/map_frame")).withColorTargetState(CT_DEST_TRANSPARENCY).withCull(false).withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false)).build();
        RP_COLOR_FILLER = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{POSITION_COLOR_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/color_filler")).withColorTargetState(CT_DEFAULT_TRANSLUCENT_TRANSPARENCY).withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false)).build();
        RP_MIPMAP = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{MATRICES_SNIPPET}).withLocation(Identifier.fromNamespaceAndPath((String)"xaerolib", (String)"pipeline/mipmap")).withVertexShader(LibShaders.MIPMAP_GEN).withFragmentShader(LibShaders.MIPMAP_GEN).withVertexBinding(0, DefaultVertexFormat.POSITION_TEX).withPrimitiveTopology(PrimitiveTopology.QUADS).withBindGroupLayout(BindGroupLayout.builder().withUniform("Sampler0", UniformType.COMBINED_IMAGE_SAMPLER).build()).withColorTargetState(CT_REPLACE_TRANSPARENCY).withCull(false).build();
    }
}

