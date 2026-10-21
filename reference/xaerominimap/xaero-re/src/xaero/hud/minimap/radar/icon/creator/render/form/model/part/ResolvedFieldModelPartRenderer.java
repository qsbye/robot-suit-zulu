/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.render.form.model.part;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import xaero.hud.minimap.radar.icon.creator.render.form.model.RadarIconModelPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.form.model.part.RadarIconModelPartPrerenderer;
import xaero.hud.minimap.radar.icon.creator.render.form.model.resolver.RadarIconModelFieldResolver;

public class ResolvedFieldModelPartRenderer
implements RadarIconModelFieldResolver.Listener {
    private PoseStack matrixStack;
    private VertexConsumer vertexConsumer;
    private boolean justOne;
    private ModelPart mainPart;
    private RadarIconModelPartPrerenderer modelPartPrerenderer;
    private RadarIconModelPrerenderer.Parameters parameters;
    private boolean stop;

    public void prepare(PoseStack matrixStack, VertexConsumer vertexConsumer, boolean justOne, ModelPart mainPart, RadarIconModelPrerenderer.Parameters parameters, RadarIconModelPartPrerenderer modelPartPrerenderer) {
        this.matrixStack = matrixStack;
        this.vertexConsumer = vertexConsumer;
        this.justOne = justOne;
        this.mainPart = mainPart;
        this.parameters = parameters;
        this.modelPartPrerenderer = modelPartPrerenderer;
        this.stop = false;
    }

    @Override
    public boolean isFieldAllowed(Field f) {
        try {
            f.getType().asSubclass(ModelPart.class);
        }
        catch (ClassCastException cce) {
            try {
                f.getType().asSubclass(ModelPart[].class);
            }
            catch (ClassCastException cce1) {
                try {
                    f.getType().asSubclass(Collection.class);
                }
                catch (ClassCastException cce2) {
                    try {
                        f.getType().asSubclass(Map.class);
                    }
                    catch (ClassCastException cce3) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldStop() {
        return this.stop;
    }

    @Override
    public void onFieldResolved(Object[] resolved, String matchedFilterElement) {
        PoseStack matrixStack = this.matrixStack;
        VertexConsumer vertexConsumer = this.vertexConsumer;
        boolean justOne = this.justOne;
        RadarIconModelPartPrerenderer modelPartPrerenderer = this.modelPartPrerenderer;
        for (Object o : resolved) {
            if (!(o instanceof ModelPart)) continue;
            ModelPart part = (ModelPart)o;
            if (this.mainPart == null) {
                this.mainPart = part;
            }
            modelPartPrerenderer.renderPart(matrixStack, vertexConsumer, part, this.mainPart, this.parameters);
            if (!justOne) continue;
            this.stop = true;
            break;
        }
    }

    public ModelPart getMainPart() {
        return this.mainPart;
    }
}

