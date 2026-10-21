/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.definition.form.type;

import java.util.Map;
import javax.annotation.Nullable;
import xaero.common.icon.XaeroIcon;
import xaero.hud.minimap.radar.icon.creator.render.form.IRadarIconFormPrerenderer;
import xaero.hud.minimap.radar.icon.definition.RadarIconDefinition;
import xaero.hud.minimap.radar.icon.definition.form.RadarIconForm;
import xaero.hud.minimap.radar.icon.definition.form.type.IRadarIconFormReader;

public class RadarIconFormType {
    private final String id;
    private final IRadarIconFormReader reader;
    private final IRadarIconFormPrerenderer prerenderer;
    private final XaeroIcon failureResult;

    public RadarIconFormType(String id, IRadarIconFormReader reader, @Nullable IRadarIconFormPrerenderer prerenderer, XaeroIcon failureResult) {
        this.id = id;
        this.reader = reader;
        this.prerenderer = prerenderer;
        this.failureResult = failureResult;
    }

    public String getId() {
        return this.id;
    }

    public RadarIconForm readForm(RadarIconDefinition iconDefinition, String[] args) {
        return this.reader.read(this, args, iconDefinition);
    }

    public RadarIconFormType addTo(Map<String, RadarIconFormType> map) {
        map.put(this.id, this);
        return this;
    }

    @Nullable
    public IRadarIconFormPrerenderer getPrerenderer() {
        return this.prerenderer;
    }

    public XaeroIcon getFailureResult() {
        return this.failureResult;
    }
}

