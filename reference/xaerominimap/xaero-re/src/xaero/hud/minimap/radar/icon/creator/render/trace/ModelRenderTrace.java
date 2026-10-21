/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.render.trace;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.client.renderer.texture.UvMapping;
import xaero.hud.minimap.radar.icon.creator.render.trace.ModelPartRenderTrace;
import xaero.lib.client.graphics.ITextureBinding;

public class ModelRenderTrace {
    public final Model model;
    public final Map<String, ITextureBinding> textures;
    public final TextureTransform textureTransform;
    public final boolean useOverlay;
    public final boolean affectsCrumbling;
    public final boolean sortOnUpload;
    public final LayeringTransform layeringTransform;
    public final UvMapping uvMapping;
    public final Float decalTextureScale;
    public final RenderPipeline layerPipeline;
    public int color;
    public boolean allVisible;
    private HashMap<ModelPart, ModelPartRenderTrace> visibleParts;

    public ModelRenderTrace(Model model, Map<String, ITextureBinding> textures, TextureTransform textureTransform, boolean useOverlay, boolean affectsCrumbling, boolean sortOnUpload, LayeringTransform layeringTransform, UvMapping uvMapping, Float decalTextureScale, RenderPipeline layerPipeline, int color) {
        this.model = model;
        this.textures = textures;
        this.textureTransform = textureTransform;
        this.useOverlay = useOverlay;
        this.affectsCrumbling = affectsCrumbling;
        this.sortOnUpload = sortOnUpload;
        this.layeringTransform = layeringTransform;
        this.uvMapping = uvMapping;
        this.decalTextureScale = decalTextureScale;
        this.layerPipeline = layerPipeline;
        this.color = color;
    }

    public String toString() {
        return String.valueOf(this.model) + " " + String.valueOf(this.layerPipeline.getLocation());
    }

    public void addVisibleModelPart(ModelPart part, int color) {
        if (this.visibleParts == null) {
            this.visibleParts = new HashMap();
        }
        this.visibleParts.put(part, new ModelPartRenderTrace(part, color));
    }

    public ModelPartRenderTrace getModelPartRenderInfo(ModelPart part) {
        ModelPartRenderTrace mprdi;
        ModelPartRenderTrace modelPartRenderTrace = mprdi = this.visibleParts == null ? null : this.visibleParts.get(part);
        if (mprdi == null && this.allVisible) {
            mprdi = new ModelPartRenderTrace(part, this.color);
        }
        return mprdi;
    }

    public boolean isEmpty() {
        return !this.allVisible && (this.visibleParts == null || this.visibleParts.isEmpty());
    }

    public boolean sameVisibility(ModelRenderTrace other) {
        HashMap<ModelPart, ModelPartRenderTrace> otherVisibleParts;
        if (this.visibleParts == null != ((otherVisibleParts = other.visibleParts) == null)) {
            return false;
        }
        if (this.visibleParts == null) {
            return true;
        }
        if (this.visibleParts.size() != otherVisibleParts.size()) {
            return false;
        }
        Set<ModelPart> keySet = this.visibleParts.keySet();
        for (ModelPart key : keySet) {
            if (otherVisibleParts.containsKey(key)) continue;
            return false;
        }
        return true;
    }
}

