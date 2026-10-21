/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.level.block;

import xaero.lib.client.level.block.BlockModelUtils;
import xaero.lib.client.level.block.BlockTextureColorUtils;
import xaero.lib.client.level.block.BlockTranslucencyUtils;

public abstract class BlockUtils {
    private final BlockModelUtils modelUtils = this.createModelUtils();
    private final BlockTextureColorUtils textureColorUtils = this.createTextureColorUtils();
    private final BlockTranslucencyUtils transparencyUtils = this.createTranslucencyUtils();

    protected BlockUtils() {
    }

    public BlockModelUtils getModelUtils() {
        return this.modelUtils;
    }

    public BlockTextureColorUtils getTextureColorUtils() {
        return this.textureColorUtils;
    }

    public BlockTranslucencyUtils getTranslucencyUtils() {
        return this.transparencyUtils;
    }

    abstract BlockModelUtils createModelUtils();

    abstract BlockTextureColorUtils createTextureColorUtils();

    abstract BlockTranslucencyUtils createTranslucencyUtils();
}

