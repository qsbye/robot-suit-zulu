/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.io.util;

import java.nio.file.Path;

public class ConfigIOUtils {
    public static Path getLargeValueFilePath(Path originalConfigFile, String subFolder, String extension) {
        return originalConfigFile.resolveSibling(subFolder).resolve(String.valueOf(originalConfigFile.getFileName()) + extension);
    }
}

