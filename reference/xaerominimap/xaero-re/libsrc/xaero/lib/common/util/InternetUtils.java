/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InternetUtils {
    public static void download(BufferedOutputStream output, InputStream input) throws IOException {
        int read;
        byte[] buffer = new byte[256];
        while ((read = input.read(buffer, 0, buffer.length)) >= 0) {
            output.write(buffer, 0, read);
        }
        output.flush();
        input.close();
        output.close();
    }
}

