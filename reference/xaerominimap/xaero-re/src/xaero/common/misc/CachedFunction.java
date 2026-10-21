/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.misc;

import java.util.HashMap;
import java.util.function.Function;

public class CachedFunction<F, T> {
    private final HashMap<F, T> cache = new HashMap();
    private F prevFrom;
    private T prevTo;
    private Function<F, T> function;

    public CachedFunction(Function<F, T> function) {
        this.function = function;
    }

    public T apply(F from) {
        if (this.prevFrom == from && from != null) {
            return this.prevTo;
        }
        T cached = this.cache.get(from);
        if (cached == null) {
            cached = this.function.apply(from);
            this.cache.put(from, cached);
        }
        this.prevFrom = from;
        this.prevTo = cached;
        return cached;
    }

    public void clear() {
        this.cache.clear();
    }
}

