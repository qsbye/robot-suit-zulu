/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.category;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.radar.category.EntityRadarCategory;
import xaero.hud.minimap.radar.category.serialization.EntityRadarCategorySerializationHandler;
import xaero.lib.common.util.IOUtils;

public final class EntityRadarCategoryFileIO {
    private final Path saveLocationPath;
    private final EntityRadarCategorySerializationHandler serializationHandler;

    private EntityRadarCategoryFileIO(@Nonnull Path saveLocationPath, @Nonnull EntityRadarCategorySerializationHandler serializationHandler) {
        this.saveLocationPath = saveLocationPath;
        this.serializationHandler = serializationHandler;
    }

    public void saveRootCategory(EntityRadarCategory category) {
        Path saveLocationTempPath = this.saveLocationPath.resolveSibling(this.saveLocationPath.getFileName().toString() + ".temp");
        String serializedData = this.serializationHandler.serialize(category);
        this.saveRootCategory(saveLocationTempPath, serializedData, 10);
    }

    public void saveRootCategory(Path saveLocationTempPath, String serializedData, int attempts) {
        try (FileOutputStream fileOutput = new FileOutputStream(saveLocationTempPath.toFile());
             BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);
             OutputStreamWriter writer = new OutputStreamWriter((OutputStream)bufferedOutput, StandardCharsets.UTF_8);){
            writer.write(serializedData);
            writer.close();
            IOUtils.safeMoveAndReplace((Path)saveLocationTempPath, (Path)this.saveLocationPath, (boolean)true);
        }
        catch (IOException e) {
            if (attempts <= 1) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
                return;
            }
            MinimapLogs.LOGGER.info("Failed to save entity radar categories. Retrying... " + attempts);
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            this.saveRootCategory(saveLocationTempPath, serializedData, --attempts);
        }
    }

    public EntityRadarCategory loadRootCategory() throws IOException {
        String serializedData;
        try (FileInputStream fileInput = new FileInputStream(this.saveLocationPath.toFile());
             BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)fileInput, "UTF8"));){
            StringBuilder stringBuilder = new StringBuilder();
            reader.lines().forEach(line -> stringBuilder.append((String)line).append('\n'));
            serializedData = stringBuilder.toString();
        }
        try {
            return (EntityRadarCategory)this.serializationHandler.deserialize(serializedData);
        }
        catch (Throwable t) {
            MinimapLogs.LOGGER.error("A minimap entity radar config file is not usable (is likely corrupt)! Resolving...");
            Path backupPath = IOUtils.quickFileBackupMove((Path)this.saveLocationPath);
            MinimapLogs.LOGGER.error(String.format("The broken file was backed up to %s and ignored.", backupPath), t);
            return null;
        }
    }

    public static final class Builder {
        private Path saveLocationPath;
        private final EntityRadarCategorySerializationHandler.Builder serializationHandlerBuilder;

        private Builder(EntityRadarCategorySerializationHandler.Builder serializationHandlerBuilder) {
            this.serializationHandlerBuilder = serializationHandlerBuilder;
        }

        private Builder setDefault() {
            this.saveLocationPath = null;
            return this;
        }

        public Builder setSaveLocationPath(Path saveLocationPath) {
            this.saveLocationPath = saveLocationPath;
            return this;
        }

        public EntityRadarCategoryFileIO build() {
            if (this.saveLocationPath == null || this.serializationHandlerBuilder == null) {
                throw new IllegalStateException("required fields not set!");
            }
            return new EntityRadarCategoryFileIO(this.saveLocationPath, (EntityRadarCategorySerializationHandler)this.serializationHandlerBuilder.build());
        }

        public static Builder begin(EntityRadarCategorySerializationHandler.Builder serializationHandlerBuilder) {
            return new Builder(serializationHandlerBuilder).setDefault();
        }
    }
}

