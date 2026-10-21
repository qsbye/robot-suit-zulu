/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.reflection.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import xaero.lib.XaeroLib;
import xaero.lib.common.reflection.util.IObfuscatedReflection;
import xaero.lib.platform.Services;

public class ReflectionUtils {
    public static Class<?> getClassForName(String officialName) throws ClassNotFoundException {
        IObfuscatedReflection obfuscatedReflection = Services.PLATFORM.getObfuscatedReflection();
        return obfuscatedReflection.getClassForName(officialName);
    }

    public static Field getFieldReflection(Class<?> c, String officialName, String descriptor) {
        IObfuscatedReflection obfuscatedReflection = Services.PLATFORM.getObfuscatedReflection();
        return obfuscatedReflection.getFieldReflection(c, officialName, descriptor);
    }

    public static <A, B> B getReflectFieldValue(A parentObject, Field field) {
        boolean accessibleBU = field.isAccessible();
        field.setAccessible(true);
        Object result = null;
        try {
            result = field.get(parentObject);
        }
        catch (Exception e) {
            XaeroLib.LOGGER.error("suppressed exception", (Throwable)e);
        }
        field.setAccessible(accessibleBU);
        return (B)result;
    }

    public static <A, B> void setReflectFieldValue(A parentObject, Field field, B value) {
        boolean accessibleBU = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(parentObject, value);
        }
        catch (Exception e) {
            XaeroLib.LOGGER.error("suppressed exception", (Throwable)e);
        }
        field.setAccessible(accessibleBU);
    }

    public static Method getMethodReflection(Class<?> c, String officialName, String descriptor, Class<?> ... parameters) {
        IObfuscatedReflection obfuscatedReflection = Services.PLATFORM.getObfuscatedReflection();
        return obfuscatedReflection.getMethodReflection(c, officialName, descriptor, parameters);
    }

    public static <A, B> B getReflectMethodValue(A parentObject, Method method, Object ... arguments) {
        boolean accessibleBU = method.isAccessible();
        method.setAccessible(true);
        Object result = null;
        try {
            result = method.invoke(parentObject, arguments);
        }
        catch (Exception e) {
            XaeroLib.LOGGER.error("suppressed exception", (Throwable)e);
        }
        method.setAccessible(accessibleBU);
        return (B)result;
    }
}

