/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.file;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.EnumSet;

public class SimpleBackup {
    public static Path moveToBackup(Path directory) {
        return SimpleBackup.moveToBackup(directory.getParent(), directory);
    }

    public static Path moveToBackup(Path backupFolderParent, Path directory) {
        Path backupFolder = backupFolderParent.resolve("backup");
        while (Files.exists(backupFolder, new LinkOption[0])) {
            backupFolder = backupFolder.getParent().resolve(backupFolder.getFileName().toString() + "-");
        }
        Path backupPath = backupFolder.resolve(directory.getFileName());
        try {
            Files.createDirectories(backupFolder, new FileAttribute[0]);
            Files.move(directory, backupPath, new CopyOption[0]);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to backup a directory! Can't continue.", e);
        }
        return backupPath;
    }

    public static void copyDirectoryWithContents(final Path from, final Path to, int maxDepth, final CopyOption ... copyOptions) throws IOException {
        Files.walkFileTree(from, EnumSet.of(FileVisitOption.FOLLOW_LINKS), maxDepth, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetPath = to.resolve(from.relativize(dir));
                if (!Files.exists(targetPath, new LinkOption[0])) {
                    Files.createDirectory(targetPath, new FileAttribute[0]);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, to.resolve(from.relativize(file)), copyOptions);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}

