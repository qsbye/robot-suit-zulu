/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.option.value.type;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import xaero.lib.common.config.option.value.io.serialization.ConfigValueIOCodec;
import xaero.lib.common.config.option.value.sync.serialization.ConfigValueSyncCodec;
import xaero.lib.common.config.option.value.type.ConfigValueType;

public class CollectionConfigValueType<T>
extends ConfigValueType<Set<T>> {
    private CollectionConfigValueType(ConfigValueIOCodec<Set<T>> ioCodec, ConfigValueSyncCodec<Set<T>, ? extends Tag> syncCodec) {
        super(ioCodec, syncCodec);
    }

    public static final class Builder<T>
    extends ConfigValueType.Builder<Set<T>, Builder<T>> {
        private ConfigValueType<T> elementValueType;
        private Character ioCodecSeparator;

        private Builder() {
        }

        @Override
        public Builder<T> setDefault() {
            super.setDefault();
            this.setElementValueType(null);
            this.setIoCodecSeparator(null);
            return (Builder)this.self;
        }

        public Builder<T> setElementValueType(ConfigValueType<T> elementValueType) {
            this.elementValueType = elementValueType;
            return (Builder)this.self;
        }

        public Builder<T> setIoCodecSeparator(Character ioCodecSeparator) {
            this.ioCodecSeparator = ioCodecSeparator;
            return (Builder)this.self;
        }

        @Override
        public Builder<T> setIoCodec(ConfigValueIOCodec<Set<T>> ioCodec) {
            if (ioCodec != null) {
                throw new IllegalArgumentException("You must use setElementValueType with the list config value types!");
            }
            return (Builder)super.setIoCodec(ioCodec);
        }

        @Override
        public Builder<T> setSyncCodec(ConfigValueSyncCodec<Set<T>, ? extends Tag> syncCodec) {
            if (syncCodec != null) {
                throw new IllegalArgumentException("You must use setElementValueType with the list config value types!");
            }
            return (Builder)super.setSyncCodec(syncCodec);
        }

        @Override
        public ConfigValueType<Set<T>> build() {
            if (this.elementValueType == null || this.ioCodecSeparator == null) {
                throw new IllegalStateException();
            }
            ConfigValueType finalElementValueType = this.elementValueType;
            String finalIoCodecSeparator = "" + this.ioCodecSeparator;
            String separatorEscapeString = finalIoCodecSeparator.equals(Character.valueOf('\u00a7')) ? "$sep$" : "\u00a7sep\u00a7";
            this.ioCodec = new ConfigValueIOCodec<Set>(set -> {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append('[');
                boolean first = true;
                for (Object element : set) {
                    String encodedElement = finalElementValueType.getIoCodec().encode(element, null, null);
                    if (encodedElement.isEmpty()) continue;
                    if (!first) {
                        stringBuilder.append(finalIoCodecSeparator).append(" ");
                    }
                    encodedElement = encodedElement.replace(finalIoCodecSeparator, separatorEscapeString);
                    stringBuilder.append(encodedElement);
                    first = false;
                }
                stringBuilder.append(']');
                return stringBuilder.toString();
            }, encodedList -> {
                if (encodedList.startsWith("[") && encodedList.endsWith("]")) {
                    encodedList = encodedList.substring(1, encodedList.length() - 1);
                }
                String[] splitString = encodedList.split(finalIoCodecSeparator);
                LinkedHashSet decodedSet = new LinkedHashSet();
                for (String elementString : splitString) {
                    if ((elementString = elementString.replace(separatorEscapeString, finalIoCodecSeparator).trim()).isEmpty()) continue;
                    Object decodedElement = finalElementValueType.getIoCodec().decode(elementString, null, null);
                    if (decodedElement == null) {
                        return null;
                    }
                    decodedSet.add(decodedElement);
                }
                return Collections.unmodifiableSet(decodedSet);
            }, Integer.MAX_VALUE);
            this.syncCodec = new ConfigValueSyncCodec<Set, ListTag>(set -> {
                ListTag listTag = new ListTag();
                for (Object element : set) {
                    listTag.add((Object)finalElementValueType.getSyncCodec().encode(element));
                }
                return listTag;
            }, listTag -> {
                LinkedHashSet decodedSet = new LinkedHashSet();
                for (Tag tag : listTag) {
                    Object decodedElement = Builder.decodeElement(tag, finalElementValueType.getSyncCodec());
                    if (decodedElement == null) {
                        return null;
                    }
                    decodedSet.add(decodedElement);
                }
                return Collections.unmodifiableSet(decodedSet);
            });
            return super.build();
        }

        @Override
        protected CollectionConfigValueType<T> buildInternally() {
            return new CollectionConfigValueType(this.ioCodec, this.syncCodec);
        }

        private static <TAG extends Tag, T> T decodeElement(Tag tag, ConfigValueSyncCodec<T, TAG> syncCodec) {
            return syncCodec.decode(tag);
        }

        public static <T> Builder<T> begin() {
            return new Builder<T>().setDefault();
        }
    }
}

