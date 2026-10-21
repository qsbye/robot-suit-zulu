/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.render.module;

public class ModuleRenderContext {
    public int x;
    public int y;
    public int w;
    public int h;
    public boolean flippedVertically;
    public boolean flippedHorizontally;
    public final int screenWidth;
    public final int screenHeight;
    public final double screenScale;

    public ModuleRenderContext(int screenWidth, int screenHeight, double screenScale) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenScale = screenScale;
    }
}

