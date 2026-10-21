/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.definition.form;

import xaero.hud.minimap.radar.icon.definition.form.RadarIconForm;
import xaero.hud.minimap.radar.icon.definition.form.item.RadarIconItemForm;
import xaero.hud.minimap.radar.icon.definition.form.model.RadarIconModelForm;
import xaero.hud.minimap.radar.icon.definition.form.type.RadarIconFormTypes;

public class RadarIconBasicForms {
    public static final RadarIconModelForm DEFAULT_MODEL = new RadarIconModelForm(RadarIconFormTypes.MODEL, null);
    public static final RadarIconItemForm SELF_ITEM = new RadarIconItemForm(RadarIconFormTypes.ITEM, null);
    public static final RadarIconForm DOT = new RadarIconForm(RadarIconFormTypes.DOT);
}

