/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet.type;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class PacketType<P> {
    private final int index;
    private final Class<P> type;
    private final BiConsumer<P, FriendlyByteBuf> encoder;
    private final Function<FriendlyByteBuf, P> decoder;
    private final BiConsumer<P, ServerPlayer> serverHandler;
    private final Consumer<P> clientHandler;

    public PacketType(int index, Class<P> type, BiConsumer<P, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, P> decoder, BiConsumer<P, ServerPlayer> serverHandler, Consumer<P> clientHandler) {
        this.index = index;
        this.type = type;
        this.encoder = encoder;
        this.decoder = decoder;
        this.serverHandler = serverHandler;
        this.clientHandler = clientHandler;
    }

    public int getIndex() {
        return this.index;
    }

    public Class<P> getType() {
        return this.type;
    }

    public BiConsumer<P, FriendlyByteBuf> getEncoder() {
        return this.encoder;
    }

    public Function<FriendlyByteBuf, P> getDecoder() {
        return this.decoder;
    }

    public BiConsumer<P, ServerPlayer> getServerHandler() {
        return this.serverHandler;
    }

    public Consumer<P> getClientHandler() {
        return this.clientHandler;
    }
}

