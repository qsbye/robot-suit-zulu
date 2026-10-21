/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.world.state;

import xaero.hud.path.XaeroPath;

public class MinimapWorldState {
    private XaeroPath autoRootContainerPath;
    private XaeroPath autoWorldPath;
    private XaeroPath customWorldPath;
    private final XaeroPath[] outdatedAutoRootContainerPaths = new XaeroPath[4];
    private XaeroPath autoContainerPathIgnoreCaseCache;

    public void setAutoRootContainerPath(XaeroPath autoRootContainerPath) {
        if (this.autoRootContainerPath != null) {
            throw new IllegalStateException();
        }
        this.autoRootContainerPath = autoRootContainerPath;
    }

    public XaeroPath getAutoRootContainerPath() {
        return this.autoRootContainerPath;
    }

    public void setOutdatedAutoRootContainerPath(int format, XaeroPath autoRootContainerPath) {
        if (this.outdatedAutoRootContainerPaths[format] != null) {
            throw new IllegalStateException();
        }
        this.outdatedAutoRootContainerPaths[format] = autoRootContainerPath;
    }

    public XaeroPath getOutdatedAutoRootContainerPath(int format) {
        return this.outdatedAutoRootContainerPaths[format];
    }

    public XaeroPath getAutoWorldPath() {
        return this.autoWorldPath;
    }

    public void setAutoWorldPath(XaeroPath autoWorldPath) {
        this.autoWorldPath = autoWorldPath;
    }

    public XaeroPath getCustomContainerPath() {
        if (this.customWorldPath == null) {
            return null;
        }
        return this.customWorldPath.getParent();
    }

    public XaeroPath getCustomWorldPath() {
        return this.customWorldPath;
    }

    public void setCustomWorldPath(XaeroPath customWorldPath) {
        this.customWorldPath = customWorldPath;
    }

    public XaeroPath getAutoContainerPathIgnoreCaseCache() {
        return this.autoContainerPathIgnoreCaseCache;
    }

    public void setAutoContainerPathIgnoreCaseCache(XaeroPath autoContainerPathIgnoreCaseCache) {
        this.autoContainerPathIgnoreCaseCache = autoContainerPathIgnoreCaseCache;
    }

    public XaeroPath getCurrentWorldPath() {
        return this.getCurrentWorldPath(this.autoWorldPath);
    }

    public XaeroPath getCurrentContainerPath() {
        XaeroPath worldPath = this.getCurrentWorldPath();
        return worldPath == null ? null : worldPath.getParent();
    }

    public XaeroPath getCurrentRootContainerPath() {
        XaeroPath containerPath = this.getCurrentContainerPath();
        return containerPath == null ? null : containerPath.getRoot();
    }

    public XaeroPath getCurrentWorldPath(XaeroPath autoWorldPath) {
        if (this.customWorldPath == null) {
            return autoWorldPath;
        }
        return this.customWorldPath;
    }
}

