/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.world.io;

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
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import xaero.common.HudMod;
import xaero.common.file.SimpleBackup;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.io.WaypointIO;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.container.MinimapWorldContainer;
import xaero.hud.minimap.world.container.MinimapWorldRootContainer;
import xaero.hud.minimap.world.container.config.io.RootConfigIO;
import xaero.hud.path.XaeroPath;
import xaero.lib.common.util.IOUtils;

public class MinimapWorldManagerIO {
    private final HudMod modMain;
    private final RootConfigIO rootConfigIO;
    private final WaypointIO waypointIO;
    private final Pattern backupFilePattern;

    public MinimapWorldManagerIO(HudMod modMain) {
        this.modMain = modMain;
        this.rootConfigIO = new RootConfigIO(modMain);
        this.waypointIO = new WaypointIO(modMain);
        this.backupFilePattern = Pattern.compile("^backup-*$");
    }

    public void loadWorldsFromAllSources(MinimapSession session, ClientPacketListener connection) throws IOException {
        this.fixOldRootFolder(session);
        boolean shouldResave = this.waypointIO.getOldIO().load(session);
        this.loadAllWorlds(session);
        if (shouldResave) {
            this.saveAllWorlds(session);
        }
    }

    public void loadAllWorlds(MinimapSession session) throws IOException {
        Path minimapTempToAddFolder;
        Path minimapFolderPath = this.modMain.getMinimapFolder();
        if (!Files.exists(minimapFolderPath, new LinkOption[0])) {
            Files.createDirectories(minimapFolderPath, new FileAttribute[0]);
        }
        if (Files.exists(minimapTempToAddFolder = minimapFolderPath.resolve("temp_to_add"), new LinkOption[0])) {
            MinimapWorldManagerIO.copyTempFilesBack(minimapTempToAddFolder);
        }
        this.convertWorldDimFilesToFolders();
        this.convertWorldDimFoldersToSingleFolder(session);
        Stream<Path> rootFiles = Files.list(minimapFolderPath);
        if (rootFiles == null) {
            return;
        }
        Iterator rootIterator = rootFiles.iterator();
        while (rootIterator.hasNext()) {
            String rootFolderName;
            Path rootFilePath = (Path)rootIterator.next();
            if (!Files.isDirectory(rootFilePath, new LinkOption[0]) || this.backupFilePattern.matcher(rootFolderName = rootFilePath.getFileName().toString()).find()) continue;
            this.loadWorldFolder(rootFilePath, rootFolderName, session);
        }
        rootFiles.close();
    }

    private void loadWorldFolder(Path folder, String rootFolderName, MinimapSession session) throws IOException {
        Stream<Path> worldFiles;
        XaeroPath rootPath = XaeroPath.root(rootFolderName);
        try {
            rootPath.applyToFilePath(this.modMain.getMinimapFolder());
        }
        catch (InvalidPathException ipe) {
            MinimapLogs.LOGGER.warn("Ignoring minimap world folder {} for somehow containing characters invalid for use with your file system.", (Object)rootFolderName);
            return;
        }
        Path tempToAdd = folder.resolve("temp_to_add");
        if (Files.exists(tempToAdd, new LinkOption[0])) {
            MinimapWorldManagerIO.copyTempFilesBack(tempToAdd);
        }
        if ((worldFiles = Files.list(folder)) == null) {
            return;
        }
        Iterator worldFileIterator = worldFiles.iterator();
        while (worldFileIterator.hasNext()) {
            Path worldFile = (Path)worldFileIterator.next();
            String worldFileName = worldFile.getFileName().toString();
            if (this.backupFilePattern.matcher(worldFileName).find()) continue;
            if (!Files.isDirectory(worldFile, new LinkOption[0])) {
                if (!worldFileName.contains("_")) continue;
                MinimapWorldContainer container = session.getWorldManager().addWorldContainer(rootPath);
                this.loadWorldFile(container, worldFileName, null);
                continue;
            }
            this.loadDimensionFolder(worldFileName, worldFile, rootFolderName, session);
        }
        if (session.getWorldManager().getWorldContainer(rootPath).isEmpty()) {
            session.getWorldManager().removeContainer(rootPath);
        }
        worldFiles.close();
    }

