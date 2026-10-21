/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.mods;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xaero.hud.minimap.MinimapLogs;
import xaero.lib.common.reflection.util.ReflectionUtils;

public class SupportFramedBlocks {
    private Class<?> framedTileBlockClass;
    private Method framedTileEntityCamoStateMethod;
    private Method framedTileEntityCamoMethod;
    private Method camoContainerStateMethod;
    private Method camoContainerContentMethod;
    private Method camoContentStateMethod;
    private boolean usable;
    private Set<Block> framedBlocks;

    public SupportFramedBlocks() {
        try {
            this.framedTileBlockClass = Class.forName("xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity");
        }
        catch (ClassNotFoundException cnfe) {
            try {
                this.framedTileBlockClass = Class.forName("xfacthd.framedblocks.common.tileentity.FramedTileEntity");
            }
            catch (ClassNotFoundException cnfe2) {
                try {
                    this.framedTileBlockClass = Class.forName("xfacthd.framedblocks.api.block.FramedBlockEntity");
                }
                catch (ClassNotFoundException cnfe3) {
                    MinimapLogs.LOGGER.info("Failed to init Framed Blocks support!", (Throwable)cnfe3);
                    return;
                }
            }
        }
        try {
            this.framedTileEntityCamoStateMethod = this.framedTileBlockClass.getDeclaredMethod("getCamoState", new Class[0]);
        }
        catch (NoSuchMethodException | SecurityException e1) {
            try {
                Class<?> camoContainerClass;
                try {
                    camoContainerClass = Class.forName("xfacthd.framedblocks.api.data.CamoContainer");
                }
                catch (ClassNotFoundException cnfe) {
                    camoContainerClass = Class.forName("xfacthd.framedblocks.api.camo.CamoContainer");
                }
                this.framedTileEntityCamoMethod = this.framedTileBlockClass.getDeclaredMethod("getCamo", new Class[0]);
                try {
                    this.camoContainerStateMethod = camoContainerClass.getDeclaredMethod("getState", new Class[0]);
                }
                catch (NoSuchMethodException nsme) {
                    this.camoContainerContentMethod = camoContainerClass.getDeclaredMethod("getContent", new Class[0]);
                    Class<?> camoContentClass = Class.forName("xfacthd.framedblocks.api.camo.CamoContent");
                    this.camoContentStateMethod = camoContentClass.getDeclaredMethod("getAppearanceState", new Class[0]);
                }
            }
            catch (ClassNotFoundException | NoSuchMethodException | SecurityException e2) {
                MinimapLogs.LOGGER.info("Failed to init Framed Blocks support!", (Throwable)e1);
                MinimapLogs.LOGGER.info("Failed to init Framed Blocks support!", (Throwable)e2);
            }
        }
        this.usable = this.framedTileBlockClass != null && (this.framedTileEntityCamoStateMethod != null || this.framedTileEntityCamoMethod != null && (this.camoContainerStateMethod != null || this.camoContainerContentMethod != null && this.camoContentStateMethod != null));
    }

    public void onWorldChange() {
        this.framedBlocks = null;
    }

    private void findFramedBlocks(Level world, Registry<Block> registry) {
        if (this.framedBlocks == null) {
            this.framedBlocks = new HashSet<Block>();
            if (registry == null) {
                registry = world.registryAccess().lookupOrThrow(Registries.BLOCK);
            }
            registry.entrySet().forEach(entry -> {
                ResourceKey key = (ResourceKey)entry.getKey();
                if (key.identifier().getNamespace().equals("framedblocks") && key.identifier().getPath().startsWith("framed_")) {
                    this.framedBlocks.add((Block)entry.getValue());
                }
            });
        }
    }

    public boolean isFrameBlock(Level world, Registry<Block> registry, BlockState state) {
        if (!this.usable) {
            return false;
        }
        this.findFramedBlocks(world, registry);
        return this.framedBlocks.contains(state.getBlock());
    }

    public BlockState unpackFramedBlock(Level world, Registry<Block> registry, BlockState original, BlockEntity tileEntity) {
        if (!this.usable) {
            return original;
        }
        if (this.framedTileBlockClass.isAssignableFrom(tileEntity.getClass())) {
            if (this.framedTileEntityCamoStateMethod != null) {
                return (BlockState)ReflectionUtils.getReflectMethodValue((Object)tileEntity, (Method)this.framedTileEntityCamoStateMethod, (Object[])new Object[0]);
            }
            Object camoContainer = ReflectionUtils.getReflectMethodValue((Object)tileEntity, (Method)this.framedTileEntityCamoMethod, (Object[])new Object[0]);
            if (this.camoContainerStateMethod != null) {
                return (BlockState)ReflectionUtils.getReflectMethodValue((Object)camoContainer, (Method)this.camoContainerStateMethod, (Object[])new Object[0]);
            }
            Object camoContent = ReflectionUtils.getReflectMethodValue((Object)camoContainer, (Method)this.camoContainerContentMethod, (Object[])new Object[0]);
            if (camoContent == null) {
                return original;
            }
            BlockState state = (BlockState)ReflectionUtils.getReflectMethodValue((Object)camoContent, (Method)this.camoContentStateMethod, (Object[])new Object[0]);
            if (state == null) {
                return original;
            }
            return state;
        }
        return original;
    }
}

