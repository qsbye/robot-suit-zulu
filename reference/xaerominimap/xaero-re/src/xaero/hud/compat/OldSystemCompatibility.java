/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.compat;

import xaero.hud.module.ModuleSession;
import xaero.hud.module.ModuleTransform;
import xaero.hud.render.module.ModuleRenderContext;

public class OldSystemCompatibility {
    public void convertTransform(ModuleTransform transform, ModuleSession<?> session, ModuleRenderContext c) {
        transform.fromOldSystem = false;
        if (transform.fromRight) {
            transform.x -= session.getWidth(c.screenScale);
        }
        if (transform.fromBottom) {
            transform.y -= session.getHeight(c.screenScale);
        }
    }

    public ModuleTransform loadOldTransform(String[] args) {
        ModuleTransform loadedTransform = new ModuleTransform();
        loadedTransform.fromOldSystem = true;
        loadedTransform.fromRight = args[6].equals("true");
        loadedTransform.fromBottom = false;
        if (args.length > 7) {
            loadedTransform.fromBottom = args[7].equals("true");
        }
        loadedTransform.x = Integer.parseInt(args[2]);
        loadedTransform.y = Integer.parseInt(args[3]);
        loadedTransform.centered = args[4].equals("true");
        loadedTransform.flippedHor = args[5].equals("true");
        return loadedTransform;
    }
}

