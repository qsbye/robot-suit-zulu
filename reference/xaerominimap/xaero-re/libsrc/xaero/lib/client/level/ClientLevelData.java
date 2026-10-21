/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.level;

import net.minecraft.client.multiplayer.ClientLevel;
import xaero.lib.client.level.IClientLevel;

public class ClientLevelData {
    private boolean serverHasMod;

    public boolean serverHasMod() {
        return this.serverHasMod;
    }

    public void setServerHasMod() {
        this.serverHasMod = true;
    }

    public static ClientLevelData get(ClientLevel level) {
        ClientLevelData result = ((IClientLevel)level).xaerolib_getData();
        if (result == null) {
            result = new ClientLevelData();
            ((IClientLevel)level).xaerolib_setData(result);
        }
        return result;
    }
}

