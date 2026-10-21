/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import xaero.common.HudMod;
import xaero.common.IXaeroMinimap;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.config.primary.option.MinimapPrimaryClientConfigOptions;
import xaero.lib.XaeroLib;
import xaero.lib.client.online.decrypt.DecryptInputStream;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.primary.option.LibPrimaryCommonConfigOptions;
import xaero.lib.patreon.Patreon;

public class Internet {
    public static Cipher cipher = null;

    public static void checkModVersion(IXaeroMinimap modMain) {
        if (!((Boolean)XaeroLib.INSTANCE.getLibConfigChannel().getPrimaryCommonConfigManager().getEffective((ConfigOption)LibPrimaryCommonConfigOptions.ALLOW_INTERNET)).booleanValue()) {
            return;
        }
        String s = modMain.getVersionsURL();
        s = s.replaceAll(" ", "%20");
        try {
            if (cipher == null) {
                throw new Exception("Cipher instance is null!");
            }
            URL url = new URL(s);
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(900);
            conn.setConnectTimeout(900);
            if (conn.getContentLengthLong() > 524288L) {
                throw new IOException("Input too long to trust!");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new DecryptInputStream(conn.getInputStream(), cipher), "UTF8"));
            String line = reader.readLine();
            if (line != null) {
                modMain.setMessage("\u00a7e\u00a7l" + line);
            }
            if ((line = reader.readLine()) != null) {
                modMain.setNewestUpdateID(Integer.parseInt(line));
            }
            modMain.setOutdated(true);
            boolean versionFound = false;
            String[] current = modMain.getVersionID().split("_");
            boolean updateNotificationConfig = (Boolean)HudMod.INSTANCE.getHudConfigs().getPrimaryClientConfigManager().getEffective((ConfigOption)MinimapPrimaryClientConfigOptions.UPDATE_NOTIFICATIONS);
            int ignoredUpdateConfig = (Integer)HudMod.INSTANCE.getHudConfigs().getPrimaryClientConfigManager().getEffective(MinimapPrimaryClientConfigOptions.IGNORED_UPDATE);
            while ((line = reader.readLine()) != null) {
                String[] args;
                if (line.startsWith("data_widget") && line.length() > 11) continue;
                if (!updateNotificationConfig || modMain.getNewestUpdateID() == ignoredUpdateConfig) {
                    modMain.setOutdated(false);
                    break;
                }
                if (line.equals(modMain.getVersionID())) {
                    modMain.setOutdated(false);
                    break;
                }
                if (!Patreon.getHasAutoUpdates()) continue;
                if (versionFound) {
                    if (line.startsWith("meta;")) {
                        String[] metadata = line.substring(5).split(";");
                        modMain.setLatestVersionMD5(metadata[0]);
                    }
                    versionFound = false;
                }
                if (!line.startsWith(current[0] + "_") || (args = line.split("_")).length != current.length) continue;
                boolean sameType = true;
                if (current.length > 2) {
                    for (int i = 2; i < current.length && sameType; ++i) {
                        if (args[i].equals(current[i])) continue;
                        sameType = false;
                    }
                }
                if (!sameType) continue;
                modMain.setLatestVersion(args[1]);
                versionFound = true;
            }
            reader.close();
        }
        catch (IOException ioe) {
            MinimapLogs.LOGGER.warn("io exception while checking versions: {}", (Object)ioe.getMessage());
            modMain.setOutdated(false);
        }
        catch (Throwable e) {
            MinimapLogs.LOGGER.error("suppressed exception", e);
            modMain.setOutdated(false);
        }
    }

    static {
        try {
            cipher = Cipher.getInstance("RSA");
            KeyFactory factory = KeyFactory.getInstance("RSA");
            byte[] byteKey = Base64.getDecoder().decode(Patreon.getPublicKeyString2().getBytes());
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
            PublicKey publicKey = factory.generatePublic(X509publicKey);
            cipher.init(2, publicKey);
        }
        catch (Exception e) {
            cipher = null;
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
    }
}

