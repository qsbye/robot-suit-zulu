/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.region;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import org.joml.Math;
import xaero.common.minimap.region.MinimapTile;
import xaero.hud.minimap.Minimap;
import xaero.lib.client.graphics.GpuTextureAndView;
import xaero.lib.client.graphics.util.TextureUtils;

public class MinimapChunk {
    public static final int SIZE_TILES = 4;
    public static final int INT_BUFFER_SIZE = 4096;
    public static final int LIGHT_LEVELS = 5;
    private boolean blockTextureUpload;
    private int X;
    private int Z;
    private boolean hasSomething;
    private MinimapTile[][] tiles;
    private GpuTextureAndView[] glTexture;
    private boolean[] refreshRequired;
    private boolean refreshed;
    private ByteBuffer[] bufferBase;
    private IntBuffer[] buffer;
    private boolean changed;
    private int levelsBuffered = 0;

    public MinimapChunk(int X, int Z) {
        this.X = X;
        this.Z = Z;
        this.tiles = new MinimapTile[4][4];
        this.glTexture = new GpuTextureAndView[5];
        this.refreshRequired = new boolean[5];
        this.buffer = new IntBuffer[5];
        this.bufferBase = new ByteBuffer[5];
    }

    public void reset(int X, int Z) {
        int i;
        this.X = X;
        this.Z = Z;
        this.hasSomething = false;
        for (i = 0; i < this.glTexture.length; ++i) {
            this.glTexture[i] = null;
            this.refreshRequired[i] = false;
            if (this.buffer[i] == null) continue;
            this.buffer[i].clear();
        }
        this.refreshed = false;
        this.changed = false;
        this.levelsBuffered = 0;
        for (i = 0; i < this.tiles.length; ++i) {
            for (int j = 0; j < this.tiles.length; ++j) {
                this.tiles[i][j] = null;
            }
        }
        this.blockTextureUpload = false;
    }

    public void recycleTiles() {
        for (int i = 0; i < this.tiles.length; ++i) {
            for (int j = 0; j < this.tiles.length; ++j) {
                MinimapTile tile = this.tiles[i][j];
                if (tile == null) continue;
                if (!tile.isWasTransfered()) {
                    tile.recycle();
                    continue;
                }
                tile.setWasTransfered(false);
            }
        }
    }

