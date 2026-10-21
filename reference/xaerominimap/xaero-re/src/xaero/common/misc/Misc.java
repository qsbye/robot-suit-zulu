/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.LevelChunk;

public class Misc {
    public static double getMouseX(Minecraft mc, boolean raw) {
        if (raw) {
            return mc.mouseHandler.xpos();
        }
        return mc.mouseHandler.xpos() * (double)mc.getWindow().getWidth() / (double)mc.getWindow().getScreenWidth();
    }

    public static double getMouseY(Minecraft mc, boolean raw) {
        if (raw) {
            return mc.mouseHandler.ypos();
        }
        return mc.mouseHandler.ypos() * (double)mc.getWindow().getHeight() / (double)mc.getWindow().getScreenHeight();
    }

    public static long getChunkPosAsLong(LevelChunk chunk) {
        return chunk.getPos().pack();
    }

    public static boolean hasItem(Player player, Item item) {
        int i;
        Inventory inventory = player.getInventory();
        for (i = 0; i < 9; ++i) {
            if (inventory.getItem(i).getItem() != item) continue;
            return true;
        }
        for (i = 36; i < inventory.getContainerSize(); ++i) {
            if (inventory.getItem(i).getItem() != item) continue;
            return true;
        }
        return false;
    }

    public static boolean hasItem(NonNullList<ItemStack> inventory, int limit, Item item) {
        for (int i = 0; i < inventory.size() && (limit == -1 || i < limit); ++i) {
            if (inventory.get(i) == null || ((ItemStack)inventory.get(i)).getItem() != item) continue;
            return true;
        }
        return false;
    }

    public static Component getFixedDisplayName(Entity e) {
        Component baseName = e.getName();
        if (baseName == null) {
            return null;
        }
        return e.getTeam() == null ? baseName.copy() : e.getTeam().getFormattedName((Component)baseName.copy());
    }

    public static boolean hasEffect(Player player, Holder<MobEffect> effect) {
        return effect != null && player != null && player.hasEffect(effect);
    }

    public static boolean hasEffect(Holder<MobEffect> effect) {
        return Misc.hasEffect((Player)Minecraft.getInstance().player, effect);
    }

    public static boolean isValidResourceLocationString(String resourceLocationString) {
        if (resourceLocationString.isEmpty()) {
            return false;
        }
        for (int i = 0; i < resourceLocationString.length(); ++i) {
            if (Identifier.isAllowedInIdentifier((char)resourceLocationString.charAt(i))) continue;
            return false;
        }
        return true;
    }
}

