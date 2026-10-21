/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.util;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import xaero.common.minimap.MinimapProcessor;
import xaero.hud.minimap.MinimapLogs;
import xaero.lib.common.reflection.util.ReflectionUtils;

public class RadarUtils {
    private static EntityDataAccessor<Optional<UUID>> FOX_TRUSTED_UUID_SECONDARY;
    private static EntityDataAccessor<Optional<UUID>> FOX_TRUSTED_UUID_MAIN;

    public static double getMaxDistance(MinimapProcessor minimap, boolean circle) {
        int cullingSize = minimap.getMinimapSize() / 2 + 48;
        if (!circle) {
            cullingSize = (int)((double)cullingSize * Math.sqrt(2.0));
        }
        return (double)(cullingSize * cullingSize) / (minimap.getMinimapZoom() * minimap.getMinimapZoom());
    }

    public static boolean isHostileException(Entity e) {
        if (e instanceof Piglin) {
            return ((Piglin)e).isBaby();
        }
        return false;
    }

    public static boolean isTamed(Entity e, Player p) {
        if (e instanceof AbstractHorse) {
            AbstractHorse horse = (AbstractHorse)e;
            return horse.isTamed();
        }
        if (e instanceof OwnableEntity) {
            OwnableEntity ownable = (OwnableEntity)e;
            EntityReference ownerReference = ownable.getOwnerReference();
            return ownerReference != null && p.getUUID().equals(ownerReference.getUUID());
        }
        if (e instanceof Fox) {
            Fox fox = (Fox)e;
            if (FOX_TRUSTED_UUID_SECONDARY != null && p.getUUID().equals(((Optional)fox.getEntityData().get(FOX_TRUSTED_UUID_SECONDARY)).orElse(null))) {
                return true;
            }
            return FOX_TRUSTED_UUID_MAIN != null && p.getUUID().equals(((Optional)fox.getEntityData().get(FOX_TRUSTED_UUID_MAIN)).orElse(null));
        }
        return false;
    }

    public static boolean isHostile(Entity e) {
        if (Minecraft.getInstance().level.getDifficulty() == Difficulty.PEACEFUL) {
            return false;
        }
        if (RadarUtils.isHostileException(e)) {
            return false;
        }
        return e instanceof Monster || e instanceof Enemy || e.getSoundSource() == SoundSource.HOSTILE;
    }

    public static String getCustomName(Entity e, boolean nullable) {
        Component c = e.getCustomName();
        if (c != null && c.getContents() instanceof PlainTextContents) {
            return ((PlainTextContents)c.getContents()).text();
        }
        return nullable ? null : "{non-plain}";
    }

    static {
        Field foxTrustSecondaryField = null;
        Field foxTrustMainField = null;
        try {
            foxTrustSecondaryField = ReflectionUtils.getFieldReflection(Fox.class, (String)"DATA_TRUSTED_ID_0", (String)"Lnet/minecraft/network/syncher/EntityDataAccessor;");
        }
        catch (Exception e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
        try {
            foxTrustMainField = ReflectionUtils.getFieldReflection(Fox.class, (String)"DATA_TRUSTED_ID_1", (String)"Lnet/minecraft/network/syncher/EntityDataAccessor;");
        }
        catch (Exception e) {
            MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
        }
        if (foxTrustSecondaryField != null) {
            FOX_TRUSTED_UUID_SECONDARY = (EntityDataAccessor)ReflectionUtils.getReflectFieldValue((Object)0, (Field)foxTrustSecondaryField);
        }
        if (foxTrustMainField != null) {
            FOX_TRUSTED_UUID_MAIN = (EntityDataAccessor)ReflectionUtils.getReflectFieldValue((Object)0, (Field)foxTrustMainField);
        }
    }
}

