/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.module;

public class ModuleTransform {
    public int x;
    public int y;
    public boolean centered;
    public boolean fromRight;
    public boolean fromBottom;
    public boolean flippedHor;
    public boolean flippedVer;
    public boolean fromOldSystem;

    public ModuleTransform copy() {
        ModuleTransform copy = new ModuleTransform();
        copy.x = this.x;
        copy.y = this.y;
        copy.centered = this.centered;
        copy.fromRight = this.fromRight;
        copy.fromBottom = this.fromBottom;
        copy.flippedHor = this.flippedHor;
        copy.flippedVer = this.flippedVer;
        copy.fromOldSystem = this.fromOldSystem;
        return copy;
    }
}

