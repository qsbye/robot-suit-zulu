/*
 * Decompiled with CFR 0.152.
 */
package xaero.lib.mixin.plugin;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import xaero.lib.platform.MixinServices;
import xaero.lib.platform.services.IPlatformMixinHelper;

public class MixinPlugin
implements IMixinConfigPlugin {
    private static final Map<String, String> MIXIN_MOD_REQ_MAP = ImmutableMap.of();
    public static final Map<String, String> MOD_REQ_CLASS_CHECKS = ImmutableMap.of((Object)"optifine", (Object)"optifine.OptiFineJar");

    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String modReq = MIXIN_MOD_REQ_MAP.get(mixinClassName);
        if (modReq == null) {
            int mixinPackageIndex = mixinClassName.indexOf(".mixin.");
            String relativeMixinPath = mixinClassName.substring(mixinPackageIndex + 7);
            int relativeDotIndex = relativeMixinPath.lastIndexOf(46);
            if (relativeDotIndex == -1) {
                return true;
            }
            modReq = relativeMixinPath.substring(0, relativeDotIndex);
        }
        String[] modReqArgs = modReq.split("\\.");
        String modId = modReqArgs[0];
        boolean isBreakRequirement = modReqArgs.length > 1 && modReqArgs[1].equals("breaks");
        String classCheck = MOD_REQ_CLASS_CHECKS.get(modId);
        if (classCheck != null) {
            try {
                Class.forName(classCheck);
                return !isBreakRequirement;
            }
            catch (ClassNotFoundException cnfe) {
                return isBreakRequirement;
            }
        }
        String minVersion = null;
        String maxVersion = null;
        int minVersionArgIndex = 1;
        if (isBreakRequirement) {
            ++minVersionArgIndex;
        }
        if (modReqArgs.length > minVersionArgIndex) {
            minVersion = modReqArgs[minVersionArgIndex].substring(1).replaceAll("_", ".").replaceAll("H", "-").replaceAll("P", "+");
        }
        if (modReqArgs.length > minVersionArgIndex + 1) {
            maxVersion = modReqArgs[minVersionArgIndex + 1].substring(1).replaceAll("_", ".").replaceAll("H", "-").replaceAll("P", "+");
        }
        return isBreakRequirement != this.isModRequirementMet(MixinServices.PLATFORM, modId, minVersion, maxVersion);
    }

    public <M, V extends Comparable<V>> boolean isModRequirementMet(IPlatformMixinHelper<M, V> platform, String modId, String minVersionString, String maxVersionString) {
        V minVersion;
        M modInfo = platform.getLoadingModInfo(modId);
        if (modInfo == null) {
            return false;
        }
        if (minVersionString == null && maxVersionString == null) {
            return true;
        }
        V installedVersion = platform.getModVersion(modInfo);
        if (minVersionString != null && installedVersion.compareTo(minVersion = platform.getVersionFromString(minVersionString)) < 0) {
            return false;
        }
        if (maxVersionString == null) {
            return true;
        }
        V maxVersion = platform.getVersionFromString(maxVersionString);
        return installedVersion.compareTo(maxVersion) <= 0;
    }

    public void onLoad(String mixinPackage) {
    }

    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    public String getRefMapperConfig() {
        return null;
    }

    public List<String> getMixins() {
        return null;
    }

    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

