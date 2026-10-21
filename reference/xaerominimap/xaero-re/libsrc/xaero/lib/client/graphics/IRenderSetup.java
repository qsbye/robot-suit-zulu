/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics;

import java.util.Map;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.TextureTransform;

public interface IRenderSetup {
    public Map<String, ?> xaero_lib_getTextures();

    public TextureTransform xaero_lib_getTextureTransform();

    public boolean xaero_lib_getUseOverlay();

    public LayeringTransform xaero_lib_getLayeringTransform();
}

