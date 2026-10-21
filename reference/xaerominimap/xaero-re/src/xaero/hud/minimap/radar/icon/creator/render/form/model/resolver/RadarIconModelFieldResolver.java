/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.creator.render.form.model.resolver;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.util.SeparatedKeysParser;
import xaero.lib.common.reflection.util.ReflectionUtils;

public class RadarIconModelFieldResolver {
    public static final SeparatedKeysParser KEYS_PARSER = new SeparatedKeysParser(c -> c.charValue() == ',' || c.charValue() == ';');

    public static Object[] handleDeclaredField(Field f, Object currentChainNode, String matchedFilterElement, Object[] oneResultArray) throws IllegalArgumentException {
        Object referencedObject = ReflectionUtils.getReflectFieldValue((Object)currentChainNode, (Field)f);
        if (referencedObject == null) {
            return null;
        }
        FieldReferenceType<?> referenceType = RadarIconModelFieldResolver.getReferenceType(referencedObject);
        Object[] collectionArray = referenceType.getArray(referencedObject, oneResultArray);
        if (collectionArray.length == 0) {
            return collectionArray;
        }
        if (matchedFilterElement == null || !matchedFilterElement.endsWith("]")) {
            return collectionArray;
        }
        int lastStartBracket = matchedFilterElement.lastIndexOf(91);
        if (lastStartBracket == -1) {
            throw new IllegalArgumentException("Field name " + matchedFilterElement + " ends with ] but is missing [!");
        }
        try {
            String keysString = matchedFilterElement.substring(lastStartBracket + 1, matchedFilterElement.length() - 1);
            String[] keys = KEYS_PARSER.parseKeys(keysString);
            Object[] result = keys.length == 1 ? oneResultArray : (Object[])Array.newInstance(oneResultArray.getClass().getComponentType(), keys.length);
            for (int i = 0; i < keys.length; ++i) {
                Object element;
                String keyString = keys[i];
                result[i] = element = referenceType.getElement(referencedObject, collectionArray, keyString);
            }
            return result;
        }
        catch (Exception nfe) {
            throw new IllegalArgumentException("Invalid element index/indices in " + matchedFilterElement + "!", nfe);
        }
    }

    public static void searchSuperclassFields(Object currentChainNode, List<String> filter, Listener listener, Object[] oneResultArray) {
        Class<?> nodeClass = currentChainNode.getClass();
        while (nodeClass != Object.class) {
            Field[] declaredModelFields = nodeClass.getDeclaredFields();
            RadarIconModelFieldResolver.handleFields(currentChainNode, declaredModelFields, filter, listener, oneResultArray);
            if (!listener.shouldStop() && (nodeClass = nodeClass.getSuperclass()) != null) continue;
        }
    }

    public static void handleFields(Object currentChainNode, Field[] declaredModelFields, List<String> filter, Listener listener, Object[] oneResultArray) {
        for (Field f : declaredModelFields) {
            if (!listener.isFieldAllowed(f)) continue;
            try {
                String comparisonName = f.getDeclaringClass().getName() + ";" + f.getName();
                String matchedFilterElement = null;
                if (filter != null && (matchedFilterElement = RadarIconModelFieldResolver.passesFilter(comparisonName, filter)) == null) continue;
                Object[] matchingObjects = RadarIconModelFieldResolver.handleDeclaredField(f, currentChainNode, matchedFilterElement, oneResultArray);
                if (matchingObjects != null) {
                    listener.onFieldResolved(matchingObjects, matchedFilterElement);
                }
                if (!listener.shouldStop()) continue;
                break;
            }
            catch (Exception e) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
        }
    }

    private static String passesFilter(String entry, List<String> filter) {
        for (String f : filter) {
            if (f.equals(entry)) {
                return f;
            }
            int indexOfBracket = f.lastIndexOf(91);
            if (indexOfBracket == -1 || !f.substring(0, indexOfBracket).equals(entry)) continue;
            return f;
        }
        return null;
    }

    private static FieldReferenceType<?> getReferenceType(Object o) {
        if (o instanceof Object[]) {
            return FieldReferenceType.ARRAY;
        }
        if (o instanceof Collection) {
            return FieldReferenceType.COLLECTION;
        }
        if (o instanceof Map) {
            return FieldReferenceType.MAP;
        }
        return FieldReferenceType.SINGLE;
    }

    private static class FieldReferenceType<T> {
        public static FieldReferenceType<Object> SINGLE = new FieldReferenceType<Object>((o, a, k) -> {
            throw new RuntimeException(String.format("%s is not an array/collection!", new Object[0]));
        }, (o, ora) -> {
            ora[0] = o;
            return ora;
        });
        public static FieldReferenceType<Object[]> ARRAY = new FieldReferenceType<Object[]>((o, a, k) -> o[Integer.parseInt(k.trim())], (o, ora) -> o);
        public static FieldReferenceType<Collection<?>> COLLECTION = new FieldReferenceType<Collection>((o, a, k) -> a[Integer.parseInt(k.trim())], (o, ora) -> o.toArray((T[])ora));
        public static FieldReferenceType<Map<?, ?>> MAP = new FieldReferenceType<Map>((o, a, k) -> {
            Object result = o.get(k);
            if (result == null) {
                try {
                    int integerAttemptKey = Integer.parseInt(k.trim());
                    result = o.get(integerAttemptKey);
                }
                catch (NumberFormatException numberFormatException) {
                    // empty catch block
                }
            }
            return result;
        }, (o, ora) -> o.values().toArray((T[])ora));
        private FieldReferenceElementGetter<T> elementGetter;
        private BiFunction<T, Object[], Object[]> arrayGetter;

        private FieldReferenceType(FieldReferenceElementGetter<T> elementGetter, BiFunction<T, Object[], Object[]> arrayGetter) {
            this.elementGetter = elementGetter;
            this.arrayGetter = arrayGetter;
        }

        public Object[] getArray(Object referencedObject, Object[] oneResultArray) {
            return this.arrayGetter.apply(referencedObject, oneResultArray);
        }

        public Object getElement(Object referencedObject, Object[] array, String key) {
            return this.elementGetter.get(referencedObject, array, key);
        }
    }

    public static interface Listener {
        public boolean isFieldAllowed(Field var1);

        public boolean shouldStop();

        public void onFieldResolved(Object[] var1, String var2);
    }

    @FunctionalInterface
    private static interface FieldReferenceElementGetter<T> {
        public Object get(T var1, Object[] var2, String var3);
    }
}

