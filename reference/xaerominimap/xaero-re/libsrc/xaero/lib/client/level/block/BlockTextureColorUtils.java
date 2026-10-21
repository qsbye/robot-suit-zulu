/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.level.block;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import xaero.lib.XaeroLib;
import xaero.lib.client.level.block.BlockUtils;
import xaero.lib.common.exception.SilentException;
import xaero.lib.common.util.ImageIOUtils;

public abstract class BlockTextureColorUtils {
    protected final BlockUtils blockUtils;
    private final Map<String, Integer> textureColors;
    private final Map<BlockState, Integer> blockColors;
    private final Object2IntMap<BlockState> blockTintIndices;
    private boolean clearCachedColors;
    private BlockState lastBlockStateForTextureColor;
    private int lastBlockStateForTextureColorResult;

    protected BlockTextureColorUtils(BlockUtils blockUtils) {
        this.blockUtils = blockUtils;
        this.textureColors = new HashMap<String, Integer>();
        this.blockColors = new HashMap<BlockState, Integer>();
        this.blockTintIndices = new Object2IntOpenHashMap();
        this.lastBlockStateForTextureColorResult = -1;
    }

    public int getBlockTextureColor(BlockState state, boolean convert, ClientLevel world, Registry<Block> blockRegistry, BlockPos globalPos) {
        if (this.clearCachedColors) {
            this.textureColors.clear();
            this.blockColors.clear();
            this.blockTintIndices.clear();
            this.lastBlockStateForTextureColor = null;
            this.lastBlockStateForTextureColorResult = -1;
            this.clearCachedColors = false;
        }
        if (state == this.lastBlockStateForTextureColor) {
            return this.lastBlockStateForTextureColorResult;
        }
        Integer cached = this.blockColors.get(state);
        if (cached == null) {
            IntIntPair colorAndTint = this.getUncachedBlockColorAndTint(state, convert, world, blockRegistry, globalPos);
            cached = colorAndTint.firstInt();
            this.blockColors.put(state, cached);
            this.blockTintIndices.put((Object)state, colorAndTint.secondInt());
        }
        this.lastBlockStateForTextureColor = state;
        this.lastBlockStateForTextureColorResult = cached;
        return cached;
    }

    private IntIntPair getUncachedBlockColorAndTint(BlockState state, boolean convert, ClientLevel world, Registry<Block> blockRegistry, BlockPos globalPos) {
        int c;
        String name = null;
        int tintIndex = -1;
        Block b = state.getBlock();
        try {
            TextureAtlasSprite texture;
            BakedQuad biggestUpQuad = null;
            BlockStateModelSet bms = Minecraft.getInstance().getModelManager().getBlockStateModelSet();
            BlockStateModel model = bms.get(state);
            if (convert) {
                biggestUpQuad = this.blockUtils.getModelUtils().getBiggestQuad(model, (BlockAndTintGetter)world, globalPos, state, Direction.UP);
            }
            TextureAtlasSprite missingTexture = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(MissingTextureAtlasSprite.getLocation());
            if (biggestUpQuad == null || biggestUpQuad.materialInfo().sprite() == missingTexture) {
                texture = this.getParticleIcon(bms, model, world, globalPos, state);
                tintIndex = 0;
            } else {
                texture = biggestUpQuad.materialInfo().sprite();
                tintIndex = biggestUpQuad.materialInfo().tintIndex();
            }
            if (texture == null) {
                throw new SilentException("No texture for " + String.valueOf(state));
            }
            name = String.valueOf(texture.contents().name()) + ".png";
            Integer cached = this.textureColors.get(name);
            if (cached == null) {
                Identifier identifier = Identifier.parse((String)name);
                cached = this.getUncachedTextureColor(identifier, convert);
                this.textureColors.put(name, cached);
            }
            c = cached;
        }
        catch (FileNotFoundException e) {
            if (convert) {
                return this.getUncachedBlockColorAndTint(state, false, world, blockRegistry, globalPos);
            }
            XaeroLib.LOGGER.info("Block file not found: " + String.valueOf(blockRegistry.getKey((Object)b)));
            c = 0;
            if (state != null && state.getMapColor((BlockGetter)world, globalPos) != null) {
                c = state.getMapColor((BlockGetter)world, (BlockPos)globalPos).col;
            }
            if (name != null) {
                this.textureColors.put(name, c);
            }
        }
        catch (Exception e) {
            XaeroLib.LOGGER.info("Exception when loading " + String.valueOf(blockRegistry.getKey((Object)b)) + " texture, using material colour.");
            c = 0;
            if (state.getMapColor((BlockGetter)world, globalPos) != null) {
                c = state.getMapColor((BlockGetter)world, (BlockPos)globalPos).col;
            }
            if (name != null) {
                this.textureColors.put(name, c);
            }
            if (e instanceof SilentException) {
                XaeroLib.LOGGER.info(e.getMessage());
            }
            XaeroLib.LOGGER.error("suppressed exception", (Throwable)e);
        }
        return IntIntPair.of((int)c, (int)tintIndex);
    }

