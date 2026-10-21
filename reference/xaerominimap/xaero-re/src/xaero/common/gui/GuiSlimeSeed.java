/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;
import xaero.common.IXaeroMinimap;
import xaero.common.gui.GuiMinimapSettings;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.lib.client.gui.ISettingEntry;
import xaero.lib.client.gui.config.context.IEditConfigScreenContext;
import xaero.lib.common.config.option.ConfigOption;

public class GuiSlimeSeed
extends GuiMinimapSettings {
    public EditBox seedTextField;
    private final MinimapWorld minimapWorld;
    private final IXaeroMinimap modMain;
    private final MinimapSession session;

    public GuiSlimeSeed(IXaeroMinimap modMain, MinimapSession session, Screen parent, Screen escape, IEditConfigScreenContext context) {
        super((Component)Component.translatable((String)"gui.xaero_slime_chunks"), parent, escape, context);
        this.modMain = modMain;
        this.session = session;
        this.entries = new ISettingEntry[]{this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.SLIME_CHUNKS), this.optionEntry((ConfigOption)MinimapProfiledConfigOptions.OPEN_SLIME_CHUNKS_SCREEN)};
        this.minimapWorld = session.getWorldManager().getAutoWorld();
    }

    public void init() {
        super.init();
        this.seedTextField = new EditBox(this.font, this.width / 2 - 100, this.height / 7 + 68, 200, 20, (Component)Component.translatable((String)"gui.xaero_used_seed"));
        this.seedTextField.setValue(String.valueOf(this.minimapWorld.getSlimeChunkSeed() == null ? "" : this.minimapWorld.getSlimeChunkSeed()));
        this.addRenderableWidget((GuiEventListener)this.seedTextField);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partial);
        this.seedTextField.extractRenderState(guiGraphics, mouseX, mouseY, partial);
        guiGraphics.centeredText(this.font, I18n.get((String)"gui.xaero_used_seed", (Object[])new Object[0]), this.width / 2, this.height / 7 + 55, -1);
    }

    public void tick() {
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        boolean result = super.keyPressed(event);
        if (event.key() == 40) {
            this.goBack();
        }
        this.updateSlimeSeed();
        return result;
    }

    public boolean charTyped(CharacterEvent event) {
        boolean result = super.charTyped(event);
        this.updateSlimeSeed();
        return result;
    }

    private void updateSlimeSeed() {
        String s = this.seedTextField.getValue();
        if (!StringUtils.isEmpty((CharSequence)s)) {
            try {
                long j = Long.parseLong(s);
                this.minimapWorld.setSlimeChunkSeed(j);
            }
            catch (NumberFormatException numberformatexception) {
                this.minimapWorld.setSlimeChunkSeed(Long.valueOf(s.hashCode()));
            }
        }
        try {
            this.session.getWorldManagerIO().saveWorld(this.minimapWorld);
        }
        catch (IOException e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }
}

