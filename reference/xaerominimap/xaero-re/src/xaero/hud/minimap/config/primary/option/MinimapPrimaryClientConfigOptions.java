/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.config.primary.option;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import xaero.lib.common.config.option.BooleanConfigOption;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.option.value.type.BuiltInConfigValueTypes;
import xaero.lib.common.config.util.ConfigUtils;

public class MinimapPrimaryClientConfigOptions {
    private static final List<ConfigOption<?>> ALL = new ArrayList();
    public static final BooleanConfigOption UPDATE_NOTIFICATIONS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("update_notifications")).setDefaultValue((Object)true)).setDisplayName((Component)Component.translatable((String)"gui.xaero_update_notification"))).build(ALL);
    public static final ConfigOption<Integer> IGNORED_UPDATE = ((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)((ConfigOption.FinalBuilder)ConfigOption.FinalBuilder.begin().setId("ignored_update")).setDefaultValue((Object)0)).setValueType(BuiltInConfigValueTypes.INTEGER)).setDisplayGetter(ConfigUtils::getDisplayForSimpleNumber)).build(ALL);
    public static final BooleanConfigOption WRONG_WORLD_TELEPORT = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("allow_wrong_world_teleport")).setDefaultValue((Object)false)).build(ALL);
    public static final BooleanConfigOption DIFFERENTIATE_BY_SERVER_ADDRESS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("differentiate_by_server_address")).setDefaultValue((Object)true)).build(ALL);
    public static final BooleanConfigOption DEBUG_ENTITY_ICONS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("debug_entity_icons")).setDefaultValue((Object)false)).build(ALL);
    public static final BooleanConfigOption DEBUG_ENTITY_VARIANT_IDS = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("debug_entity_variant_ids")).setDefaultValue((Object)false)).build(ALL);
    public static final BooleanConfigOption WAYPOINT_MUTUAL_EDIT = ((BooleanConfigOption.Builder)((BooleanConfigOption.Builder)BooleanConfigOption.Builder.begin().setId("waypoint_mutual_edit")).setDefaultValue((Object)true)).build(ALL);

    public static void registerAll(ConfigOptionManager manager) {
        for (ConfigOption<?> option : ALL) {
            manager.register(option);
        }
    }
}

