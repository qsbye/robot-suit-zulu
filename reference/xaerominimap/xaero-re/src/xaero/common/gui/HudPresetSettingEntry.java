/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xaero.common.gui.GuiChoosePreset;
import xaero.hud.preset.HudPreset;
import xaero.hud.preset.HudPresetManager;
import xaero.lib.client.gui.ISettingEntry;

public class HudPresetSettingEntry
implements ISettingEntry {
    private final HudPresetManager manager;
    private final HudPreset preset;
    private final String searchString;

    public HudPresetSettingEntry(HudPresetManager manager, HudPreset preset) {
        this.manager = manager;
        this.preset = preset;
        StringBuilder searchStringBuilder = new StringBuilder();
        searchStringBuilder.append(preset.getId()).append(" ");
        preset.getName().visit(s -> {
            searchStringBuilder.append(s);
            return Optional.empty();
        });
        this.searchString = searchStringBuilder.toString();
    }

    public String getStringForSearch() {
        return this.searchString;
    }

    public AbstractWidget createWidget(int x, int y, int w) {
        return Button.builder((Component)this.preset.getName(), b -> {
            for (HudPreset preset : this.manager.getPresets()) {
                preset.cancel();
            }
            this.preset.apply();
            Screen patt0$temp = Minecraft.getInstance().gui.screen();
            if (patt0$temp instanceof GuiChoosePreset) {
                GuiChoosePreset gui = (GuiChoosePreset)patt0$temp;
                gui.goBack();
            }
        }).bounds(x, y, w, 20).build();
    }
}

