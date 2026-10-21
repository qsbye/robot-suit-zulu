/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics.util;

import com.mojang.renderpearl.backend.opengl.BufferStorage;
import com.mojang.renderpearl.backend.opengl.GlStateManager;
import java.util.HashSet;
import java.util.Locale;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

public class OpenGLUtil {
    public static boolean usesImmutableStorage() {
        boolean forceMutable;
        String deviceName = GlStateManager._getString((int)7937).toLowerCase(Locale.ROOT);
        String vendor = GlStateManager._getString((int)7936).toLowerCase(Locale.ROOT);
        boolean isNvidia = deviceName.contains("nvidia");
        boolean couldBeIntelGen7 = OpenGLUtil.couldBeIntelGen7(deviceName, vendor);
        boolean bl = forceMutable = isNvidia || couldBeIntelGen7;
        if (forceMutable) {
            return false;
        }
        HashSet storageTestSet = new HashSet();
        BufferStorage.create((GLCapabilities)GL.getCapabilities(), storageTestSet, (boolean)false);
        return storageTestSet.contains("GL_ARB_buffer_storage");
    }

    private static boolean couldBeIntelGen7(String deviceName, String vendor) {
        if (!vendor.contains("intel")) {
            return false;
        }
        if (deviceName.contains("2500")) {
            return true;
        }
        if (deviceName.contains("4000")) {
            return true;
        }
        return deviceName.contains("hd graphics (byt)") || deviceName.endsWith("hd graphics");
    }
}