    private int getUncachedTextureColor(Identifier identifier, boolean exceptionOnBlack) throws SilentException, IOException {
        Identifier location = Identifier.fromNamespaceAndPath((String)identifier.getNamespace(), (String)("textures/" + identifier.getPath()));
        Resource resource = Minecraft.getInstance().getResourceManager().getResource(location).orElse(null);
        if (resource == null) {
            throw new SilentException("No texture " + String.valueOf(location));
        }
        InputStream input = resource.open();
        ImageInputStream imageInputStream = ImageIO.createImageInputStream(input);
        BufferedImage img = ImageIOUtils.getImageThroughZipError(imageInputStream, location.toString());
        imageInputStream.close();
        input.close();
        if (img == null) {
            throw new SilentException("No image loaded " + String.valueOf(location));
        }
        int red = 0;
        int green = 0;
        int blue = 0;
        int alpha = 0;
        int total = 0;
        int ts = Math.min(img.getWidth(), img.getHeight());
        if (ts > 0) {
            int diff = Math.max(1, Math.min(4, ts / 8));
            int parts = ts / diff;
            Raster raster = img.getData();
            int[] colorHolder = null;
            for (int i = 0; i < parts; ++i) {
                for (int j = 0; j < parts; ++j) {
                    int rgb;
                    if (img.getColorModel().getNumComponents() < 3) {
                        colorHolder = raster.getPixel(i * diff, j * diff, colorHolder);
                        int sample = colorHolder[0] & 0xFF;
                        int a = 255;
                        if (colorHolder.length > 1) {
                            a = colorHolder[1];
                        }
                        rgb = a << 24 | sample << 16 | sample << 8 | sample;
                    } else {
                        rgb = img.getRGB(i * diff, j * diff);
                    }
                    int a = rgb >> 24 & 0xFF;
                    if (rgb == 0 || a <= 10) continue;
                    red += rgb >> 16 & 0xFF;
                    green += rgb >> 8 & 0xFF;
                    blue += rgb & 0xFF;
                    alpha += a;
                    ++total;
                }
            }
        }
        if (total == 0) {
            total = 1;
        }
        alpha /= total;
        if (exceptionOnBlack && (red /= total) == 0 && (green /= total) == 0 && (blue /= total) == 0) {
            throw new SilentException("Black texture " + ts);
        }
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public void requestCachedColoursClear() {
        this.clearCachedColors = true;
    }

    public int getBlockTintIndex(BlockState state) {
        return this.blockTintIndices.getInt((Object)state);
    }

    public abstract TextureAtlasSprite getParticleIcon(BlockStateModelSet var1, BlockStateModel var2, ClientLevel var3, BlockPos var4, BlockState var5);
}

