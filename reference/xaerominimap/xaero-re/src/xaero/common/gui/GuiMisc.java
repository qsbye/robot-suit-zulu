/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.text.DecimalFormat;
import xaero.lib.common.util.NumberFormatUtils;

public class GuiMisc {
    private static int wpDistanceFormatPrecision = 1;
    private static DecimalFormat wpDistanceFormat = new DecimalFormat("0.0");

    public static DecimalFormat getFormat(int precision) {
        if (precision != wpDistanceFormatPrecision) {
            wpDistanceFormat = NumberFormatUtils.getPrecisionFormat((int)precision);
            wpDistanceFormatPrecision = precision;
        }
        return wpDistanceFormat;
    }
}