    public int getLevelToRefresh(int currentLevel) {
        if (this.refreshed || this.levelsBuffered == 0 || currentLevel == -1) {
            return -1;
        }
        int prev = currentLevel - 1;
        if (prev < 0) {
            prev = this.levelsBuffered - 1;
        }
        int i = currentLevel;
        while (true) {
            if (this.refreshRequired[i]) {
                return i;
            }
            if (i == prev) break;
            i = (i + 1) % this.levelsBuffered;
        }
        this.refreshed = true;
        return -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GpuTextureAndView bindTexture(int level) {
        MinimapChunk minimapChunk = this;
        synchronized (minimapChunk) {
            int levelToRefresh;
            if (!this.hasSomething) {
                return null;
            }
            if (!this.blockTextureUpload && (levelToRefresh = this.getLevelToRefresh(Math.min((int)level, (int)(this.levelsBuffered - 1)))) != -1) {
                boolean result = false;
                if (this.glTexture[levelToRefresh] == null) {
                    GpuTexture texture = RenderSystem.getDevice().createTexture((String)null, 5, GpuFormat.RGBA8_UNORM, 64, 64, 1, 1);
                    GpuTextureView view = RenderSystem.getDevice().createTextureView(texture);
                    this.glTexture[levelToRefresh] = new GpuTextureAndView(texture, view);
                    result = true;
                }
                RenderSystem.getDevice().createCommandEncoder().writeToTexture(this.glTexture[levelToRefresh].texture, this.bufferBase[levelToRefresh], 0, 0, 0, 0, 64, 64);
                this.refreshRequired[levelToRefresh] = false;
            }
            GpuTextureAndView levelTexture = this.glTexture[level];
            return levelTexture;
        }
    }

    public void updateBuffers(int levelsToLoad) {
        this.refreshed = true;
        for (int l = 0; l < levelsToLoad; ++l) {
            this.refreshRequired[l] = false;
            if (this.buffer[l] != null) continue;
            this.bufferBase[l] = TextureUtils.allocateByteBuffer((int)16384, (ByteOrder)ByteOrder.LITTLE_ENDIAN);
            this.buffer[l] = this.bufferBase[l].asIntBuffer();
        }
        for (int o = 0; o < this.tiles.length; ++o) {
            int offX = o * 16;
            for (int p = 0; p < this.tiles.length; ++p) {
                MinimapTile tile = this.tiles[o][p];
                int offZ = p * 16;
                for (int z = 0; z < 16; ++z) {
                    for (int x = 0; x < 16; ++x) {
                        for (int i = 0; i < levelsToLoad; ++i) {
                            if (tile == null || !tile.isHasSomething() && !tile.hasTerrain()) {
                                this.putColour(offX + x, offZ + z, 0, 0, 0, 0, this.buffer[i], 64);
                                continue;
                            }
                            this.putColour(offX + x, offZ + z, tile.getRed(i, x, z), tile.getGreen(i, x, z), tile.getBlue(i, x, z), 255, this.buffer[i], 64);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < levelsToLoad; ++i) {
            this.buffer[i].clear();
            this.buffer[i].limit(4096);
            this.refreshRequired[i] = true;
        }
        this.refreshed = false;
    }

    public void putColour(int x, int y, int red, int green, int blue, int alpha, IntBuffer buffer, int size) {
        int pos = y * size + x;
        buffer.put(pos, alpha << 24 | blue << 16 | green << 8 | red);
    }

    public void copyBuffer(int level, IntBuffer toCopy) {
        if (this.buffer[level] == null) {
            this.bufferBase[level] = TextureUtils.allocateByteBuffer((int)16384, (ByteOrder)ByteOrder.LITTLE_ENDIAN);
            this.buffer[level] = this.bufferBase[level].asIntBuffer();
        } else {
            this.buffer[level].clear();
        }
        this.buffer[level].put(toCopy);
        this.buffer[level].flip();
    }

    public int getLevelsBuffered() {
        return this.levelsBuffered;
    }

    public boolean isHasSomething() {
        return this.hasSomething;
    }

    public void setHasSomething(boolean hasSomething) {
        this.hasSomething = hasSomething;
    }

    public int getX() {
        return this.X;
    }

    public int getZ() {
        return this.Z;
    }

    public GpuTextureAndView getGlTexture(int l) {
        return this.glTexture[l];
    }

    public void setGlTexture(int l, GpuTextureAndView t) {
        this.glTexture[l] = t;
    }

    public MinimapTile getTile(int x, int z) {
        return this.tiles[x][z];
    }

    public void setTile(int x, int z, MinimapTile t) {
        this.tiles[x][z] = t;
    }

    public boolean isChanged() {
        return this.changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public void setLevelsBuffered(int levelsBuffered) {
        this.levelsBuffered = levelsBuffered;
    }

    public boolean isBlockTextureUpload() {
        return this.blockTextureUpload;
    }

    public void setBlockTextureUpload(boolean blockTextureUpload) {
        this.blockTextureUpload = blockTextureUpload;
    }

    public boolean isRefreshRequired(int l) {
        return this.refreshRequired[l];
    }

    public void setRefreshRequired(int l, boolean r) {
        this.refreshRequired[l] = r;
    }

    public IntBuffer getBuffer(int l) {
        return this.buffer[l];
    }

    public void cleanup(Minimap minimap) {
        for (int l = 0; l < this.glTexture.length; ++l) {
            if (this.glTexture[l] == null) continue;
            this.glTexture[l].close();
        }
    }
}

