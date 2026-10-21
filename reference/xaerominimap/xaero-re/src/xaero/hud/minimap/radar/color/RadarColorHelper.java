/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.color;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.TeamColor;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.setting.EntityRadarCategorySettings;
import xaero.hud.minimap.radar.color.RadarColor;
import xaero.hud.minimap.radar.state.RadarList;

public class RadarColorHelper {
    public int getTeamColor(Entity e) {
        Integer teamColor = null;
        PlayerTeam team = e.getTeam();
        if (team != null) {
            teamColor = team.getColor().orElse(TeamColor.WHITE).rgb();
        }
        return teamColor == null ? -1 : teamColor;
    }

    public int getEntityColor(Entity entity, float offY, boolean cave, int heightLimit, int startFadingAt, boolean heightBasedFade, RadarColor radarColor, RadarColor fallbackColor) {
        float heightFade;
        int color = this.getRadarColorHex(entity, radarColor, fallbackColor);
        float f = heightFade = heightBasedFade ? this.getEntityHeightFade(offY, heightLimit, startFadingAt) : 1.0f;
        if (heightFade >= 1.0f) {
            return color;
        }
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        int alpha = 255;
        if (cave) {
            alpha = (int)((float)alpha * heightFade);
        } else {
            red = (int)((float)red * heightFade);
            green = (int)((float)green * heightFade);
            blue = (int)((float)blue * heightFade);
        }
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private int getRadarColorHex(Entity entity, RadarColor radarColor, RadarColor fallbackColor) {
        if (radarColor != null) {
            return radarColor.getHex();
        }
        int entityTeamColour = this.getTeamColor(entity);
        if (entityTeamColour != -1) {
            return 0xFF000000 | entityTeamColour;
        }
        return fallbackColor.getHex();
    }

    public RadarColor getFallbackColor(RadarList radarList) {
        return this.getFallbackColor(radarList.getClientCategory(), radarList.getSyncedCategory());
    }

    public RadarColor getFallbackColor(EntityRadarCategory category, EntityRadarCategory syncedCategory) {
        int colorSetting;
        EntityRadarCategory superCategory;
        Double categorySettingValue;
        EntityRadarCategory fallbackCategory;
        EntityRadarCategory entityRadarCategory = fallbackCategory = syncedCategory == null ? category : syncedCategory;
        do {
            if ((superCategory = (EntityRadarCategory)fallbackCategory.getSuperCategory()) != null) continue;
            if (fallbackCategory == syncedCategory) {
                superCategory = category;
                continue;
            }
            return RadarColor.WHITE;
        } while ((categorySettingValue = (fallbackCategory = superCategory).getSettingValue(EntityRadarCategorySettings.COLOR)) == null || (colorSetting = categorySettingValue.intValue()) == -1);
        return RadarColor.fromIndex(colorSetting);
    }

    public float getEntityHeightFade(float offY, int heightLimit, int startFadingAt) {
        int threshold;
        float level = (float)heightLimit - offY;
        if (level < 0.0f) {
            level = 0.0f;
        }
        float brightness = 1.0f;
        int n = threshold = startFadingAt == 0 ? heightLimit * 3 / 4 : heightLimit - startFadingAt;
        if (level <= (float)threshold) {
            brightness = 0.25f + 0.5f * level / (float)threshold;
        }
        return brightness;
    }
}

