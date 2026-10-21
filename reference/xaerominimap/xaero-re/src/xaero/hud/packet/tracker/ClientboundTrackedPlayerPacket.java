/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.packet.tracker;

import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.common.XaeroMinimapSession;
import xaero.hud.nbt.util.XaeroNbtUtils;

public class ClientboundTrackedPlayerPacket {
    private final boolean remove;
    private final UUID id;
    private final double x;
    private final double y;
    private final double z;
    private final Identifier dimension;
    private final int clientNetworkVersion;

    public ClientboundTrackedPlayerPacket(boolean remove, UUID id, double x, double y, double z, Identifier dimension, int clientNetworkVersion) {
        this.remove = remove;
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.clientNetworkVersion = clientNetworkVersion;
    }

    public void write(FriendlyByteBuf buffer) {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("r", this.remove);
        if (this.clientNetworkVersion < 3) {
            XaeroNbtUtils.putUUIDAsLongArray(nbt, "i", this.id);
        } else {
            XaeroNbtUtils.putUUID(nbt, "i", this.id);
        }
        if (!this.remove) {
            nbt.putDouble("x", this.x);
            nbt.putDouble("y", this.y);
            nbt.putDouble("z", this.z);
            nbt.putString("d", this.dimension.toString());
        }
        buffer.writeNbt((Tag)nbt);
    }

    public static ClientboundTrackedPlayerPacket read(FriendlyByteBuf buffer) {
        CompoundTag nbt = (CompoundTag)buffer.readNbt(NbtAccounter.unlimitedHeap());
        boolean remove = nbt.getBoolean("r").orElse(false);
        UUID id = XaeroNbtUtils.getUUID(nbt, "i").orElse(null);
        double x = remove ? 0.0 : nbt.getDouble("x").orElse(0.0);
        double y = remove ? 0.0 : nbt.getDouble("y").orElse(0.0);
        double z = remove ? 0.0 : nbt.getDouble("z").orElse(0.0);
        String dimensionString = remove ? null : (String)nbt.getString("d").orElse(null);
        Identifier dimension = dimensionString == null ? null : Identifier.parse((String)dimensionString);
        return new ClientboundTrackedPlayerPacket(remove, id, x, y, z, dimension, 3);
    }

    public static class Handler
    implements Consumer<ClientboundTrackedPlayerPacket> {
        @Override
        public void accept(ClientboundTrackedPlayerPacket t) {
            XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
            if (minimapSession == null) {
                return;
            }
            if (t.remove) {
                minimapSession.getMinimapProcessor().getSyncedTrackedPlayerManager().remove(t.id);
                return;
            }
            minimapSession.getMinimapProcessor().getSyncedTrackedPlayerManager().update(t.id, t.x, t.y, t.z, (ResourceKey<Level>)ResourceKey.create((ResourceKey)Registries.DIMENSION, (Identifier)t.dimension));
        }
    }
}

