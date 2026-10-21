/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.util;

public class MathUtils {
    public static int getDecimalCountAfterDot(double number, int maximum) {
        double multipliedStep;
        int result;
        for (result = 0; result <= maximum && Math.floor(multipliedStep = number * Math.pow(10.0, result)) != multipliedStep; ++result) {
        }
        return result;
    }

    public static double floor(double number, int afterDot) {
        double shifter = Math.pow(10.0, afterDot);
        return Math.floor(number * shifter) / shifter;
    }

    public static double round(double a, int afterDot) {
        double x = Math.pow(10.0, afterDot);
        return (double)Math.round(a * x) / x;
    }
}

