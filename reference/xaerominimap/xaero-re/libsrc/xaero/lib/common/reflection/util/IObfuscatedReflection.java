/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.reflection.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface IObfuscatedReflection {
    public Class<?> getClassForName(String var1) throws ClassNotFoundException;

    public Field getFieldReflection(Class<?> var1, String var2, String var3);

    public Method getMethodReflection(Class<?> var1, String var2, String var3, Class<?> ... var4);
}

