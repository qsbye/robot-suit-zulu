/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.common.config.info.config;

public final class InfoDisplayConfigData {
    private final Integer backgroundColor;
    private final Integer textColor;
    private final String state;

    public InfoDisplayConfigData(Integer backgroundColor, Integer textColor, String state) {
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.state = state;
    }

    public Integer getBackgroundColor() {
        return this.backgroundColor;
    }

    public Integer getTextColor() {
        return this.textColor;
    }

    public String getState() {
        return this.state;
    }
}

