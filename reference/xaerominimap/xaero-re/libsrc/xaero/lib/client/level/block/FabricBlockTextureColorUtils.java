/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.level.block;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import xaero.lib.client.level.block.BlockTextureColorUtils;
import xaero.lib.client.level.block.BlockUtils;

public class FabricBlockTextureColorUtils
extends BlockTextureColorUtils {
    protected FabricBlockTextureColorUtils(BlockUtils blockUtils) {
        super(blockUtils);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(BlockStateModelSet bms, BlockStateModel model, ClientLevel level, BlockPos pos, BlockState state) {
        Material.Baked material = model.particleMaterial((BlockAndTintGetter)level, pos, state);
        return material.sprite();
    }
}

