/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.definition.form;

import javax.annotation.Nullable;
import xaero.common.icon.XaeroIcon;
import xaero.hud.minimap.radar.icon.creator.render.form.IRadarIconFormPrerenderer;
import xaero.hud.minimap.radar.icon.definition.form.type.RadarIconFormType;

public class RadarIconForm {
    private final RadarIconFormType type;

    public RadarIconForm(RadarIconFormType type) {
        this.type = type;
    }

    public RadarIconFormType getType() {
        return this.type;
    }

    @Nullable
    public IRadarIconFormPrerenderer getPrerenderer() {
        return this.type.getPrerenderer();
    }

    public XaeroIcon getFailureResult() {
        return this.type.getFailureResult();
    }
}

