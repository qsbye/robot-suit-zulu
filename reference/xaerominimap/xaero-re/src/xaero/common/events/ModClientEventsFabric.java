/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.events;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;
import xaero.common.IXaeroMinimap;
import xaero.common.events.ModClientEvents;

public class ModClientEventsFabric
extends ModClientEvents {
    public ModClientEventsFabric(IXaeroMinimap modMain) {
        super(modMain);
    }

    public void register() {
        HudElementRegistry.attachElementAfter((Identifier)VanillaHudElements.MISC_OVERLAYS, (Identifier)Identifier.fromNamespaceAndPath((String)"xaerohud", (String)"hud"), this::handleRenderModOverlay);
    }
}

