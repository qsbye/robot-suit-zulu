/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.packet.basic;

import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import xaero.common.minimap.mcworld.MinimapClientWorldDataHelper;

public class ClientboundRulesPacket {
    public final boolean allowCaveModeOnServer;
    public final boolean allowNetherCaveModeOnServer;
    public final boolean allowRadarOnServer;

    public ClientboundRulesPacket(boolean allowCaveModeOnServer, boolean allowNetherCaveModeOnServer, boolean allowRadarOnServer) {
        this.allowCaveModeOnServer = allowCaveModeOnServer;
        this.allowNetherCaveModeOnServer = allowNetherCaveModeOnServer;
        this.allowRadarOnServer = allowRadarOnServer;
    }

    public void write(FriendlyByteBuf u) {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("cm", this.allowCaveModeOnServer);
        nbt.putBoolean("ncm", this.allowNetherCaveModeOnServer);
        nbt.putBoolean("r", this.allowRadarOnServer);
        u.writeNbt((Tag)nbt);
    }

    public static ClientboundRulesPacket read(FriendlyByteBuf buffer) {
        CompoundTag nbt = buffer.readNbt();
        return new ClientboundRulesPacket(nbt.getBoolean("cm").orElse(false), nbt.getBoolean("ncm").orElse(false), nbt.getBoolean("r").orElse(false));
    }

    public static class ClientHandler
    implements Consumer<ClientboundRulesPacket> {
        @Override
        public void accept(ClientboundRulesPacket message) {
            MinimapClientWorldDataHelper.getCurrentWorldData().setSyncedRules(message);
        }
    }
}

