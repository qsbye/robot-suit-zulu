/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online.loader;

import java.io.IOException;
import java.util.Map;
import xaero.lib.client.gui.GuiSettings;
import xaero.lib.client.gui.GuiUpdateAll;
import xaero.lib.client.gui.widget.online.ClickAction;
import xaero.lib.client.gui.widget.online.HoverAction;
import xaero.lib.client.gui.widget.online.Widget;
import xaero.lib.client.gui.widget.online.WidgetBuilder;

public abstract class WidgetLoader {
    public abstract Widget load(Map<String, String> var1) throws IOException;

    protected void commonLoad(WidgetBuilder builder, Map<String, String> parsedArgs) {
        String location = parsedArgs.get("location");
        String anchor_hor = parsedArgs.get("anchor_hor");
        String anchor_vert = parsedArgs.get("anchor_vert");
        String on_click = parsedArgs.get("on_click");
        String on_hover = parsedArgs.get("on_hover");
        String x = parsedArgs.get("x");
        String y = parsedArgs.get("y");
        String url = parsedArgs.get("url");
        String tooltip = parsedArgs.get("tooltip");
        String min_widget_level = parsedArgs.remove("min_widget_level");
        String max_widget_level = parsedArgs.remove("max_widget_level");
        if (location != null) {
            if (location.equals("SETTINGS")) {
                builder.setLocation(GuiSettings.class);
            } else if (location.equals("UPDATE")) {
                builder.setLocation(GuiUpdateAll.class);
            }
        }
        if (anchor_hor != null) {
            builder.setHorizontalAnchor(Float.parseFloat(anchor_hor));
        }
        if (anchor_vert != null) {
            builder.setVerticalAnchor(Float.parseFloat(anchor_vert));
        }
        if (on_click != null) {
            builder.setOnClick(ClickAction.valueOf(on_click));
        }
        if (on_hover != null) {
            builder.setOnHover(HoverAction.valueOf(on_hover));
        }
        if (x != null) {
            builder.setX(Integer.parseInt(x));
        }
        if (y != null) {
            builder.setY(Integer.parseInt(y));
        }
        if (url != null) {
            builder.setUrl(url.replace("%semi%", ";"));
        }
        if (tooltip != null) {
            builder.setTooltip(tooltip.replace("%semi%", ";"));
        }
        if (min_widget_level != null) {
            builder.setMinWidgetLevel(Integer.parseInt(min_widget_level));
        }
        if (max_widget_level != null) {
            builder.setMaxWidgetLevel(Integer.parseInt(max_widget_level));
        }
    }
}

