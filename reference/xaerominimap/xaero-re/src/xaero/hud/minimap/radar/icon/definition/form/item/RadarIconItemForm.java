/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.definition.form.item;

import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;
import xaero.hud.minimap.radar.icon.definition.RadarIconDefinition;
import xaero.hud.minimap.radar.icon.definition.form.RadarIconBasicForms;
import xaero.hud.minimap.radar.icon.definition.form.RadarIconForm;
import xaero.hud.minimap.radar.icon.definition.form.type.RadarIconFormType;

public class RadarIconItemForm
extends RadarIconForm {
    private final Identifier itemKey;

    public RadarIconItemForm(RadarIconFormType type, @Nullable Identifier itemKey) {
        super(type);
        this.itemKey = itemKey;
    }

    public Identifier getItemKey() {
        return this.itemKey;
    }

    public static RadarIconItemForm read(RadarIconFormType type, String[] args, RadarIconDefinition iconDefinition) {
        if (args.length == 1) {
            return RadarIconBasicForms.SELF_ITEM;
        }
        if (args.length > 3) {
            return null;
        }
        Identifier itemKey = args.length == 2 ? Identifier.parse((String)args[1]) : Identifier.fromNamespaceAndPath((String)args[1], (String)args[2]);
        return new RadarIconItemForm(type, itemKey);
    }
}

