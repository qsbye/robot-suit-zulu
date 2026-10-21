/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet;

import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import xaero.lib.common.packet.PacketHandlerFull;
import xaero.lib.common.packet.payload.PacketPayload;
import xaero.lib.common.packet.type.PacketType;

public abstract class PacketReceiver<C> {
    private final PacketHandlerFull packetHandlerFull;

    public PacketReceiver(PacketHandlerFull packetHandlerFull) {
        this.packetHandlerFull = packetHandlerFull;
    }

    protected <T> void receive(ReentrantBlockableEventLoop<?> executor, PacketPayload<T> payload, C context) {
        PacketType<T> packetType = payload.getPacketType();
        if (packetType == null) {
            return;
        }
        if (!this.isCorrectSide(packetType)) {
            return;
        }
        T packet = payload.getPacket();
        if (executor.isSameThread()) {
            this.getTask(packetType, packet, context).run();
            return;
        }
        executor.execute(this.getTask(packetType, packet, context));
    }

    protected abstract <T> boolean isCorrectSide(PacketType<T> var1);

    protected abstract <T> Runnable getTask(PacketType<T> var1, T var2, C var3);
}

