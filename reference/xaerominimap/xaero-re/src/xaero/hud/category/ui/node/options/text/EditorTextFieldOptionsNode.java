/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node.options.text;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import xaero.common.misc.ListFactory;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntryExpandingOptions;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.entry.widget.EditorTextField;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.options.EditorExpandingOptionsNode;
import xaero.hud.category.ui.node.options.EditorOptionNode;
import xaero.hud.category.ui.node.options.EditorOptionsNode;
import xaero.hud.category.ui.node.options.text.TextFieldSuggestionsResolver;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;

public final class EditorTextFieldOptionsNode
extends EditorExpandingOptionsNode<String> {
    private String input;
    private String result;
    private int cursorPos;
    private int highlightPos;
    private final int maxLength;
    private EditorTextField.UpdatedValueConsumer updatedValueConsumer;
    private List<EditorOptionNode<String>> suggestions;
    private final TextFieldSuggestionsResolver suggestionsResolver;
    private final boolean allowCustomInput;
    private final boolean autoConfirm;
    private final Predicate<String> inputStringValidator;

    protected EditorTextFieldOptionsNode(@Nonnull Component displayName, @Nonnull String input, int maxLength, @Nonnull EditorOptionNode<String> currentValue, @Nonnull List<EditorOptionNode<String>> options, @Nonnull TextFieldSuggestionsResolver suggestionsResolver, boolean movable, @Nonnull EditorListRootEntryFactory listEntryFactory, boolean allowCustomInput, boolean autoConfirm, IEditorDataTooltipSupplier tooltipSupplier, EditorOptionsNode.IOptionsNodeIsActiveSupplier isActiveSupplier, Predicate<String> inputStringValidator) {
        super(displayName, currentValue, options, movable, listEntryFactory, tooltipSupplier, isActiveSupplier);
        this.maxLength = maxLength;
        this.resetInput(input);
        this.suggestionsResolver = suggestionsResolver;
        this.allowCustomInput = allowCustomInput;
        this.autoConfirm = autoConfirm;
        this.inputStringValidator = inputStringValidator;
    }

    public void resetInput(String input) {
        this.input = input;
        this.result = input;
        this.cursorPos = this.highlightPos = input.length();
    }

    @Override
    public void setCurrentValue(EditorOptionNode<String> currentValue) {
    }

    @Override
    public EditorOptionNode<String> getCurrentValue() {
        return EditorOptionNode.Builder.begin().setValue(this.input).build();
    }

    public String getInput() {
        return this.input;
    }

    public String getResult() {
        return this.result;
    }

    public int getCursorPos() {
        return this.cursorPos;
    }

    public int getHighlightPos() {
        return this.highlightPos;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    @Override
    public Component getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setExpanded(boolean expanded) {
        if (!expanded) {
            this.resetInput(this.result);
            this.suggestions = null;
        }
        super.setExpanded(expanded);
    }

    @Override
    public boolean onSelected(EditorOptionNode<String> option) {
        boolean result = super.onSelected(option);
        this.resetInput(option.getValue().toString());
        return result;
    }

    public EditorTextField.UpdatedValueConsumer getUpdatedValueConsumer() {
        if (this.updatedValueConsumer == null) {
            this.updatedValueConsumer = (s, c, h, rl) -> {
                this.cursorPos = c;
                this.highlightPos = h;
                String oldInput = this.input;
                if (oldInput != null && oldInput.equals(s)) {
                    return;
                }
                this.input = s;
                this.suggestions = this.suggestionsResolver.getSuggestions(s, this.options);
                if (this.autoConfirm) {
                    this.result = s;
                }
                if (!this.autoConfirm && this.allowCustomInput && !s.isEmpty() && this.inputStringValidator.test(s)) {
                    this.suggestions.add(0, (EditorOptionNode<String>)EditorOptionNode.Builder.begin().setValue(s).setDisplayName((Component)Component.translatable((String)"gui.xaero_category_add_to_list_custom", (Object[])new Object[]{s})).build());
                }
                if (!this.suggestions.isEmpty()) {
                    this.setExpanded(true);
                } else if (s.isEmpty()) {
                    this.setExpanded(false);
                }
                if (this.autoConfirm) {
                    rl.restoreScrollAfterUpdate();
                }
                rl.setLastExpandedData(this);
                rl.updateEntries();
            };
        }
        return this.updatedValueConsumer;
    }

    @Override
    public List<EditorNode> getSubNodes() {
        return this.suggestions;
    }

    public static final class Builder
    extends EditorExpandingOptionsNode.Builder<String, Builder> {
        private String input;
        private int maxLength;
        private final EditorOptionNode.Builder<String> currentInputOption = EditorOptionNode.Builder.begin();
        private final TextFieldSuggestionsResolver.Builder suggestionsResolverBuilder;
        private boolean allowCustomInput;
        private boolean autoConfirm;
        private Predicate<String> inputStringValidator;

        private Builder(ListFactory listFactory) {
            super(listFactory);
            this.suggestionsResolverBuilder = TextFieldSuggestionsResolver.Builder.begin(listFactory);
        }

        @Override
        public Builder setDefault() {
            super.setDefault();
            this.setInput("");
            this.setMaxLength(100);
            ((EditorOptionNode.Builder)this.currentInputOption.setDefault()).setDisplayName((Component)Component.literal((String)"null holder"));
            this.addOptionBuilder(this.currentInputOption);
            this.setAllowCustomInput(true);
            this.setAutoConfirm(true);
            this.setInputStringValidator(null);
            this.suggestionsResolverBuilder.setDefault();
            return this;
        }

        @Override
        protected EditorListRootEntry.CenteredEntryFactory getCenteredEntryFactory(EditorNode data, EditorNode parent, int index, GuiCategoryEditor.SettingRowList rowList) {
            return (x, y, width, height, root) -> {
                EditorTextFieldOptionsNode tfoData = (EditorTextFieldOptionsNode)data;
                EditorTextField widget = new EditorTextField(tfoData.getUpdatedValueConsumer(), tfoData.getInput(), tfoData.getCursorPos(), tfoData.getHighlightPos(), tfoData.getMaxLength(), Minecraft.getInstance().font, 214, 18, data.getDisplayName(), tfoData.inputStringValidator, rowList);
                if (rowList.readOnly) {
                    widget.active = false;
                }
                return new EditorListEntryExpandingOptions(x, y, width, height, index, rowList, root, (AbstractWidget)widget, null, data.getTooltipSupplier(parent));
            };
        }

        public Builder setInput(String input) {
            this.input = input;
            return this;
        }

        public Builder setInputStringValidator(Predicate<String> inputStringValidator) {
            this.inputStringValidator = inputStringValidator;
            return this;
        }

        public boolean needsInputStringValidator() {
            return this.inputStringValidator == null;
        }

        public Builder setAllowCustomInput(boolean allowCustomInput) {
            this.allowCustomInput = allowCustomInput;
            return this;
        }

        public Builder setAutoConfirm(boolean autoConfirm) {
            this.autoConfirm = autoConfirm;
            return this;
        }

        public Builder setMaxLength(int maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        @Override
        public Builder setCurrentValue(String currentValue) {
            return this;
        }

        @Override
        public EditorTextFieldOptionsNode build() {
            if (this.input == null || this.inputStringValidator == null) {
                throw new IllegalStateException("required fields not set!");
            }
            return (EditorTextFieldOptionsNode)super.build();
        }

        protected EditorTextFieldOptionsNode buildInternally(EditorOptionNode<String> currentValueData, List<EditorOptionNode<String>> options) {
            return new EditorTextFieldOptionsNode(this.displayName, this.input, this.maxLength, currentValueData, options, this.suggestionsResolverBuilder.build(), this.movable, this.listEntryFactory, this.allowCustomInput, this.autoConfirm, this.tooltipSupplier, this.isActiveSupplier, this.inputStringValidator);
        }

        public static Builder begin(ListFactory listFactory) {
            return new Builder(listFactory).setDefault();
        }
    }
}

