/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.packet.tracker;

import java.util.function.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import xaero.common.XaeroMinimapSession;

public class ClientboundPlayerTrackerResetPacket {
    public void write(FriendlyByteBuf buffer) {
    }

    public static ClientboundPlayerTrackerResetPacket read(FriendlyByteBuf buffer) {
        return new ClientboundPlayerTrackerResetPacket();
    }

    public static class Handler
    implements Consumer<ClientboundPlayerTrackerResetPacket> {
        @Override
        public void accept(ClientboundPlayerTrackerResetPacket t) {
            XaeroMinimapSession minimapSession = XaeroMinimapSession.getCurrentSession();
            if (minimapSession == null) {
                return;
            }
            minimapSession.getMinimapProcessor().getSyncedTrackedPlayerManager().reset();
        }
    }
}

