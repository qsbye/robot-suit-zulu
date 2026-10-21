/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online.loader;

import java.util.Map;
import xaero.lib.client.gui.widget.online.ButtonWidgetBuilder;
import xaero.lib.client.gui.widget.online.Widget;
import xaero.lib.client.gui.widget.online.loader.WidgetLoader;

public class ButtonWidgetLoader
extends WidgetLoader {
    @Override
    public Widget load(Map<String, String> parsedArgs) {
        ButtonWidgetBuilder builder = new ButtonWidgetBuilder();
        this.commonLoad(builder, parsedArgs);
        String button_text = parsedArgs.get("button_text");
        String button_w = parsedArgs.get("button_w");
        String button_h = parsedArgs.get("button_h");
        if (button_text != null) {
            builder.setButtonText(button_text.replace("%semi%", ";"));
        }
        if (button_w != null) {
            builder.setButtonW(Integer.parseInt(button_w));
        }
        if (button_h != null) {
            builder.setButtonH(Integer.parseInt(button_h));
        }
        if (builder.validate()) {
            return builder.build();
        }
        return null;
    }
}

