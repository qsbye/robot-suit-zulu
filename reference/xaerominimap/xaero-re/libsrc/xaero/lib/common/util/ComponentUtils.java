/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.util;

import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class ComponentUtils {
    public static Component[] convertFromStrings(String[] strings) {
        Component[] result = new Component[strings.length];
        for (int i = 0; i < strings.length; ++i) {
            result[i] = Component.translatable((String)strings[i]);
        }
        return result;
    }

    public static Component shortenComponent(Font font, Component component, int width) {
        if (font.width((FormattedText)component) <= width) {
            return component;
        }
        ShorteningProgress progress = new ShorteningProgress();
        component.visit((style, text) -> {
            int pieceWidth;
            MutableComponent piece;
            if (progress.widthReached) {
                return Optional.empty();
            }
            while (true) {
                piece = Component.literal((String)text).withStyle(style);
                pieceWidth = font.width((FormattedText)piece);
                if (text.isEmpty()) break;
                int wouldBeWidth = progress.widthSoFar + pieceWidth;
                if (wouldBeWidth >= width) {
                    progress.widthReached = true;
                }
                if (wouldBeWidth <= width) break;
                text = text.substring(0, text.length() - 1);
            }
            progress.shortenedComponent.getSiblings().add(piece);
            progress.widthSoFar += pieceWidth;
            return Optional.empty();
        }, Style.EMPTY.withColor(ChatFormatting.WHITE));
        return progress.shortenedComponent;
    }

    private static class ShorteningProgress {
        private MutableComponent shortenedComponent = Component.literal((String)"");
        private int widthSoFar = 0;
        private boolean widthReached;

        private ShorteningProgress() {
        }
    }
}

