/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.cache.id.armor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.nautilus.Nautilus;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import xaero.hud.minimap.radar.icon.cache.id.armor.RadarIconArmor;

public class RadarIconArmorHandler {
    public RadarIconArmor getArmor(LivingEntity livingEntity) {
        EquipmentSlot relevantArmourSlot = livingEntity instanceof AbstractHorse || livingEntity instanceof Nautilus ? EquipmentSlot.BODY : EquipmentSlot.HEAD;
        ItemStack armorItemStack = livingEntity.getItemBySlot(relevantArmourSlot);
        if (armorItemStack == null || armorItemStack == ItemStack.EMPTY) {
            return null;
        }
        Item armorItem = armorItemStack.getItem();
        if (!armorItemStack.is(ItemTags.TRIMMABLE_ARMOR)) {
            return new RadarIconArmor(armorItem, null, null);
        }
        if (!armorItemStack.has(DataComponents.TRIM)) {
            return new RadarIconArmor(armorItem, null, null);
        }
        ArmorTrim trim = (ArmorTrim)armorItemStack.get(DataComponents.TRIM);
        TrimMaterial trimMaterial = (TrimMaterial)trim.material().value();
        TrimPattern trimPattern = (TrimPattern)trim.pattern().value();
        return new RadarIconArmor(armorItem, trimMaterial, trimPattern);
    }
}

