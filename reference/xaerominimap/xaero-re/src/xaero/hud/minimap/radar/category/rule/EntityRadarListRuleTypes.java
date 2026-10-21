/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.category.rule;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.client.Minecraft;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import xaero.hud.category.rule.ObjectCategoryListRuleType;
import xaero.hud.minimap.radar.category.EntityRadarCategoryConstants;
import xaero.hud.minimap.radar.category.rule.EntityRadarCategoryHardRules;
import xaero.hud.minimap.radar.util.RadarUtils;

public class EntityRadarListRuleTypes {
    public static final List<ObjectCategoryListRuleType<Entity, Player, ?>> TYPE_LIST = EntityRadarCategoryConstants.LIST_FACTORY.get();
    public static final Map<String, ObjectCategoryListRuleType<Entity, Player, ?>> TYPE_MAP = EntityRadarCategoryConstants.MAP_FACTORY.get();
    public static final ObjectCategoryListRuleType<Entity, Player, EntityType<?>> ENTITY_TYPE = new ObjectCategoryListRuleType("entity", (e, p) -> e.getType(), () -> BuiltInRegistries.ENTITY_TYPE, EntityRadarCategoryConstants.getDefaultElementResolver(BuiltInRegistries.ENTITY_TYPE, s -> BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.parse((String)s)).orElse(null), EntityType::getKey), EntityRadarCategoryConstants.DEFAULT_LIST_SERIALIZER, EntityRadarCategoryConstants.DEFAULT_LIST_STRING_VALIDATOR_FIXER, EntityRadarCategoryConstants.DEFAULT_LIST_STRING_VALIDATOR, TYPE_LIST, TYPE_MAP);
    public static final ObjectCategoryListRuleType<Entity, Player, Item> ITEM_TYPE = new ObjectCategoryListRuleType<Entity, Player, Item>("item", (e, p) -> e instanceof ItemEntity ? ((ItemEntity)e).getItem().getItem() : null, () -> BuiltInRegistries.ITEM, EntityRadarCategoryConstants.getDefaultElementResolver(BuiltInRegistries.ITEM, s -> BuiltInRegistries.ITEM.getOptional(Identifier.parse((String)s)).orElse(null), arg_0 -> ((DefaultedRegistry)BuiltInRegistries.ITEM).getKey(arg_0)), item -> BuiltInRegistries.ITEM.getKey(item).toString(), EntityRadarCategoryConstants.DEFAULT_LIST_STRING_VALIDATOR_FIXER, EntityRadarCategoryConstants.DEFAULT_LIST_STRING_VALIDATOR, TYPE_LIST, TYPE_MAP);
    public static final ObjectCategoryListRuleType<Entity, Player, String> PLAYER_NAME = new ObjectCategoryListRuleType<Entity, Player, String>("player", (e, p) -> e instanceof Player ? ((Player)e).getGameProfile().name() : null, () -> Minecraft.getInstance().getConnection() == null ? new ArrayList() : Minecraft.getInstance().getConnection().getOnlinePlayers().stream().map(pi -> pi.getProfile().name())::iterator, xva$0 -> Lists.newArrayList((Object[])new String[]{xva$0}), Function.identity(), EntityRadarCategoryConstants.PLAYER_NAME_VALIDATOR_FIXER, EntityRadarCategoryConstants.PLAYER_NAME_VALIDATOR, TYPE_LIST, TYPE_MAP);
    public static final ObjectCategoryListRuleType<Entity, Player, String> CUSTOM_NAME = new ObjectCategoryListRuleType<Entity, Player, String>("custom-name", (e, p) -> RadarUtils.getCustomName(e, false), () -> {
        if (Minecraft.getInstance().level == null) {
            return Lists.newArrayList((Object[])new String[]{"example"});
        }
        Iterable entities = Minecraft.getInstance().level.entitiesForRendering();
        if (entities == null) {
            return Lists.newArrayList((Object[])new String[]{"example"});
        }
        Stream<String> nameStream = StreamSupport.stream(entities.spliterator(), false).map(e -> RadarUtils.getCustomName(e, true)).filter(Objects::nonNull);
        Iterator iterator = nameStream.iterator();
        if (!iterator.hasNext()) {
            return Lists.newArrayList((Object[])new String[]{"example"});
        }
        return () -> iterator;
    }, xva$0 -> Lists.newArrayList((Object[])new String[]{xva$0}), Function.identity(), s -> s, s -> true, TYPE_LIST, TYPE_MAP);
    public static final ObjectCategoryListRuleType<Entity, Player, Boolean> LIVING = EntityRadarCategoryConstants.createHardRuleBasedPredicateListRuleType(EntityRadarCategoryHardRules.IS_LIVING, TYPE_LIST, TYPE_MAP);
    public static final ObjectCategoryListRuleType<Entity, Player, Boolean> HOSTILE = EntityRadarCategoryConstants.createHardRuleBasedPredicateListRuleType(EntityRadarCategoryHardRules.IS_HOSTILE, TYPE_LIST, TYPE_MAP);
    public static final ObjectCategoryListRuleType<Entity, Player, Boolean> TAMED = EntityRadarCategoryConstants.createHardRuleBasedPredicateListRuleType(EntityRadarCategoryHardRules.IS_TAMED, TYPE_LIST, TYPE_MAP);
    public static final ObjectCategoryListRuleType<Entity, Player, Boolean> SAME_TEAM = EntityRadarCategoryConstants.createHardRuleBasedPredicateListRuleType(EntityRadarCategoryHardRules.IS_SAME_TEAM, TYPE_LIST, TYPE_MAP);
    public static final ObjectCategoryListRuleType<Entity, Player, Boolean> BABY = EntityRadarCategoryConstants.createHardRuleBasedPredicateListRuleType(EntityRadarCategoryHardRules.IS_BABY, TYPE_LIST, TYPE_MAP);
    public static final ObjectCategoryListRuleType<Entity, Player, Boolean> VANILLA = EntityRadarCategoryConstants.createHardRuleBasedPredicateListRuleType(EntityRadarCategoryHardRules.IS_VANILLA, TYPE_LIST, TYPE_MAP);
    public static final ObjectCategoryListRuleType<Entity, Player, Boolean> ABOVE_GROUND = EntityRadarCategoryConstants.createHardRuleBasedPredicateListRuleType(EntityRadarCategoryHardRules.IS_ABOVE_GROUND, TYPE_LIST, TYPE_MAP);
    public static final ObjectCategoryListRuleType<Entity, Player, Boolean> MY_GROUND = EntityRadarCategoryConstants.createHardRuleBasedPredicateListRuleType(EntityRadarCategoryHardRules.IS_MY_GROUND, TYPE_LIST, TYPE_MAP);
    public static final ObjectCategoryListRuleType<Entity, Player, Boolean> LIT = EntityRadarCategoryConstants.createHardRuleBasedPredicateListRuleType(EntityRadarCategoryHardRules.IS_LIT, TYPE_LIST, TYPE_MAP);
    public static final ObjectCategoryListRuleType<Entity, Player, Boolean> HAS_CUSTOM_NAME = EntityRadarCategoryConstants.createHardRuleBasedPredicateListRuleType(EntityRadarCategoryHardRules.HAS_CUSTOM_NAME, TYPE_LIST, TYPE_MAP);
    public static final ObjectCategoryListRuleType<Entity, Player, Boolean> IN_TEAM = EntityRadarCategoryConstants.createHardRuleBasedPredicateListRuleType(EntityRadarCategoryHardRules.IS_IN_TEAM, TYPE_LIST, TYPE_MAP);
    public static final ObjectCategoryListRuleType<Entity, Player, Boolean> TRACKED = EntityRadarCategoryConstants.createHardRuleBasedPredicateListRuleType(EntityRadarCategoryHardRules.IS_TRACKED, TYPE_LIST, TYPE_MAP);
}

