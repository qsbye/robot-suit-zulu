/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.entry.widget;

import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import xaero.common.gui.IXaeroNarratableWidget;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.lib.client.gui.util.GuiUtils;

public class EditorTextField
extends EditBox
implements IXaeroNarratableWidget {
    private final UpdatedValueConsumer updatedValueConsumer;
    private int highlightPos;
    private final GuiCategoryEditor.SettingRowList rowList;
    private boolean pauseCallback;
    private final Predicate<String> validator;

    public EditorTextField(UpdatedValueConsumer updatedValueConsumer, String startValue, int startCursorPos, int startHighlighPos, int maxLength, Font font, int w, int h, Component message, Predicate<String> validator, GuiCategoryEditor.SettingRowList rowList) {
        super(font, 3, 3, w, h, message);
        this.setMaxLength(maxLength);
        this.setValue(startValue);
        this.setCursorPosition(startCursorPos);
        this.setHighlightPos(startHighlighPos);
        this.updatedValueConsumer = updatedValueConsumer;
        this.rowList = rowList;
        this.validator = validator;
        this.updateColorForValue(startValue);
        this.setResponder(s -> {
            if (this.pauseCallback) {
                return;
            }
            this.updateColorForValue((String)s);
            this.updatedValueConsumer.accept((String)s, this.getCursorPosition(), this.highlightPos, this.rowList);
        });
    }

    private void updateColorForValue(String value) {
        this.setTextColor(this.validator.test(value) ? -2039584 : -43691);
    }

    public void setCursorPosition(int i) {
        super.setCursorPosition(i);
    }

    public void setHighlightPos(int i) {
        super.setHighlightPos(i);
        this.highlightPos = i;
    }

    @Override
    public MutableComponent createNarrationMessage() {
        return super.createNarrationMessage();
    }

    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
        super.extractWidgetRenderState(guiGraphics, i, j, f);
        int o = this.getX() + 4;
        int p = this.getY() + (this.height - 8) / 2;
        if (this.getValue().isEmpty() && !this.isFocused()) {
            guiGraphics.text(Minecraft.getInstance().font, this.getMessage(), o, p, -11184811, true);
            GuiUtils.setFieldText((EditBox)this, (String)"");
            this.pauseCallback = false;
        }
    }

    @FunctionalInterface
    public static interface UpdatedValueConsumer {
        public void accept(String var1, int var2, int var3, GuiCategoryEditor.SettingRowList var4);
    }
}

