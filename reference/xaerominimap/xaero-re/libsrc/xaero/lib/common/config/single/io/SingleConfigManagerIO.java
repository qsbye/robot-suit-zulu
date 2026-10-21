/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.single.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.io.ConfigIO;
import xaero.lib.common.config.io.serialization.IConfigSerializer;
import xaero.lib.common.config.single.SingleConfigManager;

public final class SingleConfigManagerIO<C extends Config> {
    private final Path file;
    private final Path defaultConfigsFile;
    private final SingleConfigManager<C> manager;
    private final ConfigIO<C> configIO;

    private SingleConfigManagerIO(Path file, Path defaultConfigsFile, SingleConfigManager<C> manager, ConfigIO<C> configIO) {
        this.file = file;
        this.defaultConfigsFile = defaultConfigsFile;
        this.manager = manager;
        this.configIO = configIO;
    }

    public void load() {
        C result = Files.exists(this.file, new LinkOption[0]) ? this.configIO.load(this.file) : this.configIO.load(this.defaultConfigsFile);
        if (result == null) {
            return;
        }
        this.manager.setConfig(result);
    }

    public void save() {
        Path destinationFolder = this.file.getParent();
        if (!Files.exists(destinationFolder, new LinkOption[0])) {
            try {
                Files.createDirectories(destinationFolder, new FileAttribute[0]);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.configIO.save(this.manager.getConfig(), this.file);
    }

    public static final class Builder<C extends Config> {
        private Path destination;
        private Path defaultConfigsSource;
        private SingleConfigManager<C> manager;
        private IConfigSerializer<C> serializer;
        private boolean allowNullValues;
        private String extension;

        private Builder() {
        }

        public Builder<C> setDefault() {
            this.setDestination(null);
            this.setDefaultConfigsSource(null);
            this.setManager(null);
            this.setSerializer(null);
            this.setAllowNullValues(true);
            this.setExtension(null);
            return this;
        }

        public Builder<C> setDestination(Path destination) {
            this.destination = destination;
            return this;
        }

        public Builder<C> setDefaultConfigsSource(Path defaultConfigsSource) {
            this.defaultConfigsSource = defaultConfigsSource;
            return this;
        }

        public Builder<C> setManager(SingleConfigManager<C> manager) {
            this.manager = manager;
            return this;
        }

        public Builder<C> setSerializer(IConfigSerializer<C> serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder<C> setAllowNullValues(boolean allowNullValues) {
            this.allowNullValues = allowNullValues;
            return this;
        }

        public Builder<C> setExtension(String extension) {
            this.extension = extension;
            return this;
        }

        public SingleConfigManagerIO<C> build() {
            if (this.destination == null || this.manager == null || this.serializer == null || this.extension == null) {
                throw new IllegalStateException();
            }
            ConfigIO configIO = ConfigIO.Builder.begin().setLogger(this.manager.logger).setSerializer(this.serializer).setAllowNullValues(this.allowNullValues).build();
            Path file = this.destination.resolve(this.manager.getConfigId() + this.extension);
            Path defaultConfigsFile = this.defaultConfigsSource == null ? null : this.defaultConfigsSource.resolve(file.getFileName());
            return new SingleConfigManagerIO<C>(file, defaultConfigsFile, this.manager, configIO);
        }

        public static <C extends Config> Builder<C> begin() {
            return new Builder<C>().setDefault();
        }
    }
}

