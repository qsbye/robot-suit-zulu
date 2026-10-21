/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import xaero.common.misc.ListFactory;
import xaero.common.misc.MapFactory;
import xaero.hud.category.setting.ObjectCategorySetting;
import xaero.hud.category.ui.GuiCategoryEditor;
import xaero.hud.category.ui.entry.EditorListEntryWidget;
import xaero.hud.category.ui.entry.EditorListRootEntry;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.entry.widget.EditorButton;
import xaero.hud.category.ui.node.EditorCategoryNode;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.options.EditorOptionsNode;
import xaero.hud.category.ui.node.options.EditorSimpleButtonNode;
import xaero.hud.category.ui.node.options.range.setting.IEditorSettingNode;
import xaero.hud.category.ui.node.options.range.setting.IEditorSettingNodeBuilder;
import xaero.hud.category.ui.node.options.text.EditorTextFieldOptionsNode;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;
import xaero.hud.category.ui.setting.SettingNodeBuilderFactoryManager;
import xaero.lib.client.gui.widget.Tooltip;

public class EditorSettingsNode<SETTING_DATA extends EditorOptionsNode<?>>
extends EditorNode {
    private final Map<ObjectCategorySetting<?>, SETTING_DATA> settings;
    private final List<SETTING_DATA> settingList;
    private boolean toBeDeleted;
    private final EditorSimpleButtonNode deleteButton;
    private final EditorSimpleButtonNode protectionButton;
    private final EditorTextFieldOptionsNode nameOption;
    private final ListFactory listFactory;
    private final boolean rootSettings;
    private boolean protection;

    protected EditorSettingsNode(@Nonnull Map<ObjectCategorySetting<?>, SETTING_DATA> settings, @Nonnull List<SETTING_DATA> settingList, @Nonnull EditorSimpleButtonNode deleteButton, @Nonnull EditorSimpleButtonNode protectionButton, @Nonnull EditorTextFieldOptionsNode nameOption, @Nonnull ListFactory listFactory, boolean rootSettings, boolean movable, @Nonnull EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier, boolean protection) {
        super(movable, listEntryFactory, tooltipSupplier);
        this.settings = settings;
        this.settingList = settingList;
        this.listFactory = listFactory;
        this.rootSettings = rootSettings;
        this.deleteButton = deleteButton;
        this.protectionButton = protectionButton;
        this.nameOption = nameOption;
        this.protection = protection;
    }

    public Map<ObjectCategorySetting<?>, SETTING_DATA> getSettings() {
        return this.settings;
    }

    public IEditorSettingNode<?> getSettingData(ObjectCategorySetting<?> setting) {
        return (IEditorSettingNode)this.settings.get(setting);
    }

    public boolean isRootSettings() {
        return this.rootSettings;
    }

    public boolean isToBeDeleted() {
        return this.toBeDeleted;
    }

    public void setToBeDeleted() {
        this.toBeDeleted = true;
    }

    public boolean getProtection() {
        return this.protection;
    }

    public void setProtected(boolean protection) {
        this.protection = protection;
    }

    @Override
    public List<EditorNode> getSubNodes() {
        List<EditorNode> result = this.listFactory.get();
        result.addAll(this.settingList);
        if (!this.protection) {
            result.add(this.nameOption);
        }
        result.add(this.deleteButton);
        result.add(this.protectionButton);
        return result;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable((String)"gui.xaero_category_settings");
    }

    public EditorTextFieldOptionsNode getNameOption() {
        return this.nameOption;
    }

    public static final class FinalBuilder
    extends Builder<EditorSettingsNode<?>, FinalBuilder> {
        private FinalBuilder(MapFactory mapFactory, ListFactory listFactory, List<ObjectCategorySetting<?>> allSettings, SettingNodeBuilderFactoryManager settingNodeBuilderFactoryManager) {
            super(mapFactory, listFactory, allSettings, settingNodeBuilderFactoryManager);
        }

        @Override
        protected EditorSettingsNode<?> buildInternally(List<IEditorSettingNode<?>> builtSettingData, Map<ObjectCategorySetting<?>, IEditorSettingNode<?>> builtSettingsDataMap) {
            EditorSettingsNode result = new EditorSettingsNode(builtSettingsDataMap, builtSettingData, this.deleteButtonBuilder.build(), this.protectionButtonBuilder.build(), this.nameOptionBuilder.build(), this.listFactory, this.rootSettings, this.movable, this.listEntryFactory, this.tooltipSupplier, this.protection);
            return result;
        }
    }

    public static abstract class Builder<SD extends EditorSettingsNode<?>, SDB extends Builder<SD, SDB>>
    extends EditorNode.Builder<Builder<SD, SDB>> {
        protected final SDB self = this;
        protected final Map<ObjectCategorySetting<?>, IEditorSettingNodeBuilder<?, ?>> settingMap;
        protected final List<IEditorSettingNodeBuilder<?, ?>> settingList;
        protected final EditorSimpleButtonNode.Builder deleteButtonBuilder;
        protected final EditorSimpleButtonNode.Builder protectionButtonBuilder;
        protected final EditorTextFieldOptionsNode.Builder nameOptionBuilder;
        protected final MapFactory mapFactory;
        protected final ListFactory listFactory;
        protected boolean rootSettings;
        protected boolean protection;

        protected Builder(MapFactory mapFactory, ListFactory listFactory, List<ObjectCategorySetting<?>> allSettings, SettingNodeBuilderFactoryManager settingNodeBuilderFactoryManager) {
            this.settingMap = mapFactory.get();
            this.settingList = listFactory.get();
            this.deleteButtonBuilder = EditorSimpleButtonNode.Builder.begin();
            this.protectionButtonBuilder = EditorSimpleButtonNode.Builder.begin();
            this.nameOptionBuilder = EditorTextFieldOptionsNode.Builder.begin(listFactory);
            this.mapFactory = mapFactory;
            this.listFactory = listFactory;
            for (ObjectCategorySetting<?> setting : allSettings) {
                this.addSetting(setting, settingNodeBuilderFactoryManager);
            }
        }

        private <V> void addSetting(ObjectCategorySetting<V> setting, SettingNodeBuilderFactoryManager settingNodeBuilderFactoryManager) {
            IEditorSettingNodeBuilder builder = settingNodeBuilderFactoryManager.get(setting).apply(this.listFactory).setSetting(setting);
            this.settingMap.put(setting, builder);
            this.settingList.add(builder);
        }

        @Override
        public SDB setDefault() {
            super.setDefault();
            for (IEditorSettingNodeBuilder<?, ?> builder : this.settingList) {
                builder.setSettingValue(null);
            }
            this.setRootSettings(false);
            this.nameOptionBuilder.setDefault();
            this.deleteButtonBuilder.setDefault().setDisplayName((Component)Component.translatable((String)"gui.xaero_category_delete")).setCallback((parent, bd, rl) -> {
                EditorSettingsNode settings = (EditorSettingsNode)parent;
                Minecraft mc = Minecraft.getInstance();
                Screen configScreen = mc.gui.screen();
                MutableComponent confirmSecondLine = Component.translatable((String)settings.getNameOption().getResult()).withStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat((ChatFormatting)ChatFormatting.RED)));
                mc.gui.setScreen((Screen)new ConfirmScreen(result -> {
                    if (result) {
                        settings.setToBeDeleted();
                    }
                    mc.gui.setScreen(configScreen);
                }, (Component)Component.translatable((String)"gui.xaero_category_delete_confirm"), (Component)confirmSecondLine));
            }).setIsActiveSupplier((parent, data, rowList) -> !rowList.readOnly && !((EditorSettingsNode)parent).getProtection());
            this.protectionButtonBuilder.setDefault().setDisplayName((Component)Component.literal((String)"")).setCallback((parent, bd, rl) -> {
                EditorSettingsNode settings = (EditorSettingsNode)parent;
                boolean currentlyProtected = settings.getProtection();
                Minecraft mc = Minecraft.getInstance();
                Screen configScreen = mc.gui.screen();
                MutableComponent confirmFirstLine = Component.translatable((String)(currentlyProtected ? "gui.xaero_category_disable_protection_confirm" : "gui.xaero_category_enable_protection_confirm"));
                ChatFormatting confirmSecondLineColor = currentlyProtected ? ChatFormatting.RED : ChatFormatting.GREEN;
                MutableComponent confirmSecondLine = Component.translatable((String)settings.getNameOption().getResult()).withStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat((ChatFormatting)confirmSecondLineColor)));
                mc.gui.setScreen((Screen)new ConfirmScreen(result -> {
                    if (result) {
                        settings.setProtected(!settings.getProtection());
                    }
                    mc.gui.setScreen(configScreen);
                }, (Component)confirmFirstLine, (Component)confirmSecondLine));
            }).setMessageSupplier((parent, bd) -> () -> Component.translatable((String)(((EditorSettingsNode)parent).getProtection() ? "gui.xaero_category_disable_protection" : "gui.xaero_category_enable_protection"))).setIsActiveSupplier((parent, bd, rowList) -> !rowList.readOnly && !((EditorSettingsNode)parent).isRootSettings()).setTooltipSupplier((parent, bd) -> new Tooltip((Component)Component.translatable((String)"gui.xaero_box_category_protection")));
            this.setTooltipSupplier((parent, data) -> {
                if (!(parent instanceof EditorCategoryNode)) {
                    return null;
                }
                EditorCategoryNode category = (EditorCategoryNode)parent;
                Component displayNameComponent = category.getDisplayName();
                Tooltip tooltip = new Tooltip((Component)Component.translatable((String)"gui.xaero_box_category_settings", (Object[])new Object[]{displayNameComponent}));
                tooltip.setAutoLinebreak(false);
                return tooltip;
            });
            return this.self;
        }

        @Override
        protected EditorListRootEntry.CenteredEntryFactory getCenteredEntryFactory(EditorNode data, EditorNode parent, int index, GuiCategoryEditor.SettingRowList rowList) {
            return (x, y, width, height, root) -> {
                EditorButton button = new EditorButton(parent, true, 216, 20, data, rowList);
                return new EditorListEntryWidget(x, y, width, height, index, rowList, root, (AbstractWidget)button, data.getTooltipSupplier(parent));
            };
        }

        public <T> SDB setSettingValue(ObjectCategorySetting<T> setting, T value) {
            IEditorSettingNodeBuilder<?, ?> settingBuilder = this.settingMap.get(setting);
            settingBuilder.setSettingValue(value);
            return this.self;
        }

        public SDB setRootSettings(boolean rootSettings) {
            this.rootSettings = rootSettings;
            return this.self;
        }

        public SDB setProtection(boolean protection) {
            this.protection = protection;
            return this.self;
        }

        public EditorTextFieldOptionsNode.Builder getNameOptionBuilder() {
            return this.nameOptionBuilder;
        }

        public EditorSimpleButtonNode.Builder getDeleteButtonBuilder() {
            return this.deleteButtonBuilder;
        }

        @Override
        protected EditorNode buildInternally() {
            if (this.nameOptionBuilder.needsInputStringValidator()) {
                this.nameOptionBuilder.setInputStringValidator(s -> true);
            }
            List builtSettingData = this.settingList.stream().map(b -> b.setRootSettings(this.rootSettings)).map(IEditorSettingNodeBuilder::build).collect(this.listFactory::get, (rec$, x$0) -> rec$.add((IEditorSettingNode)((Object)x$0)), List::addAll);
            Map<ObjectCategorySetting<?>, IEditorSettingNode<?>> builtSettingsDataMap = this.mapFactory.get();
            for (IEditorSettingNode sd : builtSettingData) {
                if (!(sd instanceof EditorOptionsNode)) {
                    throw new IllegalStateException("illegal setting data class! " + String.valueOf(sd.getClass()));
                }
                builtSettingsDataMap.put(sd.getSetting(), sd);
            }
            return this.buildInternally(builtSettingData, builtSettingsDataMap);
        }

        protected abstract SD buildInternally(List<IEditorSettingNode<?>> var1, Map<ObjectCategorySetting<?>, IEditorSettingNode<?>> var2);
    }
}

