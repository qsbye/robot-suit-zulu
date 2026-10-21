/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;

public class TextSplitter {
    public static int splitTextIntoLines(List<Component> dest, int minWidth, int widthLimit, FormattedText formattedText, StringBuilder plainTextBuilder) {
        return TextSplitter.splitTextIntoLines(dest, minWidth, widthLimit, formattedText, plainTextBuilder, 0);
    }

    public static int splitTextIntoLines(List<Component> dest, int minWidth, int widthLimit, FormattedText formattedText, StringBuilder plainTextBuilder, int maxLines) {
        SplitProgress progress = new SplitProgress();
        int spaceWidth = Minecraft.getInstance().font.width(" ");
        progress.resultWidth = minWidth;
        FormattedText.StyledContentConsumer consumer = (style, text) -> {
            boolean endsWithSpace;
            boolean isEnd;
            if (maxLines > 0 && dest.size() == maxLines) {
                return Optional.empty();
            }
            boolean bl = isEnd = style == null;
            if (!isEnd && plainTextBuilder != null) {
                plainTextBuilder.append(text);
            }
            if (endsWithSpace = ((String)text).endsWith(" ")) {
                text = (String)text + ".";
            }
            String[] parts = ((String)text).split(" ");
            for (int i = 0; i < parts.length; ++i) {
                boolean isNewLine;
                boolean canAddMultiword = isEnd || i < parts.length - 1;
                String part = isEnd || endsWithSpace && i == parts.length - 1 ? "" : parts[i];
                int partWidth = Minecraft.getInstance().font.width(part);
                if (!canAddMultiword) {
                    progress.buildMultiword(part, partWidth, style);
                    continue;
                }
                int wordWidth = partWidth + progress.multiwordWidth;
                int wordTakesWidth = wordWidth + (!progress.firstWord ? spaceWidth : 0);
                if (progress.lineWidth + wordTakesWidth <= widthLimit) {
                    progress.resultWidth = Math.max(progress.resultWidth, Math.min(widthLimit, progress.lineWidth + wordTakesWidth));
                }
                if (progress.firstWord && progress.lineWidth + wordTakesWidth > progress.resultWidth) {
                    progress.resultWidth = progress.lineWidth + wordTakesWidth;
                }
                boolean bl2 = isNewLine = progress.multiword == null && part.equals("\n");
                if (!isNewLine && progress.lineWidth + wordTakesWidth <= progress.resultWidth) {
                    progress.confirmWord(part, style, wordTakesWidth);
                    continue;
                }
                progress.confirmComponent();
                dest.add(progress.line);
                progress.nextLine();
                if (isNewLine) continue;
                --i;
            }
            return Optional.empty();
        };
        formattedText.visit(consumer, Style.EMPTY.withColor(ChatFormatting.WHITE));
        if (maxLines > 0 && dest.size() == maxLines) {
            return progress.resultWidth;
        }
        if (progress.multiword != null) {
            consumer.accept(null, "end");
        } else if (progress.stringBuilder.length() > 0) {
            progress.confirmComponent();
        }
        if (progress.line != null) {
            dest.add(progress.line);
        }
        if (progress.resultWidth > minWidth) {
            --progress.resultWidth;
        }
        return progress.resultWidth;
    }

    public static class SplitProgress {
        int multiwordWidth;
        List<MutableComponent> multiword = null;
        boolean firstWord = true;
        Component line = null;
        StringBuilder stringBuilder = new StringBuilder();
        int lineWidth;
        Style lastStyle;
        int resultWidth;

        public void buildMultiword(String wordPart, int width, Style style) {
            MutableComponent wordPartComponent = Component.literal((String)wordPart).withStyle(style);
            if (this.multiword == null) {
                this.multiword = new ArrayList<MutableComponent>();
            }
            this.multiword.add(wordPartComponent);
            this.multiwordWidth += width;
        }

        private void confirmWordPart(String part, Style style) {
            if (this.lastStyle != null && !Objects.equals(style, this.lastStyle)) {
                this.confirmComponent();
            }
            this.stringBuilder.append(part);
            this.lastStyle = style;
        }

        public void confirmWord(String lastPart, Style lastPartStyle, int width) {
            if (!this.firstWord) {
                this.stringBuilder.append(" ");
            }
            if (this.multiword != null) {
                for (Component component : this.multiword) {
                    String text = ((PlainTextContents)component.getContents()).text();
                    Style style = component.getStyle();
                    this.confirmWordPart(text, style);
                }
                this.multiword = null;
                this.multiwordWidth = 0;
            }
            this.confirmWordPart(lastPart, lastPartStyle);
            this.lineWidth += width;
            this.firstWord = false;
        }

        public void confirmComponent() {
            MutableComponent comp = Component.literal((String)this.stringBuilder.toString()).withStyle(this.lastStyle == null ? Style.EMPTY : this.lastStyle);
            if (this.line != null) {
                if (this.stringBuilder.length() > 0) {
                    this.line.getSiblings().add(comp);
                }
            } else {
                this.line = comp;
            }
            this.stringBuilder.delete(0, this.stringBuilder.length());
        }

        public void nextLine() {
            this.firstWord = true;
            this.line = null;
            this.lastStyle = null;
            this.lineWidth = 0;
        }
    }
}

