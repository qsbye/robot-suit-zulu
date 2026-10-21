/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.primary.option;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.option.value.type.BuiltInConfigValueTypes;
import xaero.lib.common.config.util.ConfigUtils;

public class BuiltInPrimaryCommonConfigOptions {
    private static final List<ConfigOption<?>> ALL = new ArrayList();
    public static final ConfigOption<String> DEFAULT_ENFORCED_PROFILE = ((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)ConfigOption.FinalBuilder.begin().setId("default_enforced_profile")).setDefaultValue("default")).setValidator(s -> !s.isEmpty() && s.length() <= 50)).setDisplayName((Component)Component.translatable((String)"gui.xaero_default_enforced_profile"))).setTooltip((Component)Component.translatable((String)"gui.xaero_box_default_enforced_profile"))).setValueType(BuiltInConfigValueTypes.getString(50))).setDisplayGetter(ConfigUtils::getDisplayForString)).build(ALL);

    public static void registerAll(ConfigOptionManager manager) {
        for (ConfigOption<?> option : ALL) {
            manager.register(option);
        }
    }
}

