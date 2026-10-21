/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import xaero.common.misc.ListFactory;
import xaero.hud.category.ObjectCategory;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.ConnectionLineType;
import xaero.hud.category.ui.entry.EditorListEntryCategory;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.node.EditorAdderNode;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.EditorSettingsNode;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;
import xaero.lib.client.gui.widget.Tooltip;

public abstract class EditorCategoryNode<C extends ObjectCategory<?, C>, SD extends EditorSettingsNode<?>, ED extends EditorCategoryNode<C, SD, ED>>
extends EditorNode {
    private final ED self = this;
    private boolean cut;
    private final List<ED> subCategories;
    private final EditorAdderNode topAdder;
    private final Function<EditorAdderNode, ED> newCategorySupplier;
    private final SD settingsNode;

    protected EditorCategoryNode(@Nonnull SD settingNode, @Nonnull List<ED> subCategories, @Nonnull EditorAdderNode topAdder, @Nonnull Function<EditorAdderNode, ED> newCategorySupplier, boolean movable, int subIndex, @Nonnull EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier) {
        super(movable, listEntryFactory, tooltipSupplier);
        this.settingsNode = settingNode;
        this.subCategories = subCategories;
        this.topAdder = topAdder;
        this.newCategorySupplier = newCategorySupplier;
    }

    public SD getSettingsNode() {
        return this.settingsNode;
    }

    public final List<ED> getSubCategories() {
        return this.subCategories;
    }

    public String getName() {
        return ((EditorSettingsNode)this.settingsNode).getNameOption().getResult();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable((String)this.getName());
    }

    private BiConsumer<EditorAdderNode, Integer> getAdderHandler() {
        return (adder, i) -> {
            if (!adder.isConfirmed()) {
                return;
            }
            EditorCategoryNode newCategory = (EditorCategoryNode)this.newCategorySupplier.apply((EditorAdderNode)adder);
            this.subCategories.add((int)i, (ED)newCategory);
            adder.reset();
        };
    }

    private Runnable getDeletionHandler() {
        return () -> {
            Iterator<ED> subIterator = this.subCategories.iterator();
            while (subIterator.hasNext()) {
                EditorCategoryNode subCategory = (EditorCategoryNode)subIterator.next();
                if (!((EditorSettingsNode)subCategory.getSettingsNode()).isToBeDeleted()) continue;
                subIterator.remove();
            }
        };
    }

    public Supplier<Boolean> getMoveAction(int subIndex, int direction, GuiCategoryEditor.SettingRowList rowList) {
        return () -> {
            int newSlot = subIndex + direction;
            EditorCategoryNode subCategoryToMove = (EditorCategoryNode)this.subCategories.get(subIndex);
            rowList.setLastExpandedData(subCategoryToMove);
            if (newSlot < 0) {
                this.subCategories.remove(subCategoryToMove);
                this.subCategories.add(subCategoryToMove);
                return true;
            }
            if (newSlot >= this.subCategories.size()) {
                this.subCategories.remove(subCategoryToMove);
                this.subCategories.add(0, subCategoryToMove);
                return true;
            }
            rowList.restoreScrollAfterUpdate();
            EditorCategoryNode subCategoryToReplace = (EditorCategoryNode)this.subCategories.get(newSlot);
            this.subCategories.set(subIndex, subCategoryToReplace);
            this.subCategories.set(newSlot, subCategoryToMove);
            return true;
        };
    }

    public Supplier<Boolean> getDuplicateAction(int subIndex, GuiCategoryEditor.SettingRowList rowList) {
        return () -> {
            if (subIndex < 0 || subIndex >= this.subCategories.size()) {
                return false;
            }
            EditorCategoryNode subCategoryToDuplicate = (EditorCategoryNode)this.subCategories.get(subIndex);
            GuiCategoryEditor screenToRestore = (GuiCategoryEditor)Minecraft.getInstance().gui.screen();
            MutableComponent confirmSecondLine = subCategoryToDuplicate.getDisplayName().copy().withStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat((ChatFormatting)ChatFormatting.YELLOW)));
            Minecraft.getInstance().gui.setScreen((Screen)new ConfirmScreen(result -> {
                if (!result) {
                    Minecraft.getInstance().gui.setScreen((Screen)screenToRestore);
                    return;
                }
                Object convertedCategory = rowList.getDataConverter().convert(subCategoryToDuplicate);
                Object reconstructedEditorData = rowList.getDataConverter().convert(convertedCategory, false);
                ((EditorCategoryNode)reconstructedEditorData).removeProtectionRecursive();
                this.subCategories.add(subIndex + 1, reconstructedEditorData);
                Minecraft.getInstance().gui.setScreen((Screen)screenToRestore);
                GuiCategoryEditor.SettingRowList newRowList = screenToRestore.getRowList();
                newRowList.setLastExpandedData((EditorNode)reconstructedEditorData);
                newRowList.updateEntries();
            }, (Component)Component.translatable((String)"gui.xaero_category_duplicate_confirm"), (Component)confirmSecondLine));
            return true;
        };
    }

    public Supplier<Boolean> getCutAction(ED parent, GuiCategoryEditor.SettingRowList rowList) {
        return () -> {
            rowList.setCutCategory(this.self, parent);
            rowList.setLastExpandedData(this);
            rowList.restoreScrollAfterUpdate();
            return true;
        };
    }

    public Supplier<Boolean> getPasteAction(GuiCategoryEditor.SettingRowList rowList) {
        return () -> {
            rowList.pasteTo(this.self);
            rowList.restoreScrollAfterUpdate();
            return true;
        };
    }

    @Override
    public List<EditorNode> getSubNodes() {
        BiConsumer<EditorAdderNode, Integer> adderHandler = this.getAdderHandler();
        adderHandler.accept(this.topAdder, 0);
        this.getDeletionHandler().run();
        ArrayList<EditorNode> result = new ArrayList<EditorNode>(this.subCategories);
        result.add(0, this.topAdder);
        result.add(0, (EditorNode)this.settingsNode);
        return result;
    }

    public void removeProtectionRecursive() {
        ((EditorSettingsNode)this.getSettingsNode()).setProtected(false);
        for (EditorCategoryNode sub : this.subCategories) {
            sub.removeProtectionRecursive();
        }
    }

    public static abstract class Builder<C extends ObjectCategory<?, C>, ED extends EditorCategoryNode<C, SD, ED>, SD extends EditorSettingsNode<?>, SDB extends EditorSettingsNode.Builder<SD, SDB>, EDB extends Builder<C, ED, SD, SDB, EDB>>
    extends EditorNode.Builder<EDB> {
        protected final EDB self;
        protected String name;
        protected final SDB settingsDataBuilder;
        protected final List<EDB> subCategoryBuilders;
        protected final ListFactory listFactory;
        protected final EditorAdderNode.Builder topAdderBuilder;
        protected Function<EditorAdderNode, ED> newCategorySupplier;
        protected int subIndex;

        protected Builder(ListFactory listFactory, SDB settingsDataBuilder) {
            if (settingsDataBuilder == null) {
                throw new IllegalStateException("settings data builder cannot be null!");
            }
            this.settingsDataBuilder = settingsDataBuilder;
            this.subCategoryBuilders = listFactory.get();
            this.listFactory = listFactory;
            this.topAdderBuilder = EditorAdderNode.Builder.begin(listFactory);
            this.self = this;
        }

        @Override
        public EDB setDefault() {
            super.setDefault();
            this.setName(null);
            ((EditorSettingsNode.Builder)this.settingsDataBuilder).setDefault();
            this.subCategoryBuilders.clear();
            this.topAdderBuilder.setDisplayName((Component)Component.translatable((String)"gui.xaero_category_add_subcategory"));
            this.setMovable(true);
            this.setSubIndex(0);
            this.setTooltipSupplier((parent, data) -> {
                Component displayNameComponent = data.getDisplayName();
                Tooltip tooltip = new Tooltip((Component)Component.translatable((String)"gui.xaero_box_category", (Object[])new Object[]{displayNameComponent}));
                tooltip.setAutoLinebreak(false);
                return tooltip;
            });
            return this.self;
        }

        @Override
        protected EditorListRootEntry mainEntryFactory(EditorNode data, EditorNode parent, int index, ConnectionLineType lineType, GuiCategoryEditor.SettingRowList rowList, int screenWidth, boolean isFinalExpanded) {
            return new EditorListEntryCategory(screenWidth, index, rowList, lineType, (EditorCategoryNode)data, (EditorCategoryNode)parent, data.getTooltipSupplier(parent), isFinalExpanded);
        }

        public EDB setNewCategorySupplier(Function<EditorAdderNode, ED> newCategorySupplier) {
            this.newCategorySupplier = newCategorySupplier;
            return this.self;
        }

        public EDB setSubIndex(int subIndex) {
            this.subIndex = subIndex;
            return this.self;
        }

        public EDB setName(String name) {
            this.name = name;
            return this.self;
        }

        public SDB getSettingDataBuilder() {
            return this.settingsDataBuilder;
        }

        public EDB addSubCategoryBuilder(EDB subCategory) {
            ((Builder)subCategory).setSubIndex(this.subCategoryBuilders.size());
            this.subCategoryBuilders.add(subCategory);
            return this.self;
        }

        /*
         * Exception decompiling
         */
        protected List<ED> buildSubCategories() {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
             *     at java.util.ArrayList.rangeCheck(ArrayList.java:659)
             *     at java.util.ArrayList.get(ArrayList.java:435)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteDynamicExpression(LambdaRewriter.java:368)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteDynamicExpression(LambdaRewriter.java:167)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:105)
             *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriterHelper.applyForwards(ExpressionRewriterHelper.java:12)
             *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriterToArgs(AbstractMemberFunctionInvokation.java:101)
             *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:88)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
             *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
             *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredReturn.rewriteExpressions(StructuredReturn.java:99)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewrite(LambdaRewriter.java:88)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.rewriteLambdas(Op04StructuredStatement.java:1137)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:912)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
             *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
             *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
             *     at org.benf.cfr.reader.Main.main(Main.java:54)
             */
            throw new IllegalStateException("Decompilation failed");
        }

        public ED build() {
            if (this.name == null || this.newCategorySupplier == null) {
                throw new IllegalStateException("required fields not set!");
            }
            ((EditorSettingsNode.Builder)this.settingsDataBuilder).getNameOptionBuilder().setInput(this.name);
            ((EditorSettingsNode.Builder)this.settingsDataBuilder).getNameOptionBuilder().setDisplayName((Component)Component.translatable((String)"gui.xaero_category_name"));
            ((EditorSettingsNode.Builder)this.settingsDataBuilder).getNameOptionBuilder().setMaxLength(200);
            EditorCategoryNode result = (EditorCategoryNode)super.build();
            return (ED)result;
        }
    }
}

