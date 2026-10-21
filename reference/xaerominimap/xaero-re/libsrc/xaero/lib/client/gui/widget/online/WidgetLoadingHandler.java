/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.gui.widget.online;

import java.util.HashMap;
import xaero.lib.XaeroLib;
import xaero.lib.client.gui.widget.online.Widget;
import xaero.lib.client.gui.widget.online.WidgetScreenHandler;
import xaero.lib.client.gui.widget.online.WidgetType;
import xaero.lib.client.gui.widget.online.loader.WidgetLoader;

public class WidgetLoadingHandler {
    private static int CURRENT_VERSION = 2;
    private WidgetScreenHandler handler;

    public WidgetLoadingHandler(WidgetScreenHandler destination) {
        this.handler = destination;
    }

    public void loadWidget(String serialized) {
        Widget widget = null;
        String[] args = serialized.split(";");
        HashMap<String, String> parsedArgs = new HashMap<String, String>();
        for (String arg : args) {
            int splitIndex = arg.indexOf(58);
            if (splitIndex == -1) continue;
            String parameter = arg.substring(0, splitIndex);
            String value = arg.substring(splitIndex + 1);
            parsedArgs.put(parameter, value);
        }
        try {
            String min_version = (String)parsedArgs.remove("min_version");
            String max_version = (String)parsedArgs.remove("max_version");
            if (min_version != null && CURRENT_VERSION < Integer.parseInt(min_version) || max_version != null && CURRENT_VERSION > Integer.parseInt(max_version)) {
                return;
            }
            WidgetType type = WidgetType.valueOf((String)parsedArgs.remove("type"));
            WidgetLoader loader = type.widgetLoader;
            widget = loader.load(parsedArgs);
            this.handler.addWidget(widget);
        }
        catch (Throwable t) {
            XaeroLib.LOGGER.error("suppressed exception", t);
        }
    }
}

