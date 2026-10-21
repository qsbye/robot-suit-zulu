/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.rule;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ObjectCategoryListRuleType<E, P, S> {
    private final String id;
    private final BiFunction<E, P, S> getter;
    private final Supplier<Iterable<S>> allElementSupplier;
    private final Function<String, List<S>> elementResolver;
    private final Function<S, String> elementSerializer;
    private final Function<String, String> stringFixer;
    private final Predicate<String> stringValidator;

    public ObjectCategoryListRuleType(String id, BiFunction<E, P, S> getter, Supplier<Iterable<S>> allElementSupplier, Function<String, List<S>> elementResolver, Function<S, String> elementSerializer, Function<String, String> stringFixer, Predicate<String> stringValidator, List<ObjectCategoryListRuleType<E, P, ?>> typeList, Map<String, ObjectCategoryListRuleType<E, P, ?>> typeMap) {
        this.id = id;
        this.getter = getter;
        this.allElementSupplier = allElementSupplier;
        this.elementResolver = elementResolver;
        this.elementSerializer = elementSerializer;
        this.stringFixer = stringFixer;
        this.stringValidator = stringValidator;
        typeList.add(this);
        typeMap.put(id, this);
    }

    public String getId() {
        return this.id;
    }

    public BiFunction<E, P, S> getGetter() {
        return this.getter;
    }

    public Supplier<Iterable<S>> getAllElementSupplier() {
        return this.allElementSupplier;
    }

    public Function<String, List<S>> getElementResolver() {
        return this.elementResolver;
    }

    public Function<String, String> getStringFixer() {
        return this.stringFixer;
    }

    public Predicate<String> getStringValidator() {
        return this.stringValidator;
    }

    public Function<S, String> getSerializer() {
        return this.elementSerializer;
    }
}

