/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.entry.widget;

import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import xaero.common.gui.IXaeroNarratableWidget;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.node.EditorNode;

public class EditorButton
extends Button.Plain
implements IXaeroNarratableWidget {
    protected Supplier<Component> messageSupplier;
    private EditorNode parent;
    private GuiCategoryEditor.SettingRowList rowList;

    public EditorButton(EditorNode parent, boolean active, int w, int h, EditorNode node, GuiCategoryEditor.SettingRowList rowList) {
        this(parent, () -> node.getDisplayName(), active, w, h, b -> node.getExpandAction(rowList).run(), rowList);
    }

    public EditorButton(EditorNode parent, Supplier<Component> messageSupplier, boolean active, int w, int h, Button.OnPress onPress, GuiCategoryEditor.SettingRowList rowList) {
        super(2, 2, w, h, (Component)Component.literal((String)""), onPress, DEFAULT_NARRATION);
        this.active = active;
        this.messageSupplier = messageSupplier;
        this.rowList = rowList;
        this.parent = parent;
        this.updateMessage();
    }

    protected void updateMessage() {
        this.setMessage(this.messageSupplier.get());
    }

    @Override
    public MutableComponent createNarrationMessage() {
        return Component.literal((String)"");
    }

    public static abstract class PressActionWithContext
    implements Button.OnPress {
        public void onPress(Button button) {
            this.onPress((EditorButton)button, ((EditorButton)button).parent, ((EditorButton)button).rowList);
        }

        public abstract void onPress(EditorButton var1, EditorNode var2, GuiCategoryEditor.SettingRowList var3);
    }
}

