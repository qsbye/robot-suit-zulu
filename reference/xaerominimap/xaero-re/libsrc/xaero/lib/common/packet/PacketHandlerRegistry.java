/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.resources.Identifier;
import xaero.lib.common.packet.IPacketHandler;
import xaero.lib.platform.Services;

public final class PacketHandlerRegistry
implements Iterable<IPacketHandler> {
    public static final PacketHandlerRegistry INSTANCE = Builder.begin().build();
    private final Map<Identifier, IPacketHandler> packetHandlers;
    private boolean frozen;

    private PacketHandlerRegistry(Map<Identifier, IPacketHandler> packetHandlers) {
        this.packetHandlers = packetHandlers;
    }

    public synchronized void freeze() {
        this.frozen = true;
    }

    public synchronized IPacketHandler register(Identifier channelId, int protocolVersion, String protocolVersionString) {
        if (this.frozen) {
            throw new IllegalStateException();
        }
        if (this.packetHandlers.containsKey(channelId)) {
            throw new IllegalArgumentException("duplicate packet channel used: " + String.valueOf(channelId));
        }
        IPacketHandler packetHandler = Services.PLATFORM.createPacketHandler(channelId, protocolVersion, protocolVersionString);
        this.packetHandlers.put(channelId, packetHandler);
        return packetHandler;
    }

    @Override
    public Iterator<IPacketHandler> iterator() {
        if (!this.frozen) {
            throw new IllegalStateException();
        }
        return Collections.unmodifiableCollection(this.packetHandlers.values()).iterator();
    }

    private static final class Builder {
        private Builder() {
        }

        public Builder setDefault() {
            return this;
        }

        public PacketHandlerRegistry build() {
            return new PacketHandlerRegistry(new HashMap<Identifier, IPacketHandler>());
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

