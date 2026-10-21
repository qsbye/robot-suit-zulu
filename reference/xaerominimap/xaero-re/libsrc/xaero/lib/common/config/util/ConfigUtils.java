/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.util;

import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import xaero.lib.client.config.option.value.redirect.ClientOptionValueRedirectorManager;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.SteppedConfigOption;
import xaero.lib.common.config.util.ConfigConstants;

public class ConfigUtils {
    public static Component getDisplayForSimpleNumber(ConfigOption<? extends Number> option, Number value, Component unit) {
        Object valueString;
        if (option instanceof SteppedConfigOption) {
            int precision = ((SteppedConfigOption)option).getPrecision();
            String format = "%." + precision + "f";
            valueString = String.format(format, value);
        } else {
            valueString = String.valueOf(value);
        }
        MutableComponent valueComponent = Component.literal((String)valueString);
        if (unit != null) {
            valueComponent.getSiblings().add(Component.literal((String)" "));
            valueComponent.getSiblings().add(unit);
        }
        return valueComponent;
    }

    public static Component getDisplayForSimpleNumber(ConfigOption<? extends Number> option, Number value) {
        return ConfigUtils.getDisplayForSimpleNumber(option, value, null);
    }

    public static Component getDisplayForBoolean(ConfigOption<Boolean> option, Boolean value) {
        return value != false ? ConfigConstants.ON : ConfigConstants.OFF;
    }

    public static Component getDisplayForString(ConfigOption<String> stringConfigOption, String s) {
        return Component.literal((String)s);
    }

    public static String getAutoProfileIdForName(Predicate<String> exists, String profileName) {
        Object id = profileName.toLowerCase().trim().replaceAll("\\s", "_").replaceAll("[^a-z0-9_]", "");
        if (!((String)id).isEmpty() && !exists.test((String)id)) {
            return id;
        }
        int i = 0;
        while (exists.test((String)(id = "profile_" + i))) {
            ++i;
        }
        return id;
    }

    public static <T> Component getEffectiveValueName(ClientOptionValueRedirectorManager redirectorManager, ConfigOption<T> option, Config config, Config enforced) {
        MutableComponent configValueText;
        T configValue;
        if (redirectorManager != null && redirectorManager.shouldRedirect(option)) {
            Component customName = redirectorManager.getName(option);
            if (customName != null) {
                return customName;
            }
            configValue = redirectorManager.getValue(option);
        } else {
            configValue = config.get(option);
        }
        Object object = configValueText = configValue == null ? ConfigConstants.UNSPECIFIED : option.getDisplayGetter().apply((ConfigOption<ConfigOption<T>>)option, (ConfigOption<T>)configValue);
        if (enforced == null) {
            return configValueText;
        }
        T enforcedValue = enforced.get(option);
        if (enforcedValue == null) {
            return configValueText;
        }
        if (Objects.equals(configValue, enforcedValue)) {
            return configValueText;
        }
        MutableComponent enforcedValueText = option.getDisplayGetter().apply((ConfigOption<ConfigOption<T>>)option, (ConfigOption<T>)enforcedValue).copy().withStyle(ChatFormatting.YELLOW);
        MutableComponent combinedName = Component.translatable((String)"gui.xaero_config_value_enforced", (Object[])new Object[]{configValueText.copy().withStyle(ChatFormatting.WHITE), enforcedValueText}).withStyle(ChatFormatting.YELLOW);
        return combinedName;
    }
}

