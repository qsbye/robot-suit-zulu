/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.definition.form.sprite;

import net.minecraft.resources.Identifier;
import xaero.hud.minimap.radar.icon.definition.RadarIconDefinition;
import xaero.hud.minimap.radar.icon.definition.form.RadarIconForm;
import xaero.hud.minimap.radar.icon.definition.form.type.RadarIconFormType;

public class RadarIconSpriteForm
extends RadarIconForm {
    private final Identifier spriteLocation;

    public RadarIconSpriteForm(RadarIconFormType type, Identifier spriteLocation) {
        super(type);
        this.spriteLocation = spriteLocation;
    }

    public Identifier getSpriteLocation() {
        return this.spriteLocation;
    }

    public static RadarIconSpriteForm read(RadarIconFormType type, String[] args, RadarIconDefinition iconDefinition) {
        if (args.length != 2) {
            return null;
        }
        Identifier sprite = Identifier.fromNamespaceAndPath((String)"xaerominimap", (String)("entity/icon/sprite/" + args[1]));
        return new RadarIconSpriteForm(type, sprite);
    }
}

