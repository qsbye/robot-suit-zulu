/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class OptimizedMath {
    public static final Vector3f XP = new Vector3f(1.0f, 0.0f, 0.0f);
    public static final Vector3f YP = new Vector3f(0.0f, 1.0f, 0.0f);
    public static final Vector3f ZP = new Vector3f(0.0f, 0.0f, 1.0f);

    public static int myFloor(double d) {
        int asInt = (int)d;
        if ((double)asInt != d && d < 0.0) {
            --asInt;
        }
        return asInt;
    }

    public static void rotatePose(PoseStack poseStack, float degrees, Vector3fc vector) {
        PoseStack.Pose pose = poseStack.last();
        pose.pose().rotate(degrees * ((float)Math.PI / 180), vector);
        pose.normal().rotate(degrees * ((float)Math.PI / 180), vector);
    }

    public static void rotateMatrix(Matrix4f matrix, float degrees, Vector3fc vector) {
        matrix.rotate(degrees * ((float)Math.PI / 180), vector);
    }
}

