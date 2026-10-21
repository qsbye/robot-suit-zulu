/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.permission;

import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class PermissionNode<T> {
    private final String modId;
    private final Class<T> type;
    private final String defaultPath;
    private final Supplier<String> actualPathSupplier;
    private final Component displayName;
    private final Component comment;

    protected PermissionNode(String modId, Class<T> type, String defaultPath, Supplier<String> actualPathSupplier, Component displayName, Component comment) {
        this.modId = modId;
        this.type = type;
        this.defaultPath = defaultPath;
        this.actualPathSupplier = actualPathSupplier;
        this.displayName = displayName;
        this.comment = comment;
    }

    public Class<T> getType() {
        return this.type;
    }

    public String getModId() {
        return this.modId;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public Component getComment() {
        return this.comment;
    }

    public final String getDefaultPath() {
        return this.defaultPath;
    }

    public final String getActualPath() {
        return this.actualPathSupplier == null ? this.getDefaultPath() : this.actualPathSupplier.get();
    }

    public static final class FinalBuilder<T>
    extends Builder<T, FinalBuilder<T>> {
        private FinalBuilder(Class<T> type) {
            super(type);
        }

        @Override
        protected PermissionNode<T> buildInternally() {
            return new PermissionNode(this.modId, this.type, this.defaultPath, this.actualPathSupplier, this.displayName, this.comment);
        }

        public static <T> FinalBuilder<T> begin(Class<T> type) {
            return (FinalBuilder)new FinalBuilder<T>(type).setDefault();
        }
    }

    public static abstract class Builder<T, B extends Builder<T, B>> {
        protected final B self = this;
        protected final Class<T> type;
        protected String modId;
        protected String defaultPath;
        protected Supplier<String> actualPathSupplier;
        protected Component displayName;
        protected Component comment;

        protected Builder(Class<T> type) {
            this.type = type;
        }

        public B setDefault() {
            this.setModId(null);
            this.setDefaultPath(null);
            this.setActualPathSupplier(null);
            this.setDisplayName(null);
            this.setComment(CommonComponents.EMPTY);
            return this.self;
        }

        public B setModId(String modId) {
            this.modId = modId;
            return this.self;
        }

        public B setDefaultPath(String defaultPath) {
            this.defaultPath = defaultPath;
            return this.self;
        }

        public B setActualPathSupplier(Supplier<String> actualPathSupplier) {
            this.actualPathSupplier = actualPathSupplier;
            return this.self;
        }

        public B setDisplayName(Component displayName) {
            this.displayName = displayName;
            return this.self;
        }

        public B setComment(Component comment) {
            this.comment = comment;
            return this.self;
        }

        public PermissionNode<T> build(Set<PermissionNode<?>> destination) {
            if (this.defaultPath == null || this.displayName == null || this.comment == null || this.modId == null) {
                throw new IllegalStateException();
            }
            PermissionNode<T> result = this.buildInternally();
            if (destination != null) {
                destination.add(result);
            }
            return result;
        }

        protected abstract PermissionNode<T> buildInternally();
    }
}

