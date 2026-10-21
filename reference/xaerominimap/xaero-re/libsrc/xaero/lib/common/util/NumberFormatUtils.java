/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.util;

import java.text.DecimalFormat;

public class NumberFormatUtils {
    public static DecimalFormat getPrecisionFormat(int precision) {
        StringBuilder formatStringBuilder = new StringBuilder();
        formatStringBuilder.append("0");
        if (precision > 0) {
            formatStringBuilder.append(".");
        }
        for (int i = 0; i < precision; ++i) {
            formatStringBuilder.append("0");
        }
        return new DecimalFormat(formatStringBuilder.toString());
    }
}

