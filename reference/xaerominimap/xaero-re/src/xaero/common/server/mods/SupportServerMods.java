/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.server.mods;

import xaero.common.IXaeroMinimap;
import xaero.common.server.mods.SupportWorldMapServer;
import xaero.common.server.mods.argonauts.SupportArgonautsServer;
import xaero.common.server.mods.ftbteams.SupportFTBTeamsServer;
import xaero.common.server.mods.opac.SupportOPACServer;

public class SupportServerMods {
    private SupportFTBTeamsServer ftbTeams;
    private SupportArgonautsServer argonauts;
    private SupportOPACServer opac;
    private SupportWorldMapServer worldmap;

    public void check(IXaeroMinimap modMain) {
        try {
            Class.forName("dev.ftb.mods.ftbteams.api.FTBTeamsAPI");
            this.ftbTeams = new SupportFTBTeamsServer();
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        try {
            Class.forName("earth.terrarium.argonauts.api.ApiHelper");
            this.argonauts = new SupportArgonautsServer();
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        try {
            Class.forName("xaero.pac.common.server.api.OpenPACServerAPI");
            this.opac = new SupportOPACServer();
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        try {
            Class.forName("xaero.map.WorldMap");
            this.worldmap = new SupportWorldMapServer();
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }

    public boolean hasFtbTeams() {
        return this.ftbTeams != null;
    }

    public SupportFTBTeamsServer getFtbTeams() {
        return this.ftbTeams;
    }

    public boolean hasArgonauts() {
        return this.argonauts != null;
    }

    public SupportArgonautsServer getArgonauts() {
        return this.argonauts;
    }

    public boolean hasOpac() {
        return this.opac != null;
    }

    public SupportOPACServer getOpac() {
        return this.opac;
    }

    public boolean hasWorldmap() {
        return this.worldmap != null;
    }

    public SupportWorldMapServer getWorldmap() {
        return this.worldmap;
    }

    public void registerEvents() {
    }
}

