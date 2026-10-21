/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.category;

public class EntityRadarBackwardsCompatibilityConfig {
    public int entityAmount = 10;
    public boolean showPlayers = true;
    public boolean showHostile = true;
    public boolean showMobs = true;
    public boolean showItems = true;
    public boolean showOther = true;
    public boolean showOtherTeam = true;
    public boolean showTamed = true;
    public int playersColor = 15;
    public int mobsColor = 14;
    public int hostileColor = 14;
    public int itemsColor = 12;
    public int otherColor = 5;
    public int otherTeamColor = -1;
    public int tamedMobsColor = -1;
    public int dotsSize = 2;
    public double headsScale = 1.0;
    public boolean showEntityHeight = true;
    public int playerIcons = 1;
    public int mobIcons = 1;
    public int hostileIcons = 1;
    public int tamedIcons = 3;
    public int heightLimit = 20;
    public int playerNames = 0;
    public int otherTeamsNames = 3;
    public int friendlyMobNames = 0;
    public int hostileMobNames = 0;
    public int itemNames = 0;
    public int otherNames = 0;
    public int tamedMobNames = 3;
    public boolean alwaysEntityNametags;
    public boolean itemFramesOnRadar;
    public boolean displayNameWhenIconFails = true;

    public boolean readSetting(String[] args) {
        if (args[0].equalsIgnoreCase("itemFramesOnRadar")) {
            this.itemFramesOnRadar = args[1].equals("true");
        } else if (args[0].equalsIgnoreCase("entityAmount")) {
            this.entityAmount = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("showPlayers")) {
            this.showPlayers = args[1].equals("true");
        } else if (args[0].equalsIgnoreCase("showHostile")) {
            this.showHostile = args[1].equals("true");
        } else if (args[0].equalsIgnoreCase("showMobs")) {
            this.showMobs = args[1].equals("true");
        } else if (args[0].equalsIgnoreCase("showItems")) {
            this.showItems = args[1].equals("true");
        } else if (args[0].equalsIgnoreCase("showOther")) {
            this.showOther = args[1].equals("true");
        } else if (args[0].equalsIgnoreCase("showOtherTeam")) {
            this.showOtherTeam = args[1].equals("true");
        } else if (args[0].equalsIgnoreCase("showTamed")) {
            this.showTamed = args[1].equals("true");
        } else if (args[0].equalsIgnoreCase("playersColor")) {
            this.playersColor = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("mobsColor")) {
            this.mobsColor = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("hostileColor")) {
            this.hostileColor = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("itemsColor")) {
            this.itemsColor = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("otherColor")) {
            this.otherColor = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("otherTeamColor")) {
            this.otherTeamColor = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("tamedMobsColor")) {
            this.tamedMobsColor = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("dotsScale")) {
            this.dotsSize = Double.valueOf(args[1]) == 1.0 ? 2 : 3;
        } else if (args[0].equalsIgnoreCase("dotsSize")) {
            this.dotsSize = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("headsScale")) {
            this.headsScale = Double.valueOf(args[1]);
        } else if (args[0].equalsIgnoreCase("showEntityHeight")) {
            this.showEntityHeight = args[1].equals("true");
        } else if (args[0].equalsIgnoreCase("playerHeads")) {
            this.playerIcons = args[1].equals("true") ? 2 : (args[1].equals("false") ? 1 : Integer.parseInt(args[1]));
        } else if (args[0].equalsIgnoreCase("playerIcons")) {
            this.playerIcons = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("mobIcons")) {
            this.mobIcons = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("hostileIcons")) {
            this.hostileIcons = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("tamedIcons")) {
            this.tamedIcons = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("heightLimit")) {
            this.heightLimit = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("playerNames")) {
            this.playerNames = args[1].equals("true") ? this.playerIcons : (args[1].equals("false") ? 0 : Integer.parseInt(args[1]));
        } else if (args[0].equalsIgnoreCase("otherTeamsNames")) {
            this.otherTeamsNames = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("friendlyMobNames")) {
            this.friendlyMobNames = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("hostileMobNames")) {
            this.hostileMobNames = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("itemNames")) {
            this.itemNames = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("otherNames")) {
            this.otherNames = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("tamedMobNames")) {
            this.tamedMobNames = Integer.parseInt(args[1]);
        } else if (args[0].equalsIgnoreCase("entityNametags") || args[0].equalsIgnoreCase("alwaysEntityNametags")) {
            this.alwaysEntityNametags = args[1].equals("true");
        } else if (args[0].equalsIgnoreCase("displayNameWhenIconFails")) {
            this.displayNameWhenIconFails = args[1].equals("true");
        } else {
            return false;
        }
        return true;
    }
}

