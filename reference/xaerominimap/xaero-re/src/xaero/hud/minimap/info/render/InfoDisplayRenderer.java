/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.info.render;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import xaero.common.HudMod;
import xaero.hud.minimap.Minimap;
import xaero.hud.minimap.common.config.MinimapConfigConstants;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.info.InfoDisplay;
import xaero.hud.minimap.info.render.compile.InfoDisplayCompiler;
import xaero.hud.minimap.module.MinimapSession;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.common.config.option.ConfigOption;

public final class InfoDisplayRenderer {
    public static final int DEPTH_OFFSET = 2;
    private final InfoDisplayCompiler compiler;

    private InfoDisplayRenderer(InfoDisplayCompiler compiler) {
        this.compiler = compiler;
    }

    public void render(MinimapSession session, Minimap minimap, int height, int size, BlockPos playerPos, int scaledX, int scaledY, float mapScale, GuiGraphicsExtractor guiGraphics) {
        ClientConfigManager configManager = HudMod.INSTANCE.getHudConfigs().getClientConfigManager();
        Iterator iterator = minimap.getInfoDisplays().getManager().getOrderedStream().iterator();
        int interfaceSize = size;
        int scaledHeight = (int)((float)height * mapScale);
        int align = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.INFO_DISPLAY_ALIGNMENT);
        boolean under = scaledY + interfaceSize / 2 < scaledHeight / 2;
        int stringY = scaledY + (under ? interfaceSize : -9);
        int bgOpacityConfig = (Integer)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.INFO_DISPLAY_BG_OPACITY);
        int bgOpacityMask = bgOpacityConfig * 255 / 100 << 24;
        while (iterator.hasNext()) {
            InfoDisplay infoDisplay = (InfoDisplay)iterator.next();
            List<Component> compiledLines = this.compiler.compile(infoDisplay, session, size, playerPos);
            int textColorIndex = infoDisplay.getTextColor();
            int backgroundColorIndex = infoDisplay.getBackgroundColor();
            int textColor = MinimapConfigConstants.COLORS[textColorIndex < 0 ? 15 : textColorIndex % MinimapConfigConstants.COLORS.length];
            int backgroundColor = backgroundColorIndex < 0 ? 0 : bgOpacityMask | MinimapConfigConstants.COLORS[backgroundColorIndex % MinimapConfigConstants.COLORS.length] & 0xFFFFFF;
            int startIndex = 0;
            int endIndex = compiledLines.size();
            int step = 1;
            if (!under) {
                startIndex = endIndex - 1;
                endIndex = -1;
                step = -1;
            }
            for (int i = startIndex; i != endIndex; i += step) {
                Component s = compiledLines.get(i);
                int stringWidth = Minecraft.getInstance().font.width((FormattedText)s);
                int stringX = scaledX + (align == 0 ? interfaceSize / 2 - stringWidth / 2 : (align == 1 ? 6 : interfaceSize - 6 - stringWidth));
                if (backgroundColor != 0) {
                    guiGraphics.fill(stringX - 1, stringY - 1, stringX + stringWidth + 1, stringY + 9, backgroundColor);
                }
                guiGraphics.text(Minecraft.getInstance().font, s, stringX, stringY, textColor, true);
                stringY += 10 * step;
            }
            compiledLines.clear();
        }
    }

    public static final class Builder {
        private Builder() {
        }

        private Builder setDefault() {
            return this;
        }

        public InfoDisplayRenderer build() {
            return new InfoDisplayRenderer(InfoDisplayCompiler.Builder.begin().build());
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

