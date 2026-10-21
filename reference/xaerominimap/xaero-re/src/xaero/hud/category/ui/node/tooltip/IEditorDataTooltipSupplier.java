/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node.tooltip;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import xaero.hud.category.ui.node.EditorNode;
import xaero.lib.client.gui.widget.Tooltip;

public interface IEditorDataTooltipSupplier
extends BiFunction<EditorNode, EditorNode, Supplier<Tooltip>> {
}

