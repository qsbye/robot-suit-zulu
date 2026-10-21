/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget;

import java.util.ArrayList;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import xaero.lib.common.gui.widget.TooltipInfo;
import xaero.lib.common.util.TextSplitter;

public class Tooltip
implements Supplier<Tooltip> {
    private static final int BOX_OFFSET_X = 12;
    private static final int BOX_OFFSET_Y = 10;
    private static final int START_WIDTH = 20;
    private static final int USUAL_WIDTH = 200;
    private ArrayList<Component> strings;
    private Component directText;
    private boolean directTextReady;
    private String language;
    private String fullCode;
    private Style codeStyle;
    private String plainText;
    private int boxWidth = 20;
    private int startWidth = 20;
    private static final int color = -939524096;
    private boolean customLines;
    private boolean flippedByDefault;
    private boolean autoLinebreak;

    public Tooltip(String code) {
        this(code, Style.EMPTY);
    }

    public Tooltip(String code, Style codeStyle) {
        this(code, codeStyle, false);
    }

    public Tooltip(String code, Style codeStyle, boolean flippedByDefault) {
        this.fullCode = code;
        this.codeStyle = codeStyle;
        this.flippedByDefault = flippedByDefault;
        this.autoLinebreak = true;
    }

    public Tooltip(Component directText) {
        this(directText, false);
    }

    public Tooltip(Component directText, boolean flippedByDefault) {
        this.directText = directText;
        this.flippedByDefault = flippedByDefault;
        this.autoLinebreak = true;
    }

    public Tooltip(int size) {
        this.strings = new ArrayList();
        for (int i = 0; i < size; ++i) {
            this.strings.add((Component)Component.literal((String)""));
        }
        this.customLines = true;
    }

    public Tooltip(TooltipInfo info) {
        this(info.text, info.flippedByDefault);
        this.autoLinebreak = info.autoLineBreak;
    }

    public void setStartWidth(int startWidth) {
        this.startWidth = startWidth;
    }

    private String currentLanguage() {
        return Minecraft.getInstance().getLanguageManager().getSelected();
    }

    public void createLines(Component text) {
        try {
            this.language = this.currentLanguage();
        }
        catch (NullPointerException e) {
            this.language = "en_us";
        }
        this.strings = new ArrayList();
        this.splitWords(this.strings, (FormattedText)text);
    }

    public void splitWords(ArrayList<Component> dest, FormattedText formattedText) {
        StringBuilder plainTextBuilder = new StringBuilder();
        this.boxWidth = 20 + TextSplitter.splitTextIntoLines(dest, this.startWidth - 20, (this.autoLinebreak ? 200 : Integer.MAX_VALUE) - 20, formattedText, plainTextBuilder);
        this.plainText = plainTextBuilder.toString().replaceAll("(\u00a7[0-9a-g])+", "");
    }

    public Component getLine(int line) {
        return this.strings.get(line);
    }

    private void ensure() {
        try {
            if (!(this.customLines || (this.fullCode != null || this.directTextReady) && this.language != null && this.language.equals(this.currentLanguage()))) {
                if (this.fullCode != null) {
                    this.createLines((Component)Component.translatable((String)this.fullCode).withStyle(this.codeStyle));
                } else {
                    this.createLines(this.directText);
                    this.directTextReady = true;
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void drawBox(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height) {
        int h;
        int overEdgeY;
        this.ensure();
        int drawX = x + 12;
        int drawY = y + 10;
        int overEdgeX = drawX + this.boxWidth - width;
        if (this.flippedByDefault || overEdgeX > 9) {
            drawX = x - 12 - this.boxWidth;
        } else if (overEdgeX > 0) {
            drawX -= overEdgeX;
        }
        if (drawX < 0) {
            drawX = 0;
        }
        if ((overEdgeY = drawY + (h = 5 + this.strings.size() * 10 + 5) - height) > h / 2) {
            drawY = y - 10 - h;
        } else if (overEdgeY > 0) {
            drawY -= overEdgeY;
        }
        if (drawY < 0) {
            drawY = 0;
        }
        guiGraphics.fill(drawX, drawY, drawX + this.boxWidth, drawY + h, -939524096);
        for (int i = 0; i < this.strings.size(); ++i) {
            Component s = this.getLine(i);
            guiGraphics.text(Minecraft.getInstance().font, s, drawX + 10, drawY + 6 + 10 * i, -1);
        }
    }

    public Tooltip withWidth(int boxWidth) {
        this.boxWidth = boxWidth;
        return this;
    }

    public void setAutoLinebreak(boolean autoLinebreak) {
        this.autoLinebreak = autoLinebreak;
    }

    public String getPlainText() {
        this.ensure();
        return this.plainText;
    }

    public String getFullCode() {
        return this.fullCode;
    }

    @Override
    public Tooltip get() {
        return this;
    }
}

