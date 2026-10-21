/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.config.option.ui;

import xaero.lib.client.config.option.ui.ConfigOptionUITypeManager;
import xaero.lib.client.config.option.ui.type.BuiltInConfigOptionUITypes;
import xaero.lib.common.config.option.BuiltInProfiledConfigOptions;

public class BuiltInConfigOptionUIRegister {
    public static void registerAll(ConfigOptionUITypeManager manager) {
        manager.registerUIType(BuiltInProfiledConfigOptions.PROFILE_NAME, BuiltInConfigOptionUITypes.STRING_STRING_EDIT);
        manager.registerUIType(BuiltInProfiledConfigOptions.IGNORE_ENFORCEMENT_IF_EDITOR, BuiltInConfigOptionUITypes.TOGGLE);
    }
}

