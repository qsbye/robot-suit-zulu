/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.node;

import com.google.common.base.Objects;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import xaero.hud.category.ui.entry.EditorListRootEntryFactory;
import xaero.hud.category.ui.node.EditorNode;
import xaero.hud.category.ui.node.tooltip.IEditorDataTooltipSupplier;

public class EditorSimpleWrapperNode<S extends Comparable<S>>
extends EditorNode
implements Comparable<EditorSimpleWrapperNode<S>> {
    private S element;

    protected EditorSimpleWrapperNode(@Nonnull S element, boolean movable, EditorListRootEntryFactory listEntryFactory, IEditorDataTooltipSupplier tooltipSupplier) {
        super(movable, listEntryFactory, tooltipSupplier);
        this.element = element;
    }

    public S getElement() {
        return this.element;
    }

    public void setElement(S element) {
        this.element = element;
    }

    @Override
    public List<EditorNode> getSubNodes() {
        return null;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal((String)this.element.toString());
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof EditorSimpleWrapperNode) {
            EditorSimpleWrapperNode otherWrapper = (EditorSimpleWrapperNode)obj;
            return Objects.equal(this.element, otherWrapper.element);
        }
        return false;
    }

    @Override
    public int compareTo(EditorSimpleWrapperNode<S> o) {
        if (this.element == o.element) {
            return 0;
        }
        if (this.element == null) {
            return -1;
        }
        if (o.element == null) {
            return 1;
        }
        return this.element.compareTo(o.element);
    }

    public static final class FinalBuilder<S extends Comparable<S>>
    extends Builder<S, FinalBuilder<S>> {
        public static <S extends Comparable<S>> FinalBuilder<S> begin() {
            return (FinalBuilder)new FinalBuilder<S>().setDefault();
        }

        @Override
        protected EditorSimpleWrapperNode<S> buildInternally() {
            return new EditorSimpleWrapperNode<Comparable>(this.element, this.movable, this.listEntryFactory, this.tooltipSupplier);
        }
    }

    public static abstract class Builder<S extends Comparable<S>, B extends Builder<S, B>>
    extends EditorNode.Builder<B> {
        protected S element;

        protected Builder() {
        }

        @Override
        public B setDefault() {
            super.setDefault();
            this.setElement(null);
            return (B)((Builder)this.self);
        }

        public B setElement(S element) {
            this.element = element;
            return (B)((Builder)this.self);
        }

        @Override
        public EditorSimpleWrapperNode<S> build() {
            if (this.element == null) {
                throw new IllegalStateException("required fields not set!");
            }
            EditorSimpleWrapperNode result = (EditorSimpleWrapperNode)super.build();
            return result;
        }
    }
}

