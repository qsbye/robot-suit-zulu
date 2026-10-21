/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online.loader;

import java.util.Map;
import xaero.lib.client.gui.widget.online.ScalableWidgetBuilder;
import xaero.lib.client.gui.widget.online.WidgetBuilder;
import xaero.lib.client.gui.widget.online.loader.WidgetLoader;

public abstract class ScalableWidgetLoader
extends WidgetLoader {
    @Override
    protected void commonLoad(WidgetBuilder builder, Map<String, String> parsedArgs) {
        String scale = parsedArgs.get("scale");
        String no_gui_scale = parsedArgs.get("no_gui_scale");
        String scaled_offset_x = parsedArgs.get("scaled_offset_x");
        String scaled_offset_y = parsedArgs.get("scaled_offset_y");
        if (scale != null) {
            ((ScalableWidgetBuilder)builder).setScale(Double.parseDouble(scale));
        }
        if (no_gui_scale != null) {
            ((ScalableWidgetBuilder)builder).setNoGuiScale(no_gui_scale.equals("true"));
        }
        if (scaled_offset_x != null) {
            ((ScalableWidgetBuilder)builder).setScaledOffsetX(Integer.parseInt(scaled_offset_x));
        }
        if (scaled_offset_y != null) {
            ((ScalableWidgetBuilder)builder).setScaledOffsetY(Integer.parseInt(scaled_offset_y));
        }
        super.commonLoad(builder, parsedArgs);
    }
}

