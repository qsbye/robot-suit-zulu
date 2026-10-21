/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.info.render.compile;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import xaero.hud.minimap.info.InfoDisplay;
import xaero.hud.minimap.module.MinimapSession;

public final class InfoDisplayCompiler {
    private boolean compiling;
    private int size;
    private final List<Component> compiledLines;

    private InfoDisplayCompiler(List<Component> compiledLines) {
        this.compiledLines = compiledLines;
    }

    public <T> List<Component> compile(InfoDisplay<T> infoDisplay, MinimapSession minimapSession, int size, BlockPos playerPos) {
        if (this.compiling) {
            throw new IllegalStateException();
        }
        this.compiling = true;
        this.size = size;
        this.compiledLines.clear();
        infoDisplay.getCompiler().onCompile(infoDisplay, this, minimapSession, size, playerPos);
        this.compiling = false;
        return this.compiledLines;
    }

    public void addWords(String text) {
        if (!this.compiling) {
            throw new IllegalStateException();
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.font.width(text) <= this.size) {
            this.compiledLines.add((Component)Component.literal((String)text));
        } else {
            String[] words = text.split(" ");
            StringBuilder lineBuilder = new StringBuilder();
            for (int i = 0; i < words.length; ++i) {
                int lineWidth;
                int wordStart = lineBuilder.length();
                if (i > 0) {
                    lineBuilder.append(' ');
                }
                lineBuilder.append(words[i]);
                if (i == 0 || (lineWidth = mc.font.width(lineBuilder.toString())) <= this.size) continue;
                lineBuilder.delete(wordStart, lineBuilder.length());
                this.compiledLines.add((Component)Component.literal((String)lineBuilder.toString()));
                lineBuilder.delete(0, lineBuilder.length());
                lineBuilder.append(words[i]);
            }
            this.compiledLines.add((Component)Component.literal((String)lineBuilder.toString()));
        }
    }

    public void addLine(Component line) {
        if (!this.compiling) {
            throw new IllegalStateException();
        }
        this.compiledLines.add(line);
    }

    public void addLine(String line) {
        this.addLine((Component)Component.literal((String)line));
    }

    public static final class Builder {
        private Builder() {
        }

        private Builder setDefault() {
            return this;
        }

        public InfoDisplayCompiler build() {
            return new InfoDisplayCompiler(new ArrayList<Component>());
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

