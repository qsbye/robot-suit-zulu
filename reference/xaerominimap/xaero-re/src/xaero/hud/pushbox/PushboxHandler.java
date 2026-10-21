/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.pushbox;

import xaero.hud.module.ModuleSession;
import xaero.hud.pushbox.PushBox;
import xaero.hud.pushbox.PushboxManager;

public class PushboxHandler {
    public void applyPushboxes(PushboxManager manager, State state, int screenWidth, int screenHeight, double screenScale) {
        for (PushBox pb : manager.getAll()) {
            boolean bottomBlocked;
            boolean rightBlocked;
            if (!pb.isActive()) continue;
            int pushBoxX = (int)((float)screenWidth * pb.getAnchorX()) + pb.getX(screenWidth, screenHeight);
            int pushBoxY = (int)((float)screenHeight * pb.getAnchorY()) + pb.getY(screenWidth, screenHeight);
            int pushBoxW = pb.getW(screenWidth, screenHeight);
            int pushBoxH = pb.getH(screenWidth, screenHeight);
            int minPushX = 0;
            int minPushY = 0;
            int overLeftSide = state.x + state.w - pushBoxX;
            int overRightSide = state.x - (pushBoxX + pushBoxW);
            int overTopSide = state.y + state.h - pushBoxY;
            int overBottomSide = state.y - (pushBoxY + pushBoxH);
            if (overLeftSide <= 0 || overRightSide >= 0 || overTopSide <= 0 || overBottomSide >= 0) continue;
            boolean leftBlocked = state.x - overLeftSide < 0;
            boolean bl = rightBlocked = state.x - overRightSide + state.w > screenWidth;
            minPushX = leftBlocked == rightBlocked ? (-overRightSide < overLeftSide ? overRightSide : overLeftSide) : (leftBlocked ? overRightSide : overLeftSide);
            boolean topBlocked = state.y - overTopSide < 0;
            boolean bl2 = bottomBlocked = state.y - overBottomSide + state.h > screenHeight;
            if (topBlocked == bottomBlocked) {
                minPushY = -overBottomSide < overTopSide ? overBottomSide : overTopSide;
            } else {
                int n = minPushY = topBlocked ? overBottomSide : overTopSide;
            }
            if ((!leftBlocked || !rightBlocked || topBlocked && bottomBlocked) && Math.abs(minPushX) < Math.abs(minPushY) - pb.getVerticalBias()) {
                pb.push(state, -minPushX, 0);
                continue;
            }
            pb.push(state, 0, -minPushY);
        }
        this.applyScreenEdges(state, screenWidth, screenHeight, screenScale);
    }

    public void updateAll(PushboxManager manager) {
        for (PushBox pb : manager.getAll()) {
            pb.update();
        }
    }

    public void postUpdateAll(PushboxManager manager) {
        for (PushBox pb : manager.getAll()) {
            pb.postUpdate();
        }
    }

    public void applyScreenEdges(State state, int screenWidth, int screenHeight, double screenScale) {
        if (state.x + state.w > screenWidth) {
            state.x = screenWidth - state.w;
        }
        if (state.y + state.h > screenHeight) {
            state.y = screenHeight - state.h;
        }
        if (state.x < 0) {
            state.x = 0;
        }
        if (state.y < 0) {
            state.y = 0;
        }
    }

    public static final class State {
        public int x;
        public int y;
        public int w;
        public int h;

        public State resetForModule(ModuleSession<?> moduleSession, int screenWidth, int screenHeight, double screenScale) {
            this.x = moduleSession.getEffectiveX(screenWidth, screenScale);
            this.y = moduleSession.getEffectiveY(screenHeight, screenScale);
            this.w = moduleSession.getWidth(screenScale);
            this.h = moduleSession.getHeight(screenScale);
            return this;
        }
    }
}

