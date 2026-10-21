/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.minimap.highlight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import xaero.common.minimap.highlight.AbstractHighlighter;

public class HighlighterRegistry {
    private List<AbstractHighlighter> highlighters = new ArrayList<AbstractHighlighter>();

    public void register(AbstractHighlighter highlighter) {
        this.highlighters.add(highlighter);
    }

    public void end() {
        this.highlighters = Collections.unmodifiableList(this.highlighters);
    }

    public List<AbstractHighlighter> getHighlighters() {
        return this.highlighters;
    }
}

