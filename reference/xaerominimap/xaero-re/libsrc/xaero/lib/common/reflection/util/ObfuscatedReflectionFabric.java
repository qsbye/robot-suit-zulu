/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.reflection.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import xaero.lib.common.reflection.util.IObfuscatedReflection;

public class ObfuscatedReflectionFabric
implements IObfuscatedReflection {
    private static String fixFabricFieldMapping(Class<?> clazz, String name, String descriptor) {
        MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
        String owner = mappingResolver.unmapClassName("official", clazz.getName());
        return mappingResolver.mapFieldName("official", owner, name, descriptor);
    }

    private static String fixFabricMethodMapping(Class<?> clazz, String name, String descriptor) {
        MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
        String owner = mappingResolver.unmapClassName("official", clazz.getName());
        return mappingResolver.mapMethodName("official", owner, name, descriptor);
    }

    @Override
    public Class<?> getClassForName(String officialName) throws ClassNotFoundException {
        MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
        String name = mappingResolver.mapClassName("official", officialName);
        return Class.forName(name);
    }

    @Override
    public Field getFieldReflection(Class<?> c, String officialName, String descriptor) {
        String name = ObfuscatedReflectionFabric.fixFabricFieldMapping(c, officialName, descriptor);
        Field field = null;
        try {
            field = c.getDeclaredField(name);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return field;
    }

    @Override
    public Method getMethodReflection(Class<?> c, String officialName, String descriptor, Class<?> ... parameters) {
        String name = ObfuscatedReflectionFabric.fixFabricMethodMapping(c, officialName, descriptor);
        Method method = null;
        try {
            method = c.getDeclaredMethod(name, parameters);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return method;
    }
}

