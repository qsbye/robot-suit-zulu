/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.nbt.util;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;

public class XaeroNbtUtils {
    public static Optional<UUID> getUUID(CompoundTag tag, String id) {
        int[] intArray = tag.getIntArray(id).orElse(null);
        if (intArray == null) {
            long[] longArray = tag.getLongArray(id).orElse(null);
            if (longArray == null || longArray.length != 2) {
                return Optional.empty();
            }
            return Optional.of(new UUID(longArray[0], longArray[1]));
        }
        if (intArray.length != 4) {
            return Optional.empty();
        }
        return Optional.of(UUIDUtil.uuidFromIntArray((int[])intArray));
    }

    public static void putUUID(CompoundTag tag, String id, UUID uuid) {
        tag.putIntArray(id, UUIDUtil.uuidToIntArray((UUID)uuid));
    }

    public static void putUUIDAsLongArray(CompoundTag tag, String id, UUID uuid) {
        tag.putLongArray(id, new long[]{uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()});
    }

    public static IntArrayTag createUUIDTag(UUID partyId) {
        return new IntArrayTag(UUIDUtil.uuidToIntArray((UUID)partyId));
    }
}

