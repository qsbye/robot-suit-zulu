/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.packet.config;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import xaero.lib.XaeroLib;
import xaero.lib.common.packet.config.AbstractConfigPacket;

public final class ClientboundEnforcedConfigPacket
extends AbstractConfigPacket<ClientboundEnforcedConfigPacket> {
    private final boolean reset;

    public ClientboundEnforcedConfigPacket(List<AbstractConfigPacket.OptionEntry> entries, boolean reset) {
        super(entries);
        this.reset = reset;
    }

    @Override
    protected void writeExtra(CompoundTag tag) {
        tag.putBoolean("r", this.reset);
    }

    public static ClientboundEnforcedConfigPacket read(FriendlyByteBuf buffer) {
        try {
            CompoundTag tag = (CompoundTag)buffer.readNbt(NbtAccounter.unlimitedHeap());
            if (tag == null) {
                return null;
            }
            List<AbstractConfigPacket.OptionEntry> entries = ClientboundEnforcedConfigPacket.readOptions(tag);
            boolean reset = tag.getBooleanOr("r", false);
            return new ClientboundEnforcedConfigPacket(entries, reset);
        }
        catch (Throwable t) {
            XaeroLib.LOGGER.error("packet error", t);
            return null;
        }
    }

    public static final class ClientHandler
    implements Consumer<ClientboundEnforcedConfigPacket> {
        @Override
        public void accept(ClientboundEnforcedConfigPacket packet) {
            try {
                XaeroLib.INSTANCE.getClient().getConfigSynchronizer().onEnforcedConfigPacket(packet.reset, packet.getEntries());
            }
            catch (Throwable t) {
                XaeroLib.LOGGER.error("packet error", t);
            }
        }
    }
}

