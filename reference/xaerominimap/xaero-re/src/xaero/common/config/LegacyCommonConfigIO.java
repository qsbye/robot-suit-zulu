/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.config;

import com.google.common.collect.Sets;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import xaero.common.HudMod;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.common.config.primary.option.MinimapPrimaryCommonConfigOptions;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.profile.ConfigProfile;
import xaero.lib.common.util.IOUtils;

public class LegacyCommonConfigIO {
    private final Path configFilePath;
    private boolean allowCaveModeOnServer;
    private boolean allowNetherCaveModeOnServer;
    private boolean shouldEnableEveryoneTracksEveryone;

    public LegacyCommonConfigIO(Path configFilePath) {
        this.configFilePath = configFilePath;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void load() {
        ConfigProfile defaultEnforcedProfile = HudMod.INSTANCE.getHudConfigs().getServerConfigManager().getDefaultEnforcedProfile();
        try (BufferedInputStream bufferedOutput = new BufferedInputStream(new FileInputStream(this.configFilePath.toFile()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(bufferedOutput));){
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    this.readLine(line.split(":"));
                }
                if (this.allowCaveModeOnServer && this.allowNetherCaveModeOnServer) {
                    return;
                }
                if (!this.allowCaveModeOnServer && !this.allowNetherCaveModeOnServer) {
                    defaultEnforcedProfile.set((ConfigOption)MinimapProfiledConfigOptions.CAVE_MODE_ALLOWED, (Object)false);
                    return;
                }
                if (this.allowCaveModeOnServer) {
                    defaultEnforcedProfile.set(MinimapProfiledConfigOptions.CAVE_MODE_ALLOWED_DIMENSIONS, (Object)Sets.newHashSet((Object[])new Identifier[]{Level.OVERWORLD.identifier(), Level.END.identifier()}));
                    return;
                }
                defaultEnforcedProfile.set(MinimapProfiledConfigOptions.CAVE_MODE_ALLOWED_DIMENSIONS, (Object)Sets.newHashSet((Object[])new Identifier[]{Level.NETHER.identifier()}));
                return;
            }
            finally {
                HudMod.INSTANCE.getHudConfigs().getPrimaryCommonConfigManagerIO().save();
                HudMod.INSTANCE.getHudConfigs().getServerConfigProfileIO().save(defaultEnforcedProfile);
                reader.close();
                IOUtils.tryQuickFileBackupMove((Path)this.configFilePath, (int)10);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean readLine(String[] args) {
        if (args[0].equals("allowCaveModeOnServer")) {
            this.allowCaveModeOnServer = args[1].equals("true");
            return true;
        }
        if (args[0].equals("allowNetherCaveModeOnServer")) {
            this.allowNetherCaveModeOnServer = args[1].equals("true");
            return true;
        }
        if (args[0].equals("allowRadarOnServer")) {
            if (!args[1].equals("true")) {
                HudMod.INSTANCE.getHudConfigs().getServerConfigManager().getDefaultEnforcedProfile().set((ConfigOption)MinimapProfiledConfigOptions.DISPLAY_RADAR, (Object)false);
            }
            return true;
        }
        if (args[0].equals("registerStatusEffects")) {
            HudMod.INSTANCE.getHudConfigs().getPrimaryCommonConfigManager().getConfig().set((ConfigOption)MinimapPrimaryCommonConfigOptions.REGISTER_EFFECTS, (Object)args[1].equals("true"));
            return true;
        }
        if (args[0].equals("everyoneTracksEveryone") && args[1].equals("true")) {
            this.shouldEnableEveryoneTracksEveryone = true;
            return true;
        }
        return false;
    }

    public boolean shouldEnableEveryoneTracksEveryone() {
        return this.shouldEnableEveryoneTracksEveryone;
    }
}

