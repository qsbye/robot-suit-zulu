/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.icon;

import java.util.List;
import xaero.common.icon.XaeroIconAtlas;

public class XaeroIconAtlasManager {
    private final int iconWidth;
    private final int atlasTextureSize;
    private final List<XaeroIconAtlas> atlases;
    private int currentAtlasIndex;

    public XaeroIconAtlasManager(int iconWidth, int atlasTextureSize, List<XaeroIconAtlas> atlases) {
        this.iconWidth = iconWidth;
        this.atlasTextureSize = atlasTextureSize;
        this.atlases = atlases;
        this.currentAtlasIndex = -1;
    }

    public void clearAtlases() {
        for (XaeroIconAtlas entityIconAtlas : this.atlases) {
            entityIconAtlas.close();
        }
        this.currentAtlasIndex = -1;
        this.atlases.clear();
    }

    public XaeroIconAtlas getCurrentAtlas() {
        if (this.currentAtlasIndex < 0 || this.atlases.get(this.currentAtlasIndex).isFull()) {
            this.atlases.add(XaeroIconAtlas.Builder.begin().setWidth(this.atlasTextureSize).setIconWidth(this.iconWidth).build());
            this.currentAtlasIndex = this.atlases.size() - 1;
        }
        return this.atlases.get(this.currentAtlasIndex);
    }

    public int getAtlasTextureSize() {
        return this.atlasTextureSize;
    }
}