    private void loadDimensionFolder(String dimensionName, Path folder, String rootFolderName, MinimapSession session) throws IOException {
        Path tempToAdd2 = folder.resolve("temp_to_add");
        if (Files.exists(tempToAdd2, new LinkOption[0])) {
            MinimapWorldManagerIO.copyTempFilesBack(tempToAdd2);
        }
        String fixedDimensionName = this.waypointIO.getOldIO().fixOldDimensionName(dimensionName);
        XaeroPath containerKey = XaeroPath.root(rootFolderName).resolve(fixedDimensionName);
        MinimapWorldContainer container = session.getWorldManager().addWorldContainer(containerKey);
        Stream<Path> dimensionFiles = Files.list(folder);
        if (dimensionFiles != null) {
            Iterator dimensionFileIterator = dimensionFiles.iterator();
            while (dimensionFileIterator.hasNext()) {
                Path dimensionFile = (Path)dimensionFileIterator.next();
                String fileName = dimensionFile.getFileName().toString();
                this.loadWorldFile(container, fileName, dimensionFile);
            }
            dimensionFiles.close();
        }
        if (container.isEmpty()) {
            session.getWorldManager().removeContainer(containerKey);
        }
        if (!fixedDimensionName.equals(dimensionName)) {
            SimpleBackup.moveToBackup(folder);
            this.saveWorlds(container);
        }
    }

    public boolean loadWorldFile(MinimapWorldContainer container, String fileName, Path filePath) throws IOException {
        MinimapWorld world;
        String noExtension;
        if (!fileName.endsWith(".txt")) {
            return false;
        }
        String multiworldId = noExtension = fileName.substring(0, fileName.lastIndexOf("."));
        if (!noExtension.equals("waypoints")) {
            String[] multiworld = noExtension.split("_");
            if (multiworld.length < 2) {
                return false;
            }
            multiworldId = multiworld[0];
            String multiworldName = multiworld[1].replace("%us%", "_");
            container.setName(multiworldId, multiworldName);
        }
        if ((world = container.addWorld(multiworldId)) != null) {
            this.loadWorld(world, filePath);
        }
        return true;
    }

