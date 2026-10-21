/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.core;

import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;

public interface IGuiRenderer {
    public Projection xaero_mm_getGuiProjection();

    public ProjectionMatrixBuffer xaero_mm_getGuiProjectionMatrixBuffer();
}

