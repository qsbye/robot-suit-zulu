/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.graphics.font.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import xaero.lib.client.graphics.XaeroBufferProvider;
import xaero.lib.client.graphics.font.XaeroBufferFontRenderer;

public class FontUtils {
    private static final XaeroBufferFontRenderer xaeroBufferFontRenderer = new XaeroBufferFontRenderer();

    public static void drawText(Font font, PoseStack matrices, String text, float x, float y, int color, boolean shadow, Font.DisplayMode displayMode, XaeroBufferProvider xaeroBufferProvider) {
        Font.PreparedText preparedText = font.prepareText(text, x, y, color, shadow, 0);
        xaeroBufferFontRenderer.renderText(preparedText, matrices, displayMode, 0xF000F0, xaeroBufferProvider);
    }

    public static void drawText(Font font, PoseStack matrices, FormattedCharSequence text, float x, float y, int color, boolean shadow, Font.DisplayMode displayMode, XaeroBufferProvider xaeroBufferProvider) {
        Font.PreparedText preparedText = font.prepareText(text, x, y, color, shadow, false, 0);
        xaeroBufferFontRenderer.renderText(preparedText, matrices, displayMode, 0xF000F0, xaeroBufferProvider);
    }

    public static void drawText(Font font, PoseStack matrices, Component text, float x, float y, int color, boolean shadow, Font.DisplayMode displayMode, XaeroBufferProvider xaeroBufferProvider) {
        FontUtils.drawText(font, matrices, text.getVisualOrderText(), x, y, color, shadow, displayMode, xaeroBufferProvider);
    }

    public static void drawText(PoseStack matrices, String text, float x, float y, int color, boolean shadow, Font.DisplayMode displayMode, XaeroBufferProvider xaeroBufferProvider) {
        FontUtils.drawText(Minecraft.getInstance().font, matrices, text, x, y, color, shadow, displayMode, xaeroBufferProvider);
    }

    public static void drawText(PoseStack matrices, Component text, float x, float y, int color, boolean shadow, Font.DisplayMode displayMode, XaeroBufferProvider xaeroBufferProvider) {
        FontUtils.drawText(Minecraft.getInstance().font, matrices, text, x, y, color, shadow, displayMode, xaeroBufferProvider);
    }

    public static void drawNormalText(PoseStack matrices, String text, float x, float y, int color, boolean shadow, XaeroBufferProvider xaeroBufferProvider) {
        FontUtils.drawText(matrices, text, x, y, color, shadow, Font.DisplayMode.NORMAL, xaeroBufferProvider);
    }

    public static void drawNormalText(PoseStack matrices, Component text, float x, float y, int color, boolean shadow, XaeroBufferProvider xaeroBufferProvider) {
        FontUtils.drawText(matrices, text, x, y, color, shadow, Font.DisplayMode.NORMAL, xaeroBufferProvider);
    }

    public static void drawPiercingText(PoseStack matrices, String text, float x, float y, int color, boolean shadow, XaeroBufferProvider xaeroBufferProvider) {
        FontUtils.drawText(matrices, text, x, y, color, shadow, Font.DisplayMode.SEE_THROUGH, xaeroBufferProvider);
    }

    public static void drawPiercingText(PoseStack matrices, Component text, float x, float y, int color, boolean shadow, XaeroBufferProvider xaeroBufferProvider) {
        FontUtils.drawText(matrices, text, x, y, color, shadow, Font.DisplayMode.SEE_THROUGH, xaeroBufferProvider);
    }

    public static void drawCenteredPiercingText(PoseStack matrices, String text, float x, float y, int color, boolean shadow, XaeroBufferProvider xaeroBufferProvider) {
        FontUtils.drawPiercingText(matrices, text, x - (float)(Minecraft.getInstance().font.width(text) / 2), y, color, shadow, xaeroBufferProvider);
    }

    public static void drawCenteredPiercingText(PoseStack matrices, Component text, float x, float y, int color, boolean shadow, XaeroBufferProvider xaeroBufferProvider) {
        FontUtils.drawPiercingText(matrices, text, x - (float)(Minecraft.getInstance().font.width((FormattedText)text) / 2), y, color, shadow, xaeroBufferProvider);
    }
}

