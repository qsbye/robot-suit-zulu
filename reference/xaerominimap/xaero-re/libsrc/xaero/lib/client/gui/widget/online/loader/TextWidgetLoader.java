/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online.loader;

import java.io.IOException;
import java.util.Map;
import xaero.lib.client.gui.widget.online.Alignment;
import xaero.lib.client.gui.widget.online.TextWidgetBuilder;
import xaero.lib.client.gui.widget.online.Widget;
import xaero.lib.client.gui.widget.online.loader.ScalableWidgetLoader;

public class TextWidgetLoader
extends ScalableWidgetLoader {
    @Override
    public Widget load(Map<String, String> parsedArgs) throws IOException {
        TextWidgetBuilder builder = new TextWidgetBuilder();
        this.commonLoad(builder, parsedArgs);
        String text = parsedArgs.get("text");
        String alignment = parsedArgs.get("alignment");
        if (text != null) {
            builder.setText(text.replace("%semi%", ";"));
        }
        if (alignment != null) {
            builder.setAlignment(Alignment.valueOf(alignment));
        }
        if (builder.validate()) {
            return builder.build();
        }
        return null;
    }
}

