/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.region;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import xaero.common.settings.ModSettings;

public class MinimapTile {
    public static List<MinimapTile> recycled = new ArrayList<MinimapTile>();
    private boolean wasTransfered;
    private long[][] comparisonCodes = new long[16][16];
    private byte[][] comparisonCodesAdd = new byte[16][16];
    private byte[][] comparisonCodesAdd2 = new byte[16][16];
    private byte[][] verticalSlopes = new byte[16][16];
    private byte[][] diagonalSlopes = new byte[16][16];
    private byte[][][] red = new byte[5][16][16];
    private byte[][][] green = new byte[5][16][16];
    private byte[][][] blue = new byte[5][16][16];
    private boolean chunkGrid;
    private boolean slimeChunk;
    private int X;
    private int Z;
    private boolean success = true;
    private short[][] heights;
    public int caveLevel;
    private boolean hasSomething;
    private boolean hasTerrain;
    private int highlightVersion;
    private int[] highlights;

    public static MinimapTile getANewTile(ModSettings settings, int X, int Z, Long seed) {
        if (recycled.isEmpty()) {
            return new MinimapTile(settings, X, Z, seed);
        }
        MinimapTile t = recycled.remove(0);
        t.create(settings, X, Z, seed);
        return t;
    }

    public MinimapTile(ModSettings settings, int X, int Z, Long seed) {
        this.create(settings, X, Z, seed);
    }

    private void create(ModSettings settings, int X, int Z, Long seed) {
        this.X = X;
        this.Z = Z;
        this.chunkGrid = (X & 1) == (Z & 1);
        this.slimeChunk = MinimapTile.isSlimeChunk(settings, X, Z, seed);
        this.heights = new short[16][16];
        this.hasSomething = false;
        this.hasTerrain = false;
    }

    public void recycle() {
        this.success = true;
        this.highlights = null;
        recycled.add(this);
    }

    public static boolean isSlimeChunk(ModSettings settings, int xPosition, int zPosition, Long seed) {
        try {
            if (seed == null) {
                return false;
            }
            RandomSource rnd = WorldgenRandom.seedSlimeChunk((int)xPosition, (int)zPosition, (long)seed, (long)987234911L);
            return rnd.nextInt(10) == 0;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean isWasTransfered() {
        return this.wasTransfered;
    }

    public void setWasTransfered(boolean wasTransfered) {
        this.wasTransfered = wasTransfered;
    }

    public boolean isChunkGrid() {
        return this.chunkGrid;
    }

    public boolean isSlimeChunk() {
        return this.slimeChunk;
    }

    public int getRed(int l, int x, int z) {
        return this.red[l][x][z] & 0xFF;
    }

    public int getGreen(int l, int x, int z) {
        return this.green[l][x][z] & 0xFF;
    }

    public int getBlue(int l, int x, int z) {
        return this.blue[l][x][z] & 0xFF;
    }

    public void setRGB(int l, int x, int z, int r, int g, int b) {
        this.red[l][x][z] = (byte)r;
        this.green[l][x][z] = (byte)g;
        this.blue[l][x][z] = (byte)b;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getX() {
        return this.X;
    }

    public int getZ() {
        return this.Z;
    }

    public int getHeight(int x, int z) {
        return this.heights[x][z];
    }

    public void setHeight(int x, int z, int h) {
        this.heights[x][z] = (short)h;
    }

    public boolean pixelChanged(int x, int z, long code, byte add, byte add2, byte verticalSlope, byte diagonalSlope) {
        return this.comparisonCodesAdd[x][z] != add || this.comparisonCodesAdd2[x][z] != add2 || this.comparisonCodes[x][z] != code || this.verticalSlopes[x][z] != verticalSlope || this.diagonalSlopes[x][z] != diagonalSlope;
    }

    public void setCode(int x, int z, long code, byte add, byte add2, byte verticalSlope, byte diagonalSlope) {
        this.comparisonCodes[x][z] = code;
        this.comparisonCodesAdd[x][z] = add;
        this.comparisonCodesAdd2[x][z] = add2;
        this.verticalSlopes[x][z] = verticalSlope;
        this.diagonalSlopes[x][z] = diagonalSlope;
    }

    public void setHasSomething(boolean hasSomething) {
        this.hasSomething = hasSomething;
    }

    public boolean isHasSomething() {
        return this.hasSomething;
    }

    public void setHasTerrain(boolean hasTerrain) {
        this.hasTerrain = hasTerrain;
    }

    public boolean hasTerrain() {
        return this.hasTerrain;
    }

    public int getHighlightVersion() {
        return this.highlightVersion;
    }

    public void setHighlightVersion(int highlightVersion) {
        this.highlightVersion = highlightVersion;
    }

    public int[] getHighlights() {
        return this.highlights;
    }

    public void setHighlights(int[] highlights) {
        this.highlights = highlights;
    }
}

