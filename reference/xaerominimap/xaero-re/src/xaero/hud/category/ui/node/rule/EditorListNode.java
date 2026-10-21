/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node.rule;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import xaero.common.misc.ListFactory;
import xaero.hud.category.rule.ObjectCategoryListRuleType;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.ConnectionLineType;
import xaero.hud.category.ui.entry.EditorListEntryWidget;
import xaero.hud.category.ui.entry.EditorListEntryWrapper;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.entry.widget.EditorButton;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.EditorSimpleDeletableWrapperNode;
import xaero.hud.category.ui.node.EditorSimpleWrapperNode;
import xaero.hud.category.ui.node.options.text.EditorTextFieldOptionsNode;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;
import xaero.lib.client.gui.widget.Tooltip;
import xaero.lib.common.gui.widget.TooltipInfo;

abstract class EditorListNode
extends EditorNode {
    protected final List<EditorSimpleDeletableWrapperNode<String>> list;
    private final EditorTextFieldOptionsNode topAdder;
    private final EditorTextFieldOptionsNode bottomAdder;
    private final ListFactory listFactory;
    private final EditorSimpleDeletableWrapperNode.DeletionCallback deletionCallback;
    private final IEditorDataTooltipSupplier helpTooltipSupplier;

    protected EditorListNode(@Nonnull List<EditorSimpleDeletableWrapperNode<String>> list, @Nonnull ListFactory listFactory, @Nonnull EditorTextFieldOptionsNode topAdder, @Nonnull EditorTextFieldOptionsNode bottomAdder, boolean movable, @Nonnull EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier, @Nonnull EditorSimpleDeletableWrapperNode.DeletionCallback deletionCallback, @Nonnull IEditorDataTooltipSupplier helpTooltipSupplier) {
        super(movable, listEntryFactory, tooltipSupplier);
        this.list = list;
        this.listFactory = listFactory;
        this.topAdder = topAdder;
        this.bottomAdder = bottomAdder;
        this.deletionCallback = deletionCallback;
        this.helpTooltipSupplier = helpTooltipSupplier;
    }

    public List<EditorSimpleDeletableWrapperNode<String>> getList() {
        return this.list;
    }

    public EditorSimpleDeletableWrapperNode.DeletionCallback getDeletionCallback() {
        return this.deletionCallback;
    }

    private Consumer<EditorTextFieldOptionsNode> getAdderHandler() {
        return adder -> {
            String adderRequest = adder.getResult();
            if (adderRequest.isEmpty()) {
                return;
            }
            EditorSimpleWrapperNode element = ((EditorSimpleDeletableWrapperNode.Builder)EditorSimpleDeletableWrapperNode.Builder.begin().setElement(adderRequest)).setDeletionCallback(this.getDeletionCallback()).build();
            int sortedIndex = Collections.binarySearch(this.list, element);
            if (sortedIndex < 0) {
                this.list.add(sortedIndex ^= 0xFFFFFFFF, (EditorSimpleDeletableWrapperNode<String>)element);
            }
            adder.resetInput("");
        };
    }

    @Override
    public List<EditorNode> getSubNodes() {
        Consumer<EditorTextFieldOptionsNode> adderHandler = this.getAdderHandler();
        adderHandler.accept(this.topAdder);
        adderHandler.accept(this.bottomAdder);
        List<EditorNode> result = this.listFactory.get();
        if (this.list.size() > 0) {
            result.add(this.topAdder);
        }
        result.addAll(this.list);
        result.add(this.bottomAdder);
        return result;
    }

    public static abstract class Builder<E, P, ED extends EditorListNode, B extends Builder<E, P, ED, B>>
    extends EditorNode.Builder<B> {
        private final B self = this;
        protected final List<EditorSimpleDeletableWrapperNode.Builder<String>> list;
        protected final EditorTextFieldOptionsNode.Builder adderBuilder;
        protected ListFactory listFactory;
        protected EditorSimpleDeletableWrapperNode.DeletionCallback deletionCallback;
        private Predicate<String> inputRuleTypeStringValidator;
        protected IEditorDataTooltipSupplier helpTooltipSupplier;
        private ObjectCategoryListRuleType<E, P, ?> defaultListRuleType;
        private Iterable<ObjectCategoryListRuleType<E, P, ?>> listRuleTypes;
        private String listRuleTypePrefixSeparator;

        protected Builder(ListFactory listFactory) {
            this.list = listFactory.get();
            this.listFactory = listFactory;
            this.adderBuilder = EditorTextFieldOptionsNode.Builder.begin(listFactory);
        }

        @Override
        public B setDefault() {
            super.setDefault();
            this.list.clear();
            this.setDeletionCallback(null);
            this.adderBuilder.setDefault().setAllowCustomInput(false).setAutoConfirm(false).setDisplayName((Component)Component.translatable((String)"gui.xaero_category_list_add"));
            this.setDeletionCallback((parent, element, rowList) -> {
                EditorListNode listData = (EditorListNode)parent;
                if (listData.getList().remove(element)) {
                    rowList.restoreScrollAfterUpdate();
                    return true;
                }
                return false;
            });
            this.setHelpTooltipSupplier((parent, data) -> null);
            this.setDefaultListRuleType(null);
            this.setListRuleTypes(null);
            this.setListRuleTypePrefixSeparator(null);
            this.setInputRuleTypeStringValidator(null);
            return this.self;
        }

        @Override
        protected EditorListRootEntry mainEntryFactory(EditorNode data, EditorNode parent, int index, ConnectionLineType lineType, GuiCategoryEditor.SettingRowList rowList, int screenWidth, boolean isFinalExpanded) {
            EditorListNode elData = (EditorListNode)data;
            return new EditorListEntryWrapper(this.getCenteredEntryFactory(data, parent, index, rowList), screenWidth, index, rowList, lineType, data, (Supplier<Tooltip>)((Supplier)elData.helpTooltipSupplier.apply(parent, elData)));
        }

        @Override
        protected EditorListRootEntry.CenteredEntryFactory getCenteredEntryFactory(EditorNode data, EditorNode parent, int index, GuiCategoryEditor.SettingRowList rowList) {
            return (x, y, width, height, root) -> {
                EditorButton button = new EditorButton(parent, true, 216, 20, data, rowList);
                return new EditorListEntryWidget(x, y, width, height, index, rowList, root, (AbstractWidget)button, data.getTooltipSupplier(parent));
            };
        }

        public B addListElement(String element) {
            this.list.add((EditorSimpleDeletableWrapperNode.Builder)EditorSimpleDeletableWrapperNode.Builder.begin().setElement(element));
            return this.self;
        }

        public B setDeletionCallback(EditorSimpleDeletableWrapperNode.DeletionCallback deletionCallback) {
            this.deletionCallback = deletionCallback;
            return this.self;
        }

        public EditorTextFieldOptionsNode.Builder getAdderBuilder() {
            return this.adderBuilder;
        }

        protected List<EditorSimpleDeletableWrapperNode<String>> buildList() {
            return this.list.stream().map(builder -> builder.setDeletionCallback(this.deletionCallback).build()).sorted().collect(this.listFactory::get, List::add, List::addAll);
        }

        public B setInputRuleTypeStringValidator(Predicate<String> inputRuleTypeStringValidator) {
            this.inputRuleTypeStringValidator = inputRuleTypeStringValidator;
            return this.self;
        }

        public B setHelpTooltipSupplier(IEditorDataTooltipSupplier helpTooltipSupplier) {
            this.helpTooltipSupplier = helpTooltipSupplier;
            return this.self;
        }

        public B setHelpTooltipInfoSupplier(BiFunction<EditorNode, EditorNode, TooltipInfo> tooltipInfoSupplier) {
            return this.setHelpTooltipSupplier((parent, data) -> new Tooltip((TooltipInfo)tooltipInfoSupplier.apply((EditorNode)parent, (EditorNode)data)));
        }

        public B setDefaultListRuleType(ObjectCategoryListRuleType<E, P, ?> defaultListRuleType) {
            this.defaultListRuleType = defaultListRuleType;
            return this.self;
        }

        public B setListRuleTypes(Iterable<ObjectCategoryListRuleType<E, P, ?>> listRuleTypes) {
            this.listRuleTypes = listRuleTypes;
            return this.self;
        }

        public B setListRuleTypePrefixSeparator(String listRuleTypePrefixSeparator) {
            this.listRuleTypePrefixSeparator = listRuleTypePrefixSeparator;
            return this.self;
        }

        @Override
        public EditorNode build() {
            if (this.deletionCallback == null || this.helpTooltipSupplier == null || this.defaultListRuleType == null || this.listRuleTypes == null || this.listRuleTypePrefixSeparator == null) {
                throw new IllegalStateException("required fields not set!");
            }
            String listRuleTypePrefixSeparator = this.listRuleTypePrefixSeparator;
            Predicate<String> inputRuleTypeStringValidator = this.inputRuleTypeStringValidator;
            Iterable listRuleTypes = this.listRuleTypes;
            Predicate<String> inputStringValidator = s -> {
                for (ObjectCategoryListRuleType listRuleType : listRuleTypes) {
                    if (!listRuleType.getStringValidator().test((String)s)) continue;
                    return true;
                }
                return false;
            };
            if (this.adderBuilder.needsInputStringValidator()) {
                this.adderBuilder.setInputStringValidator(s -> {
                    int separatorIndex = s.indexOf(listRuleTypePrefixSeparator);
                    if (separatorIndex == -1) {
                        return inputStringValidator.test((String)s);
                    }
                    String listRuleTypeString = s.substring(0, separatorIndex);
                    if (inputRuleTypeStringValidator != null && !inputRuleTypeStringValidator.test(listRuleTypeString)) {
                        return false;
                    }
                    String elementString = s.substring(separatorIndex + 1);
                    return inputStringValidator.test(elementString);
                });
            }
            for (ObjectCategoryListRuleType<E, P, ?> listRuleType : listRuleTypes) {
                String prefix = listRuleType == this.defaultListRuleType ? "" : listRuleType.getId() + listRuleTypePrefixSeparator;
                this.addSuggestionsForListRuleType(listRuleType, prefix);
            }
            return super.build();
        }

        private <S> void addSuggestionsForListRuleType(ObjectCategoryListRuleType<E, P, S> listRuleType, String prefix) {
            listRuleType.getAllElementSupplier().get().forEach(e -> this.adderBuilder.addOptionBuilderFor(prefix + listRuleType.getSerializer().apply(e)));
        }

        protected abstract ED buildInternally();
    }
}

