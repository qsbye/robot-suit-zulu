/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics.shader;

import xaero.lib.client.graphics.shader.BuiltInCustomUniforms;

public class WorldMapShaderHelper {
    public static Float cachedBrightness = null;
    public static Integer cachedWithLight = null;

    public static void setBrightness(float brightness) {
        if (cachedBrightness != null && cachedBrightness.floatValue() == brightness) {
            return;
        }
        cachedBrightness = Float.valueOf(brightness);
        BuiltInCustomUniforms.BRIGHTNESS.setValue(cachedBrightness);
    }

    public static void setWithLight(boolean withLight) {
        int withLightInt;
        int n = withLightInt = withLight ? 1 : 0;
        if (cachedWithLight != null && cachedWithLight == withLightInt) {
            return;
        }
        cachedWithLight = withLightInt;
        BuiltInCustomUniforms.WITH_LIGHT.setValue(cachedWithLight);
    }
}

