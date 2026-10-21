/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.patreon;

import java.io.File;

public class PatreonMod {
    public String fileLayoutID;
    public String latestVersionLayout;
    public String changelogLink;
    public String modName;
    public File modJar;
    public String currentVersion;
    public String latestVersion;
    public String md5;
    public Runnable onVersionIgnore;

    public PatreonMod(String fileLayoutID, String latestVersionLayout, String changelogLink, String modName) {
        this.fileLayoutID = fileLayoutID;
        this.latestVersionLayout = latestVersionLayout;
        this.changelogLink = changelogLink;
        this.modName = modName;
    }
}

