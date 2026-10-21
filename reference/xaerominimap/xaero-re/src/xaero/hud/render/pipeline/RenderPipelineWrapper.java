/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.render.pipeline;

import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PolygonMode;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class RenderPipelineWrapper
extends RenderPipeline {
    private final RenderPipeline original;
    private Map<ShaderType, Identifier> shaderOverrides;
    private Optional<ShaderDefines> shaderDefinesOverride;
    private Optional<List<BindGroupLayout>> bindGroupLayoutOverride;
    private Optional<DepthStencilState> depthStencilStateOverride;
    private Optional<PolygonMode> polygonModeOverride;
    private Optional<Boolean> cullOverride;
    private List<@Nullable ColorTargetState> colorTargetStatesOverrides;
    private List<@Nullable VertexFormat> vertexFormatPerBufferOverrides;
    private Optional<PrimitiveTopology> primitiveTopologyOverride;
    private Optional<Integer> pushConstantSizeOverride;
    private Optional<Integer> sortKeyOverride;

    public RenderPipelineWrapper(Identifier location, RenderPipeline original) {
        super(location, Collections.singletonMap(ShaderType.VERTEX, Identifier.fromNamespaceAndPath((String)"xaero", (String)"wrapper")), null, Collections.emptyList(), new ColorTargetState[0], null, null, false, new VertexFormat[16], null, 0, 0);
        this.original = original;
    }

    public @Nullable DepthStencilState getDepthStencilState() {
        if (this.depthStencilStateOverride != null) {
            return this.depthStencilStateOverride.get();
        }
        return this.original.getDepthStencilState();
    }

    public PolygonMode getPolygonMode() {
        if (this.polygonModeOverride != null) {
            return this.polygonModeOverride.get();
        }
        return this.original.getPolygonMode();
    }

    public boolean isCull() {
        if (this.cullOverride != null) {
            return this.cullOverride.get();
        }
        return this.original.isCull();
    }

    public List<@Nullable ColorTargetState> getColorTargetStates() {
        if (this.colorTargetStatesOverrides != null) {
            return this.colorTargetStatesOverrides;
        }
        return this.original.getColorTargetStates();
    }

    public List<@Nullable VertexFormat> getVertexFormatBindings() {
        if (this.vertexFormatPerBufferOverrides != null) {
            return this.vertexFormatPerBufferOverrides;
        }
        return this.original.getVertexFormatBindings();
    }

    public @Nullable VertexFormat getVertexFormatBinding(int bindingIndex) {
        return this.getVertexFormatBindings().get(bindingIndex);
    }

    public PrimitiveTopology getPrimitiveTopology() {
        if (this.primitiveTopologyOverride != null) {
            return this.primitiveTopologyOverride.get();
        }
        return this.original.getPrimitiveTopology();
    }

    public Map<ShaderType, Identifier> getShaders() {
        if (this.shaderOverrides != null) {
            return this.shaderOverrides;
        }
        return this.original.getShaders();
    }

    public ShaderDefines getShaderDefines() {
        if (this.shaderDefinesOverride != null) {
            return this.shaderDefinesOverride.get();
        }
        return this.original.getShaderDefines();
    }

    public List<BindGroupLayout> getBindGroupLayouts() {
        if (this.bindGroupLayoutOverride != null) {
            return this.bindGroupLayoutOverride.get();
        }
        return this.original.getBindGroupLayouts();
    }

    public int pushConstantSize() {
        if (this.pushConstantSizeOverride != null) {
            return this.pushConstantSizeOverride.get();
        }
        return this.original.pushConstantSize();
    }

    public int getSortKey() {
        if (this.sortKeyOverride != null) {
            return this.sortKeyOverride.get();
        }
        return this.original.getSortKey();
    }

    public boolean wantsDepthTexture() {
        return this.getDepthStencilState() != null;
    }

    public void setShaderOverrides(Map<ShaderType, Identifier> shaderOverrides) {
        this.shaderOverrides = shaderOverrides;
    }

    public void setShaderOverride(ShaderType type, Identifier vertexShaderOverride) {
        if (this.shaderOverrides == null) {
            this.shaderOverrides = new HashMap<ShaderType, Identifier>(this.original.getShaders());
        }
        this.shaderOverrides.put(type, vertexShaderOverride);
    }

    public void setShaderDefinesOverride(ShaderDefines shaderDefinesOverride) {
        this.shaderDefinesOverride = Optional.of(shaderDefinesOverride);
    }

    public void setBindGroupLayoutOverride(Optional<List<BindGroupLayout>> bindGroupLayoutOverride) {
        this.bindGroupLayoutOverride = bindGroupLayoutOverride;
    }

    public void setDepthStencilStateOverride(DepthStencilState depthStencilStateOverride) {
        this.depthStencilStateOverride = Optional.of(depthStencilStateOverride);
    }

    public void setPolygonModeOverride(PolygonMode polygonModeOverride) {
        this.polygonModeOverride = Optional.of(polygonModeOverride);
    }

    public void setCullOverride(Boolean cullOverride) {
        this.cullOverride = Optional.of(cullOverride);
    }

    public void setColorTargetStateOverride(int index, ColorTargetState colorTargetStateOverride) {
        if (this.colorTargetStatesOverrides == null) {
            this.colorTargetStatesOverrides = new ArrayList<ColorTargetState>(this.original.getColorTargetStates());
        }
        this.colorTargetStatesOverrides.set(index, colorTargetStateOverride);
    }

    public void resetColorTargetStateOverrides() {
        this.colorTargetStatesOverrides = null;
    }

    public void setVertexFormatOverride(int index, VertexFormat vertexFormat) {
        if (this.vertexFormatPerBufferOverrides == null) {
            this.vertexFormatPerBufferOverrides = new ArrayList<VertexFormat>(this.original.getVertexFormatBindings());
        }
        this.vertexFormatPerBufferOverrides.set(index, vertexFormat);
    }

    public void resetVertexFormatOverrides() {
        this.vertexFormatPerBufferOverrides = null;
    }

    public void setPrimitiveTopologyOverride(PrimitiveTopology primitiveTopologyOverride) {
        this.primitiveTopologyOverride = Optional.of(primitiveTopologyOverride);
    }

    public void setPushConstantSizeOverride(int pushConstantSizeOverride) {
        this.pushConstantSizeOverride = Optional.of(pushConstantSizeOverride);
    }

    public void setSortKeyOverride(int sortKeyOverride) {
        this.sortKeyOverride = Optional.of(sortKeyOverride);
    }
}

