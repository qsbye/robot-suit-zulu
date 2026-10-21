/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.config.option.value.io.serialization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.function.Function;
import javax.annotation.Nullable;
import xaero.lib.XaeroLib;
import xaero.lib.common.config.io.util.ConfigIOUtils;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.option.value.io.serialization.ConfigValueIOCodec;
import xaero.lib.common.util.IOUtils;

public class LargeConfigValueIOCodec<T>
extends ConfigValueIOCodec<T> {
    private static final int IO_ATTEMPTS = 10;
    private final String extension;

    public LargeConfigValueIOCodec(Function<T, String> encoder, Function<String, T> decoder, int maxStringLength, String extension) {
        super(encoder, decoder, maxStringLength);
        this.extension = extension;
    }

    @Override
    public String encode(T value, @Nullable Path file, @Nullable ConfigOption<T> option) {
        String encodedValue = super.encode(value, file, option);
        if (file == null) {
            return encodedValue;
        }
        String subFolder = option.getId();
        Path largeValueFilePath = ConfigIOUtils.getLargeValueFilePath(file, subFolder, this.extension);
        this.saveValueToFile(largeValueFilePath, encodedValue, 10);
        return "This option's value is saved to sub-folder \"" + subFolder + "\".";
    }

    @Override
    public T decode(String valueString, @Nullable Path file, @Nullable ConfigOption<T> option) {
        if (file == null) {
            return super.decode(valueString, null, option);
        }
        String subFolder = option.getId();
        Path largeValueFilePath = ConfigIOUtils.getLargeValueFilePath(file, subFolder, this.extension);
        String encodedValue = this.loadValueFromFile(largeValueFilePath, 10);
        if (encodedValue == null) {
            return null;
        }
        try {
            return super.decode(encodedValue, file, option);
        }
        catch (Throwable t) {
            XaeroLib.LOGGER.error("Error trying to decode data loaded from file {}! It's likely corrupt. Resolving...", (Object)largeValueFilePath);
            try {
                Path backupPath = IOUtils.tryQuickFileBackupMove(largeValueFilePath, 10);
                XaeroLib.LOGGER.error(String.format("The broken file was backed up to %s and ignored.", backupPath), t);
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
            return null;
        }
    }

    private void saveValueToFile(Path path, String encodedValue, int attempts) {
        Path pathParent = path.getParent();
        try {
            if (!Files.exists(pathParent, new LinkOption[0])) {
                Files.createDirectories(pathParent, new FileAttribute[0]);
            }
            File file = path.toFile();
            try (FileOutputStream fileOutput = new FileOutputStream(file);
                 OutputStreamWriter outputWriter = new OutputStreamWriter(fileOutput);
                 BufferedWriter writer = new BufferedWriter(outputWriter);){
                writer.write(encodedValue);
            }
        }
        catch (IOException e) {
            if (--attempts > 0) {
                XaeroLib.LOGGER.warn("IO exception trying to save config value to {}. Retrying...", (Object)path, (Object)e);
                try {
                    Thread.sleep(50L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                this.saveValueToFile(path, encodedValue, attempts);
                return;
            }
            XaeroLib.LOGGER.error("Failed to save config value to {}", (Object)path, (Object)e);
        }
    }

    /*
     * Exception decompiling
     */
    private String loadValueFromFile(Path path, int attempts) {
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

    public String getExtension() {
        return this.extension;
    }

    private static /* synthetic */ void lambda$loadValueFromFile$0(StringBuilder stringBuilder, String line) {
        if (!stringBuilder.isEmpty()) {
            stringBuilder.append("\n");
        }
        stringBuilder.append(line);
    }
}

