/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui.component;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.function.Function;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class CachedComponentSupplier {
    private final Function<Object[], Component> factory;
    private Object[] registeredArgs;
    private Component cachedComponent;
    private WeakReference<Language> registeredLanguage;

    public CachedComponentSupplier(Function<Object[], Component> factory) {
        this.factory = factory;
    }

    public Component get(Object ... args) {
        if (this.cachedComponent == null || !Arrays.equals(this.registeredArgs, args) || this.registeredLanguage.get() != Language.getInstance()) {
            this.registeredLanguage = new WeakReference<Language>(Language.getInstance());
            this.cachedComponent = this.factory.apply(args);
            this.registeredArgs = args;
        }
        return this.cachedComponent;
    }
}

