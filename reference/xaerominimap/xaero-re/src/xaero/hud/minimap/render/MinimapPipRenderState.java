/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.render;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.Nullable;
import xaero.lib.client.graphics.XaeroBufferProvider;

public class MinimapPipRenderState
implements PictureInPictureRenderState {
    public static final int HORIZONTAL_PADDING = 20;
    public static final int VERTICAL_PADDING = 5;
    private boolean shouldRender;
    private int x;
    private int y;
    private int width;
    private int height;
    private double scale;
    private float minimapScale;
    private int size;
    private int boxSize;
    private float partial;
    private ScreenRectangle rectangle;
    private XaeroBufferProvider cvc;
    public boolean prepared;

    public MinimapPipRenderState update(int x, int y, int width, int height, double scale, float minimapScale, int size, int boxSize, float partial, XaeroBufferProvider cvc) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.minimapScale = minimapScale;
        this.size = size;
        this.boxSize = boxSize;
        this.partial = partial;
        this.cvc = cvc;
        this.rectangle = new ScreenRectangle(this.x0(), this.y0(), this.x1() - this.x0(), this.y1() - this.y0());
        this.prepared = false;
        return this;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public double getScale() {
        return this.scale;
    }

    public float getMinimapScale() {
        return this.minimapScale;
    }

    public int getSize() {
        return this.size;
    }

    public int getBoxSize() {
        return this.boxSize;
    }

    public float getPartial() {
        return this.partial;
    }

    public XaeroBufferProvider getCvc() {
        return this.cvc;
    }

    public int getScaledHorizontalPadding() {
        return (int)(20.0f * this.minimapScale);
    }

    public int getScaledVerticalPadding() {
        return (int)(5.0f * this.minimapScale);
    }

    public int x0() {
        return this.x - this.getScaledHorizontalPadding();
    }

    public int y0() {
        return this.y - this.getScaledVerticalPadding();
    }

    public int x1() {
        return this.x + this.boxSize + this.getScaledHorizontalPadding();
    }

    public int y1() {
        return this.y + this.boxSize + this.getScaledVerticalPadding();
    }

    public float scale() {
        return 1.0f;
    }

    @Nullable
    public ScreenRectangle scissorArea() {
        return null;
    }

    @Nullable
    public ScreenRectangle bounds() {
        return this.rectangle;
    }

    public static class Enlarged
    extends MinimapPipRenderState {
    }
}

