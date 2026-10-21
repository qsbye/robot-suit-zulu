/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.profile.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.stream.Stream;
import xaero.lib.common.config.io.ConfigIO;
import xaero.lib.common.config.io.serialization.IConfigSerializer;
import xaero.lib.common.config.option.ConfigOptionManager;
import xaero.lib.common.config.profile.ConfigProfile;
import xaero.lib.common.config.profile.ConfigProfileManager;
import xaero.lib.common.config.profile.io.serialization.cfg.ConfigProfileCfgSerializer;

public final class ConfigProfileManagerIO {
    private final ConfigProfileManager manager;
    private final Path path;
    private final Path defaultConfigsPath;
    private final String extension;
    private final ConfigIO<ConfigProfile> configIO;

    private ConfigProfileManagerIO(ConfigProfileManager manager, Path path, Path defaultConfigsPath, String extension, ConfigIO<ConfigProfile> configIO) {
        this.manager = manager;
        this.path = path;
        this.defaultConfigsPath = defaultConfigsPath;
        this.extension = extension;
        this.configIO = configIO;
    }

    public void load() {
        this.manager.reset();
        Path pathToLoad = this.path;
        if (!Files.exists(this.path, new LinkOption[0])) {
            if (this.defaultConfigsPath != null && Files.exists(this.defaultConfigsPath, new LinkOption[0])) {
                pathToLoad = this.defaultConfigsPath;
            } else {
                return;
            }
        }
        this.load(pathToLoad, 10);
    }

    private void load(Path pathToLoad, int attempts) {
        if (!Files.isDirectory(pathToLoad, new LinkOption[0])) {
            this.loadFile(pathToLoad);
            return;
        }
        try (Stream<Path> allFiles = Files.list(pathToLoad);){
            allFiles.forEach(fileOrFolder -> {
                if (Files.isDirectory(fileOrFolder, new LinkOption[0])) {
                    return;
                }
                this.loadFile((Path)fileOrFolder);
            });
        }
        catch (IOException ioe) {
            if (--attempts < 1) {
                throw new RuntimeException("Failed to load config profiles!", ioe);
            }
            this.load(pathToLoad, attempts);
        }
    }

    private void loadFile(Path file) {
        this.loadFile(file, 10);
    }

    private void loadFile(Path file, int attempts) {
        String fileName = file.getFileName().toString();
        if (!fileName.endsWith(this.extension)) {
            return;
        }
        ConfigProfile loadedProfile = this.configIO.load(file, attempts);
        if (loadedProfile == null) {
            return;
        }
        this.manager.add(loadedProfile);
    }

    public void saveAll() {
        for (ConfigProfile profile : this.manager) {
            this.save(profile);
        }
    }

    public void save(ConfigProfile profile) {
        this.save(profile, 10);
    }

    private void save(ConfigProfile profile, int attempts) {
        Path file = this.getProfilePath(profile);
        if (file == null) {
            return;
        }
        this.configIO.save(profile, file, attempts);
    }

    private Path getProfilePath(ConfigProfile profile) {
        boolean pathIsFolder;
        boolean bl = pathIsFolder = !this.path.getFileName().toString().endsWith(this.extension);
        if (!this.createDirectories(pathIsFolder, 10)) {
            return null;
        }
        Path file = pathIsFolder ? this.path.resolve(profile.getId() + this.extension) : this.path;
        return file;
    }

    public void delete(ConfigProfile profile) {
        this.delete(profile, 10);
    }

    public void delete(ConfigProfile profile, int attempts) {
        Path file = this.getProfilePath(profile);
        if (file == null) {
            return;
        }
        this.configIO.delete(profile, file, attempts);
    }

    private boolean createDirectories(boolean pathIsFolder, int attempts) {
        try {
            if (!Files.exists(this.path.getParent(), new LinkOption[0])) {
                Files.createDirectories(this.path.getParent(), new FileAttribute[0]);
            }
            if (pathIsFolder && !Files.exists(this.path, new LinkOption[0])) {
                Files.createDirectories(this.path, new FileAttribute[0]);
            }
            return true;
        }
        catch (IOException e) {
            if (--attempts < 1) {
                this.manager.logger.error("Failed to create config profile directory!", (Throwable)e);
                return false;
            }
            return this.createDirectories(pathIsFolder, attempts);
        }
    }

    public static final class Builder {
        private ConfigProfileManager manager;
        private Path path;
        private Path defaultConfigsPath;
        private boolean singleFile;
        private String extension;
        private IConfigSerializer<ConfigProfile> serializer;
        private ConfigOptionManager optionsDefault;
        private boolean allowNullValues;
        private boolean configsTrackDirtyOptionsDefault;

        private Builder() {
        }

        public Builder setDefault() {
            this.setManager(null);
            this.setPath(null);
            this.setDefaultConfigsPath(null);
            this.setSingleFile(false);
            this.setExtension(null);
            this.setSerializer(null);
            this.setOptionsDefault(null);
            this.setAllowNullValues(false);
            this.setConfigsTrackDirtyOptionsDefault(false);
            return this;
        }

        public Builder setManager(ConfigProfileManager manager) {
            this.manager = manager;
            return this;
        }

        public Builder setPath(Path path) {
            this.path = path;
            return this;
        }

        public Builder setDefaultConfigsPath(Path defaultConfigsPath) {
            this.defaultConfigsPath = defaultConfigsPath;
            return this;
        }

        public Builder setSingleFile(boolean singleFile) {
            this.singleFile = singleFile;
            return this;
        }

        public Builder setExtension(String extension) {
            this.extension = extension;
            return this;
        }

        public Builder setSerializer(IConfigSerializer<ConfigProfile> serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder setOptionsDefault(ConfigOptionManager optionsDefault) {
            this.optionsDefault = optionsDefault;
            return this;
        }

        public Builder setAllowNullValues(boolean allowNullValues) {
            this.allowNullValues = allowNullValues;
            return this;
        }

        public Builder setConfigsTrackDirtyOptionsDefault(boolean configsTrackDirtyOptionsDefault) {
            this.configsTrackDirtyOptionsDefault = configsTrackDirtyOptionsDefault;
            return this;
        }

        public ConfigProfileManagerIO build() {
            if (this.manager == null || this.path == null || this.extension == null || this.serializer == null && this.optionsDefault == null) {
                throw new IllegalStateException();
            }
            if (this.serializer == null) {
                this.serializer = ((ConfigProfileCfgSerializer.Builder)((ConfigProfileCfgSerializer.Builder)ConfigProfileCfgSerializer.Builder.begin().setConfigType(this.manager.getConfigType()).setOptions(this.optionsDefault)).setConfigsTrackDirtyOptions(this.configsTrackDirtyOptionsDefault)).build();
            }
            if (this.singleFile && !this.path.getFileName().toString().endsWith(this.extension)) {
                this.path = this.path.resolveSibling(String.valueOf(this.path.getFileName()) + this.extension);
                if (this.defaultConfigsPath != null) {
                    this.defaultConfigsPath = this.defaultConfigsPath.resolveSibling(this.path.getFileName());
                }
            }
            ConfigIO<ConfigProfile> configIO = ConfigIO.Builder.begin().setLogger(this.manager.logger).setSerializer(this.serializer).setAllowNullValues(this.allowNullValues).build();
            return new ConfigProfileManagerIO(this.manager, this.path, this.defaultConfigsPath, this.extension, configIO);
        }

        public static Builder begin() {
            return new Builder().setDefault();
        }
    }
}

