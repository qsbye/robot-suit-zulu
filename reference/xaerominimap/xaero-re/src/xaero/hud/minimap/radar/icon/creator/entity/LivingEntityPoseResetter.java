/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.entity;

import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class LivingEntityPoseResetter {
    public void resetValues(LivingEntityRenderState livingEntityRenderState) {
        livingEntityRenderState.walkAnimationSpeed = 0.0f;
        livingEntityRenderState.yRot = 0.0f;
        livingEntityRenderState.xRot = 0.0f;
        livingEntityRenderState.bodyRot = 0.0f;
        livingEntityRenderState.ageInTicks = 10.0f;
        if (livingEntityRenderState instanceof ArmedEntityRenderState) {
            ArmedEntityRenderState armedRenderState = (ArmedEntityRenderState)livingEntityRenderState;
            armedRenderState.swingAnimation = 0.0f;
        }
        if (livingEntityRenderState instanceof HumanoidRenderState) {
            HumanoidRenderState humanoidRenderState = (HumanoidRenderState)livingEntityRenderState;
            humanoidRenderState.swimAmount = 0.0f;
            humanoidRenderState.isCrouching = false;
            humanoidRenderState.isFallFlying = false;
            humanoidRenderState.isVisuallySwimming = false;
            humanoidRenderState.ticksUsingItem = 0.0f;
        }
    }
}

