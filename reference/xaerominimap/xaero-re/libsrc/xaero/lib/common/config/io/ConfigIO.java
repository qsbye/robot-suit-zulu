/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.io;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import org.apache.logging.log4j.Logger;
import xaero.lib.common.config.Config;
import xaero.lib.common.config.io.serialization.IConfigSerializer;
import xaero.lib.common.config.io.util.ConfigIOUtils;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.value.io.serialization.LargeConfigValueIOCodec;
import xaero.lib.common.config.option.value.type.LargeConfigValueType;
import xaero.lib.common.util.IOUtils;

public class ConfigIO<C extends Config> {
    public static final int IO_ATTEMPTS = 10;
    public final Logger logger;
    private final IConfigSerializer<C> serializer;
    private final boolean allowNullValues;

    protected ConfigIO(Logger logger, IConfigSerializer<C> serializer, boolean allowNullValues) {
        this.logger = logger;
        this.serializer = serializer;
        this.allowNullValues = allowNullValues;
    }

    public C load(Path file) {
        return this.load(file, 10);
    }

    /*
     * Exception decompiling
     */
    public C load(Path file, int attempts) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public void save(C config, Path file) {
        this.save(config, file, 10);
    }

    public void save(C config, Path file, int attempts) {
        Path fileTemp = file.resolveSibling(file.getFileName().toString() + ".temp");
        try {
            try (FileOutputStream outputStream = new FileOutputStream(fileTemp.toFile());
                 OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                 BufferedWriter writer = new BufferedWriter(outputStreamWriter);){
                String serializedProfile = this.serializer.serialize(config, file);
                writer.write(serializedProfile);
                writer.close();
                IOUtils.safeMoveAndReplace(fileTemp, file, true);
            }
            for (ConfigOption configOption : ((Config)config).getRemovedLargeOptions()) {
                this.deleteLargeOptionFile(file, configOption);
            }
            ((Config)config).postSave();
        }
        catch (IOException e) {
            if (--attempts < 1) {
                this.logger.error("Failed to save config {}!", (Object)file.getFileName().toString(), (Object)e);
                return;
            }
            this.logger.warn("IO exception trying to save config {}. Retrying...", (Object)file.getFileName().toString());
            try {
                Thread.sleep(20L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            this.save(config, file, attempts);
        }
    }

    private void deleteLargeOptionFile(Path originalConfigFile, ConfigOption<?> option) {
        String extension;
        LargeConfigValueType type = (LargeConfigValueType)option.getValueType();
        String subFolder = option.getId();
        Path largeValuePath = ConfigIOUtils.getLargeValueFilePath(originalConfigFile, subFolder, extension = ((LargeConfigValueIOCodec)type.getIoCodec()).getExtension());
        if (!Files.exists(largeValuePath, new LinkOption[0])) {
            return;
        }
        try {
            IOUtils.deleteFile(largeValuePath, 2);
        }
        catch (IOException e) {
            this.logger.error("Failed to delete large value file {}", (Object)largeValuePath, (Object)e);
        }
    }

    public void delete(C config, Path file, int attempts) {
        String debugId = file.getFileName().toString();
        for (ConfigOption<?> option : ((Config)config).usedOptions()) {
            if (!(option.getValueType() instanceof LargeConfigValueType)) continue;
            this.deleteLargeOptionFile(file, option);
        }
        try {
            IOUtils.deleteFile(file, attempts);
        }
        catch (IOException ioe) {
            this.logger.error("Failed to delete config profile {}.", (Object)debugId);
        }
    }

    private static /* synthetic */ void lambda$load$0(StringBuilder serializedProfileBuilder, String line) {
        serializedProfileBuilder.append(line).append("\n");
    }

    public static final class Builder<C extends Config> {
        private IConfigSerializer<C> serializer;
        private boolean allowNullValues;
        public Logger logger;

        private Builder() {
        }

        public Builder<C> setDefault() {
            this.setLogger(null);
            this.setSerializer(null);
            this.setAllowNullValues(false);
            return this;
        }

        public Builder<C> setLogger(Logger logger) {
            this.logger = logger;
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

        public ConfigIO<C> build() {
            if (this.logger == null || this.serializer == null) {
                throw new IllegalStateException();
            }
            return new ConfigIO<C>(this.logger, this.serializer, this.allowNullValues);
        }

        public static <C extends Config> Builder<C> begin() {
            return new Builder<C>().setDefault();
        }
    }
}

