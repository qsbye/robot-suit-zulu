/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.entry.widget;

import java.util.function.IntConsumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.widget.EditorButton;
import xaero.hud.category.ui.node.EditorNode;
import xaero.lib.client.gui.ScreenBase;

public class EditorButtonIteration
extends EditorButton {
    protected int currentIndex;
    protected int optionCount;
    protected IntConsumer updatedIndexConsumer;

    public EditorButtonIteration(EditorNode parent, IntConsumer updatedIndexConsumer, Supplier<Component> messageSupplier, boolean active, int currentIndex, int optionCount, int w, int h, GuiCategoryEditor.SettingRowList rowList) {
        super(parent, messageSupplier, active, w, h, b -> ((EditorButtonIteration)b).toggle(), rowList);
        this.currentIndex = currentIndex;
        this.optionCount = optionCount;
        this.updatedIndexConsumer = updatedIndexConsumer;
        this.updateMessage();
    }

    public final void toggle() {
        this.iterate(ScreenBase.hasShiftDown() ? -1 : 1);
    }

    private void iterate(int direction) {
        this.currentIndex += direction;
        this.putCurrentIndexInRange();
        this.updatedIndexConsumer.accept(this.currentIndex);
        this.updateMessage();
    }

    private void putCurrentIndexInRange() {
        if (this.currentIndex >= this.optionCount) {
            this.currentIndex %= this.optionCount;
            return;
        }
        if (this.currentIndex >= 0) {
            return;
        }
        this.currentIndex = this.optionCount + this.currentIndex % this.optionCount;
        if (this.currentIndex == this.optionCount) {
            this.currentIndex = 0;
        }
    }

    public boolean mouseScrolled(double d, double e, double f, double g) {
        return super.mouseScrolled(d, e, f, g);
    }
}

