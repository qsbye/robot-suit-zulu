/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.render.form.model.resolver;

import java.lang.reflect.Field;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.radar.icon.creator.render.form.model.resolver.RadarIconModelFieldResolver;

public class ResolvedFieldModelRootPathListener
implements RadarIconModelFieldResolver.Listener {
    private Object resolvedObject;
    private boolean stop;
    private boolean failed;

    public void prepare() {
        this.resolvedObject = null;
        this.stop = false;
        this.failed = false;
    }

    @Override
    public boolean isFieldAllowed(Field f) {
        return true;
    }

    @Override
    public boolean shouldStop() {
        return this.stop;
    }

    @Override
    public void onFieldResolved(Object[] resolved, String matchedFilterElement) {
        this.stop = true;
        if (resolved.length != 1) {
            MinimapLogs.LOGGER.warn("Only exactly 1 object can be referenced with a model root path step but {} were referenced with {}", (Object)resolved.length, (Object)matchedFilterElement);
            this.failed = true;
            return;
        }
        this.resolvedObject = resolved[0];
    }

    public Object getCurrentNode() {
        return this.resolvedObject;
    }

    public boolean failed() {
        return this.failed;
    }
}

