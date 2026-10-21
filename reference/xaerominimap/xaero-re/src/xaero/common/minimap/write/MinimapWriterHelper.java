/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.write;

public class MinimapWriterHelper {
    void getGlowingColour(int r, int g, int b, int[] result) {
        int total = r + g + b;
        float minBrightness = 407.0f;
        float brightener = Math.max(1.0f, minBrightness / (float)total);
        result[0] = (int)((float)r * brightener);
        result[1] = (int)((float)g * brightener);
        result[2] = (int)((float)b * brightener);
    }
}

