/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.controls.util;

import java.nio.FloatBuffer;
import org.lwjgl.sdl.SDLMouse;
import org.lwjgl.system.MemoryStack;

public class MouseUtils {
    public static boolean isMouseButtonDown(int button) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            FloatBuffer x = stack.mallocFloat(1);
            FloatBuffer y = stack.mallocFloat(1);
            int stateFlags = SDLMouse.SDL_GetMouseState((FloatBuffer)x, (FloatBuffer)y);
            boolean bl = (stateFlags & 1 << button - 1) == 1;
            return bl;
        }
    }
}

