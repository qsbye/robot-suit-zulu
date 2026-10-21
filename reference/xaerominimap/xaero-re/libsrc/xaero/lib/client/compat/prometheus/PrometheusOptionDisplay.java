/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.client.compat.prometheus;

import com.teamresourceful.resourcefullib.client.components.selection.ListEntry;
import com.teamresourceful.resourcefullib.client.components.selection.SelectionList;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.prometheus.api.roles.client.OptionDisplay;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.client.screens.roles.options.entries.NumberBoxListEntry;
import earth.terrarium.prometheus.client.screens.roles.options.entries.TextBoxListEntry;
import earth.terrarium.prometheus.client.screens.roles.options.entries.TextListEntry;
import earth.terrarium.prometheus.client.screens.roles.options.entries.TriStateListEntry;
import earth.terrarium.prometheus.common.handlers.role.Role;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import net.minecraft.network.chat.Component;
import xaero.lib.client.compat.prometheus.FixedNumberBoxListEntry;
import xaero.lib.common.compat.prometheus.ModPrometheus;
import xaero.lib.common.compat.prometheus.PrometheusOptions;
import xaero.lib.common.permission.PermissionNode;
import xaero.lib.common.permission.PermissionRegistry;
import xaero.lib.common.util.JsonUtils;

public class PrometheusOptionDisplay
implements OptionDisplay {
    private final ModPrometheus mod;
    private final List<ListEntry> entries;
    private final Map<PermissionNode<?>, ListEntry> mappedEntries;

    private PrometheusOptionDisplay(ModPrometheus mod, List<ListEntry> entries, Map<PermissionNode<?>, ListEntry> mappedEntries) {
        this.mod = mod;
        this.entries = entries;
        this.mappedEntries = mappedEntries;
    }

    public static PrometheusOptionDisplay create(ModPrometheus mod, Role role, SelectionList<ListEntry> ignored) {
        ArrayList<ListEntry> entries = new ArrayList<ListEntry>();
        HashMap mappedEntries = new HashMap();
        PrometheusOptions options = (PrometheusOptions)role.getOption(mod.getSerializer());
        entries.add((ListEntry)new TextListEntry((Component)Component.literal((String)mod.getModId())));
        entries.add((ListEntry)new TextListEntry((Component)Component.translatable((String)"gui.xaero_prometheus_option_hint")));
        for (PermissionNode<Boolean> permissionNode : PermissionRegistry.INSTANCE.getModNodes(mod.getModId())) {
            TriStateListEntry entry = null;
            if (permissionNode.getType() == Boolean.class) {
                PermissionNode<Boolean> booleanPermissionNode = permissionNode;
                entry = PrometheusOptionDisplay.createBooleanEntry(booleanPermissionNode, options);
            } else if (Number.class.isAssignableFrom(permissionNode.getType())) {
                PermissionNode<Boolean> numberNode = permissionNode;
                entry = PrometheusOptionDisplay.createNumberEntry(numberNode, options);
            } else if (permissionNode.getType() == String.class || permissionNode.getType() == Component.class) {
                String nodeValueString = null;
                if (permissionNode.getType() == String.class) {
                    PermissionNode<Boolean> stringNode = permissionNode;
                    nodeValueString = (String)((Object)options.get(stringNode));
                } else {
                    PermissionNode<Boolean> componentNode = permissionNode;
                    Component componentValue = (Component)options.get(componentNode);
                    if (componentValue != null) {
                        nodeValueString = JsonUtils.toJson(componentValue);
                    }
                }
                entry = new TextBoxListEntry(nodeValueString == null ? "" : nodeValueString, 200, permissionNode.getDisplayName(), permissionNode.getComment(), s -> true);
            }
            if (entry == null) {
                throw new IllegalArgumentException("Unsupported permission node type: " + String.valueOf(permissionNode.getType()));
            }
            entries.add((ListEntry)entry);
            mappedEntries.put(permissionNode, (ListEntry)entry);
        }
        return new PrometheusOptionDisplay(mod, entries, mappedEntries);
    }

    private static TriStateListEntry createBooleanEntry(PermissionNode<Boolean> node, PrometheusOptions options) {
        return new TriStateListEntry(node.getDisplayName(), TriState.of((Boolean)options.get(node)), e -> options.put(node, e.state().isDefined() ? Boolean.valueOf(e.state().isTrue()) : null));
    }

    private static <T extends Number> NumberBoxListEntry createNumberEntry(PermissionNode<T> node, PrometheusOptions options) {
        Number nodeValue = (Number)options.get(node);
        return new FixedNumberBoxListEntry(nodeValue, node.getDisplayName(), node.getComment());
    }

    public List<ListEntry> getDisplayEntries() {
        return this.entries;
    }

    public boolean save(Role role) {
        PrometheusOptions options = PrometheusOptions.Builder.begin().setModId(this.mod.getModId()).build();
        for (PermissionNode<?> node : this.mappedEntries.keySet()) {
            String entryText;
            TextBoxListEntry textBoxListEntry;
            ListEntry entry = this.mappedEntries.get(node);
            if (node.getType() == Boolean.class) {
                PermissionNode<?> booleanPermissionNode = node;
                TriStateListEntry triStateListEntry = (TriStateListEntry)entry;
                TriState state = triStateListEntry.state();
                options.put(booleanPermissionNode, state.isUndefined() ? null : Boolean.valueOf(state.isTrue()));
                continue;
            }
            if (Number.class.isAssignableFrom(node.getType())) {
                PermissionNode<?> numberNode = node;
                this.saveNumberEntry(numberNode, entry, options);
                continue;
            }
            if (node.getType() == String.class) {
                textBoxListEntry = (TextBoxListEntry)entry;
                PermissionNode<?> stringNode = node;
                entryText = textBoxListEntry.getText();
                options.put(stringNode, entryText.isEmpty() ? null : entryText);
                continue;
            }
            if (node.getType() == Component.class) {
                textBoxListEntry = (TextBoxListEntry)entry;
                PermissionNode<?> componentNode = node;
                entryText = textBoxListEntry.getText();
                options.put(componentNode, entryText.isEmpty() ? null : JsonUtils.fromJson(entryText));
                continue;
            }
            throw new IllegalArgumentException("Unsupported permission node type: " + String.valueOf(node.getType()));
        }
        role.setData((RoleOption)options);
        return true;
    }

    private <T extends Number> void saveNumberEntry(PermissionNode<T> numberNode, ListEntry entry, PrometheusOptions options) {
        FixedNumberBoxListEntry numberEntry = (FixedNumberBoxListEntry)entry;
        Integer entryValue = null;
        if (numberNode.getType() == Integer.class) {
            OptionalInt optionalInt = numberEntry.getIntValue();
            entryValue = optionalInt.isEmpty() ? null : Integer.valueOf(optionalInt.getAsInt());
        } else if (numberNode.getType() == Long.class) {
            OptionalLong optionalLong = numberEntry.getLongValue();
            entryValue = optionalLong.isEmpty() ? null : Long.valueOf(optionalLong.getAsLong());
        } else if (numberNode.getType() == Double.class) {
            OptionalDouble optionalDouble = numberEntry.getDoubleValue();
            entryValue = optionalDouble.isEmpty() ? null : Double.valueOf(optionalDouble.getAsDouble());
        } else if (numberNode.getType() == Byte.class) {
            OptionalInt optionalByte = numberEntry.getByteValue();
            entryValue = optionalByte.isEmpty() ? null : Byte.valueOf((byte)optionalByte.getAsInt());
        } else {
            throw new IllegalArgumentException("Unsupported permission node type: " + String.valueOf(numberNode.getType()));
        }
        Integer entryValueCast = entryValue;
        options.put(numberNode, entryValueCast);
    }
}

