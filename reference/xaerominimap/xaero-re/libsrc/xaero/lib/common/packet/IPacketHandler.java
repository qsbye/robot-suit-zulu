/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import xaero.lib.common.player.ServerPlayerData;

public interface IPacketHandler {
    public Identifier getChannelId();

    public <P> void register(int var1, Class<P> var2, BiConsumer<P, FriendlyByteBuf> var3, Function<FriendlyByteBuf, P> var4, BiConsumer<P, ServerPlayer> var5, Consumer<P> var6);

    public <T> void sendToServer(T var1);

    public <T> void sendToPlayer(ServerPlayer var1, T var2);

    default public <T> void sendToPlayer(ServerPlayerData playerData, T packet) {
        this.sendToPlayer(playerData.getPlayer(), packet);
    }
}

