/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.pushbox;

import java.util.HashSet;
import java.util.Set;
import xaero.hud.pushbox.PushBox;

public class PushboxManager {
    private final Set<PushBox> pushBoxes = new HashSet<PushBox>();

    public void add(PushBox pushBox) {
        this.pushBoxes.add(pushBox);
    }

    public Iterable<PushBox> getAll() {
        return this.pushBoxes;
    }
}

