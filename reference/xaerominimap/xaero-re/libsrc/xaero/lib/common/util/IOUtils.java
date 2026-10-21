/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.common.util;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;
import xaero.lib.XaeroLib;

public class IOUtils {
    public static Path quickFileBackupMove(Path file) throws IOException {
        Path backupPath;
        int backupNumber = 0;
        while (Files.exists(backupPath = file.resolveSibling(file.getFileName().toString() + ".backup" + backupNumber), new LinkOption[0])) {
            ++backupNumber;
        }
        Files.move(file, backupPath, new CopyOption[0]);
        return backupPath;
    }

    public static Path tryQuickFileBackupMove(Path file, int attempts) throws IOException {
        try {
            XaeroLib.LOGGER.info("Attempting to back up file {}", (Object)file);
            return IOUtils.quickFileBackupMove(file);
        }
        catch (IOException e) {
            if (--attempts > 0) {
                XaeroLib.LOGGER.info("Failed due to an IO exception. A computer restart might help. Retrying... {}", (Object)attempts);
                try {
                    Thread.sleep(50L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                return IOUtils.tryQuickFileBackupMove(file, attempts);
            }
            throw e;
        }
    }

    public static void safeMoveAndReplace(Path from, Path to, boolean backupFrom) throws IOException {
        Path fromBackupPath;
        Path backupPath;
        block8: {
            backupPath = null;
            fromBackupPath = null;
            if (backupFrom) {
                while (true) {
                    try {
                        fromBackupPath = IOUtils.quickFileBackupMove(from);
                        break block8;
                    }
                    catch (IOException ioe2) {
                        try {
                            Thread.sleep(10L);
                        }
                        catch (InterruptedException interruptedException) {}
                        continue;
                    }
                    break;
                }
            }
            fromBackupPath = from;
        }
        if (Files.exists(to, new LinkOption[0])) {
            backupPath = IOUtils.quickFileBackupMove(to);
        }
        Files.move(fromBackupPath, to, new CopyOption[0]);
        if (backupPath != null) {
            Files.delete(backupPath);
        }
    }

    public static void deleteFile(Path file, int attempts) throws IOException {
        IOUtils.deleteFileIf(file, path -> true, attempts);
    }

    public static void deleteFileIf(Path file, final Predicate<Path> condition, int attempts) throws IOException {
        --attempts;
        try {
            Files.walkFileTree(file, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes basicFileAttributes) throws IOException {
                    if (condition.test(path)) {
                        Files.delete(path);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path path, IOException iOException) throws IOException {
                    if (iOException != null) {
                        throw iOException;
                    }
                    if (condition.test(path)) {
                        Files.delete(path);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (IOException e) {
            if (attempts > 0) {
                XaeroLib.LOGGER.info("Failed to delete file/folder! Retrying... " + attempts);
                try {
                    Thread.sleep(50L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                IOUtils.deleteFileIf(file, condition, attempts);
            }
            throw e;
        }
    }

    public static String replaceTrailingDots(String string, char replacement) {
        StringBuilder path = new StringBuilder(string);
        int dotCount = 0;
        while (!path.isEmpty() && path.charAt(path.length() - 1) == '.') {
            path.deleteCharAt(path.length() - 1);
            ++dotCount;
        }
        for (int i = 0; i < dotCount; ++i) {
            path.append(replacement);
        }
        return path.toString();
    }
}

