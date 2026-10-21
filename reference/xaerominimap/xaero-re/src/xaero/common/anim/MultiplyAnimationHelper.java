/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.anim;

public class MultiplyAnimationHelper {
    public static long currentTick;
    public static long lastTick;
    public static final double STEP_TIME = 16.666666666666668;

    public static void tick() {
        lastTick = currentTick;
        currentTick = System.currentTimeMillis();
    }

    public static double animate(double a, double factor) {
        double power = (double)(currentTick - lastTick) / 16.666666666666668;
        return a *= Math.pow(factor, power);
    }

    static {
        lastTick = currentTick = System.currentTimeMillis();
    }
}

