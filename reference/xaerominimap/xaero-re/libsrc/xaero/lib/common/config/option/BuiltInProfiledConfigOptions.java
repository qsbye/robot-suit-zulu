/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.option;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import xaero.lib.common.config.option.BooleanConfigOption;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.option.value.type.BuiltInConfigValueTypes;
import xaero.lib.common.config.util.ConfigUtils;

public class BuiltInProfiledConfigOptions {
    private static final List<ConfigOption<?>> ALL = new ArrayList();
    public static final ConfigOption<String> PROFILE_NAME = ((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)ConfigOption.FinalBuilder.begin().setId("profile_name")).setDisplayName((Component)Component.translatable((String)"gui.xaero_config_option_profile_name"))).setDefaultValue("Untitled")).setValidator(s -> !s.isEmpty() && s.length() <= 50)).setValueType(BuiltInConfigValueTypes.getString(50))).setDisplayGetter(ConfigUtils::getDisplayForString)).setSkipConfigReset(true)).setOverridable(false)).build(ALL);
    public static final BooleanConfigOption IGNORE_ENFORCEMENT_IF_EDITOR = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("ignore_enforcement_if_edit_permission")).setDefaultValue(false)).setDisplayName((Component)Component.translatable((String)"gui.xaero_ignore_enforcement_if_edit_permission"))).setTooltip((Component)Component.translatable((String)"gui.xaero_box_ignore_enforcement_if_edit_permission"))).build((List)ALL);

    public static void registerAll(ConfigOptionManager manager) {
        for (ConfigOption<?> option : ALL) {
            manager.register(option);
        }
    }
}

