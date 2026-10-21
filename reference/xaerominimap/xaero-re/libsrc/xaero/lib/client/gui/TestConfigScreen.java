/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xaero.lib.client.gui.ISettingEntry;
import xaero.lib.client.gui.config.EditConfigScreen;
import xaero.lib.client.gui.config.context.IEditConfigScreenContext;
import xaero.lib.common.config.channel.ConfigChannel;
import xaero.lib.common.config.option.BuiltInProfiledConfigOptions;

public class TestConfigScreen
extends EditConfigScreen {
    public TestConfigScreen(Screen backScreen, Screen escScreen, IEditConfigScreenContext context, ConfigChannel channel) {
        super((Component)Component.literal((String)"test"), backScreen, escScreen, context, channel);
        this.entries = new ISettingEntry[]{this.createProfileIDEntry(), this.optionEntry(BuiltInProfiledConfigOptions.PROFILE_NAME), this.optionEntry(BuiltInProfiledConfigOptions.IGNORE_ENFORCEMENT_IF_EDITOR)};
    }
}

