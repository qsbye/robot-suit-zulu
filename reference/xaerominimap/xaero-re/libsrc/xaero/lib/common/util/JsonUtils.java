/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.LenientJsonParser;

public class JsonUtils {
    private static final HolderLookup.Provider EMPTY_REGISTRY_LOOKUP_PROVIDER = new HolderLookup.Provider(){

        public Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys() {
            return Stream.empty();
        }

        public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> resourceKey) {
            return Optional.empty();
        }
    };

    public static String toJson(Component component) {
        if (component.getContents() instanceof PlainTextContents) {
            return ((PlainTextContents)component.getContents()).text();
        }
        RegistryOps serializationContext = RegistryAccess.EMPTY.createSerializationContext((DynamicOps)JsonOps.INSTANCE);
        JsonElement jsonElement = ComponentSerialization.CODEC.encodeStart((DynamicOps)serializationContext, (Object)component).result().orElse(null);
        if (jsonElement == null) {
            return null;
        }
        return jsonElement.toString();
    }

    public static Component fromJson(String json) {
        JsonElement jsonElement = LenientJsonParser.parse((String)json);
        RegistryOps serializationContext = RegistryAccess.EMPTY.createSerializationContext((DynamicOps)JsonOps.INSTANCE);
        return ComponentSerialization.CODEC.parse((DynamicOps)serializationContext, (Object)jsonElement).result().orElseGet(() -> Component.literal((String)json));
    }
}