    public void loadWorld(MinimapWorld world, Path filePath) throws IOException {
        block13: {
            if (filePath == null) {
                filePath = this.getWorldFile(world);
            }
            if (!Files.exists(filePath, new LinkOption[0])) {
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(filePath.toFile()), "UTF8"));
            block7: while (true) {
                String s;
                while ((s = reader.readLine()) != null) {
                    Object[] args = s.split(":");
                    try {
                        this.checkWorldFileLine((String[])args, world);
                        continue block7;
                    }
                    catch (Throwable e) {
                        MinimapLogs.LOGGER.error("Skipping minimap world file line:" + Arrays.toString(args), e);
                    }
                }
                break block13;
                {
                    continue block7;
                    break;
                }
                break;
            }
            finally {
                reader.close();
            }
        }
        if (world.getSlimeChunkSeed() != null) {
            return;
        }
        Long legacySlimeSeed = this.modMain.getSettings().getLegacySlimeChunksSeed(world.getFullPath());
        if (legacySlimeSeed == null) {
            return;
        }
        world.setSlimeChunkSeed(legacySlimeSeed);
        this.saveWorld(world);
        this.modMain.getSettings().removeLegacySlimeChunksSeed(world.getFullPath());
    }

    public boolean checkWorldFileLine(String[] args, MinimapWorld world) {
        if (this.waypointIO.checkLine(args, world)) {
            return true;
        }
        if (args[0].equals("slime_chunk_seed")) {
            try {
                world.setSlimeChunkSeed(Long.parseLong(args[1]));
            }
            catch (NumberFormatException nfe) {
                world.setSlimeChunkSeed(Long.valueOf(args[1].hashCode()));
            }
        }
        return false;
    }

    public void saveWorlds(MinimapWorldContainer container) throws IOException {
        for (MinimapWorld world : container.getAllWorldsIterable()) {
            this.saveWorld(world);
        }
    }

    public void saveAllWorlds(MinimapSession session) throws IOException {
        for (MinimapWorldRootContainer rootContainer : session.getWorldManager().getRootContainers()) {
            this.saveWorlds(rootContainer);
        }
    }

    public void saveWorld(MinimapWorld wpw) throws IOException {
        this.saveWorld(wpw, true);
    }

    public void saveWorld(MinimapWorld world, boolean overwrite) throws IOException {
        if (world == null) {
            return;
        }
        Path worldFilePath = this.getWorldFile(world);
        if (Files.exists(worldFilePath, new LinkOption[0]) && !overwrite) {
            return;
        }
        Path worldFileTempPath = worldFilePath.getParent().resolve(String.valueOf(worldFilePath.getFileName()) + ".temp");
        try (BufferedOutputStream bufferedOutput = new BufferedOutputStream(new FileOutputStream(worldFileTempPath.toFile()));
             OutputStreamWriter output = new OutputStreamWriter((OutputStream)bufferedOutput, StandardCharsets.UTF_8);){
            this.waypointIO.saveWaypoints(world, output);
            if (world.getSlimeChunkSeed() != null) {
                output.write("slime_chunk_seed:" + world.getSlimeChunkSeed());
            }
        }
        IOUtils.safeMoveAndReplace((Path)worldFileTempPath, (Path)worldFilePath, (boolean)true);
        if (world.hasSomethingToRemoveOnSave()) {
            world.cleanupOnSave(worldFilePath);
        }
    }

    public void convertWorldDimFilesToFolders() throws IOException {
        Stream<Path> files = Files.list(this.modMain.getMinimapFolder());
        Path backupFolder = this.modMain.getMinimapFolder().resolve("backup");
        Files.createDirectories(backupFolder, new FileAttribute[0]);
        if (files == null) {
            return;
        }
        Iterator rootFileIterator = files.iterator();
        while (rootFileIterator.hasNext()) {
            Object fileName;
            Path rootFilePath = (Path)rootFileIterator.next();
            if (Files.isDirectory(rootFilePath, new LinkOption[0]) || !((String)(fileName = rootFilePath.getFileName().toString())).endsWith(".txt") || !((String)fileName).contains("_")) continue;
            int lastUnderscore = ((String)fileName).lastIndexOf("_");
            if (!((String)fileName).startsWith("Multiplayer_") && !((String)fileName).startsWith("Realms_")) {
                fileName = ((String)fileName).substring(0, lastUnderscore).replace("_", "%us%") + ((String)fileName).substring(lastUnderscore);
            }
            String noExtension = ((String)fileName).substring(0, ((String)fileName).lastIndexOf("."));
            Path folderPath = rootFilePath.getParent().resolve(noExtension);
            Path fixedFilePath = folderPath.resolve("waypoints.txt");
            Path backupFilePath = backupFolder.resolve((String)fileName);
            if (!Files.exists(folderPath, new LinkOption[0])) {
                Files.createDirectories(folderPath, new FileAttribute[0]);
            }
            if (!Files.exists(backupFilePath, new LinkOption[0])) {
                Files.copy(rootFilePath, backupFilePath, new CopyOption[0]);
            }
            try {
                Files.move(rootFilePath, fixedFilePath, new CopyOption[0]);
            }
            catch (FileAlreadyExistsException e) {
                if (!Files.exists(backupFilePath, new LinkOption[0])) continue;
                Files.deleteIfExists(rootFilePath);
            }
        }
        files.close();
    }

    public void convertWorldDimFoldersToSingleFolder(MinimapSession session) throws IOException {
        Stream<Path> files = Files.list(this.modMain.getMinimapFolder());
        if (files == null) {
            return;
        }
        Iterator rootFileIterator = files.iterator();
        while (rootFileIterator.hasNext()) {
            Stream<Path> deleteCheck;
            Stream<Path> dimensionFiles;
            Path correctDimensionFolder;
            String lastArg;
            String folderName;
            String[] folderArgs;
            Path rootFilePath = (Path)rootFileIterator.next();
            if (!Files.isDirectory(rootFilePath, new LinkOption[0]) || (folderArgs = (folderName = rootFilePath.getFileName().toString()).split("_")).length <= 2 && (folderArgs.length != 2 || folderArgs[0].equals("Multiplayer")) || !(lastArg = folderArgs[folderArgs.length - 1]).equals("null") && (!lastArg.startsWith("DIM") || lastArg.length() == 3)) continue;
            int dimensionId = lastArg.equals("null") ? 0 : Integer.parseInt(lastArg.substring(3));
            Object dimensionName = "dim%" + dimensionId;
            ResourceKey<Level> dimRegistryKey = session.getDimensionHelper().getDimensionKeyForDirectoryName((String)dimensionName);
            if (dimRegistryKey != null) {
                dimensionName = session.getDimensionHelper().getDimensionDirectoryName(dimRegistryKey);
            }
            if (!Files.exists(correctDimensionFolder = rootFilePath.getParent().resolve(folderName.substring(0, folderName.lastIndexOf("_"))).resolve((String)dimensionName), new LinkOption[0])) {
                Files.createDirectories(correctDimensionFolder, new FileAttribute[0]);
            }
            if ((dimensionFiles = Files.list(rootFilePath)) != null) {
                Iterator dimensionFileIterator = dimensionFiles.iterator();
                while (dimensionFileIterator.hasNext()) {
                    Path dimensionFilePath = (Path)dimensionFileIterator.next();
                    if (Files.isDirectory(dimensionFilePath, new LinkOption[0])) continue;
                    Path correctFilePath = correctDimensionFolder.resolve(dimensionFilePath.getFileName());
                    Files.move(dimensionFilePath, correctFilePath, new CopyOption[0]);
                }
                dimensionFiles.close();
            }
            if ((deleteCheck = Files.list(rootFilePath)) == null) continue;
            boolean oldFolderEmpty = deleteCheck.count() == 0L;
            deleteCheck.close();
            if (!oldFolderEmpty) continue;
            Files.deleteIfExists(rootFilePath);
        }
        files.close();
    }

    public static void copyTempFilesBack(Path folder) throws IOException {
        Stream<Path> tempFiles = Files.list(folder);
        if (tempFiles != null) {
            Iterator tempFilesIterator = tempFiles.iterator();
            while (tempFilesIterator.hasNext()) {
                Path tempFile = (Path)tempFilesIterator.next();
                Path newLocation = folder.getParent().resolve(tempFile.getFileName());
                if (!Files.exists(newLocation, new LinkOption[0]) || !Files.isDirectory(newLocation, new LinkOption[0]) && Files.size(newLocation) == 0L) {
                    IOUtils.safeMoveAndReplace((Path)tempFile, (Path)newLocation, (boolean)false);
                    continue;
                }
                SimpleBackup.moveToBackup(folder.getParent(), tempFile);
            }
            tempFiles.close();
        }
        Files.delete(folder);
    }

    private void fixOldRootFolder(MinimapSession session) throws IOException {
        XaeroPath autoRootContainerPath = session.getWorldState().getAutoRootContainerPath();
        for (int format = 3; format >= 0; --format) {
            this.fixOldRootFolder(autoRootContainerPath, session.getWorldState().getOutdatedAutoRootContainerPath(format));
        }
    }

    private void fixOldRootFolder(XaeroPath path, XaeroPath outdatedPath) throws IOException {
        if (!path.equals(outdatedPath)) {
            Path fixedFolder;
            Path oldFormatRootFolder;
            try {
                oldFormatRootFolder = outdatedPath.applyToFilePath(this.modMain.getMinimapFolder());
            }
            catch (InvalidPathException ipe) {
                return;
            }
            if (Files.exists(oldFormatRootFolder, new LinkOption[0]) && !Files.exists(fixedFolder = path.applyToFilePath(this.modMain.getMinimapFolder()), new LinkOption[0])) {
                Files.move(oldFormatRootFolder, fixedFolder, new CopyOption[0]);
            }
        }
    }

    public void onRootContainerAdded(MinimapWorldRootContainer rootContainer) {
        if (!rootContainer.isConfigLoaded()) {
            this.rootConfigIO.load(rootContainer);
        }
    }

    public Path getWorldFile(MinimapWorld w) throws IOException {
        Path containerFolderPath = w.getContainer().getDirectoryPath();
        if (!Files.exists(containerFolderPath, new LinkOption[0])) {
            Files.createDirectories(containerFolderPath, new FileAttribute[0]);
        }
        Object fileName = w.getNode();
        String worldName = w.getContainer().getName(w.getNode());
        if (worldName != null) {
            fileName = (String)fileName + "_" + worldName.replace("_", "%us%").replace(":", "\u00a7\u00a7");
        }
        fileName = (String)fileName + ".txt";
        return containerFolderPath.resolve((String)fileName);
    }

    public RootConfigIO getRootConfigIO() {
        return this.rootConfigIO;
    }
}

