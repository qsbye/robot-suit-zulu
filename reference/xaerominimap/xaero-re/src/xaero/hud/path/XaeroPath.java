/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.path;

import java.nio.file.Path;
import java.util.Objects;

public class XaeroPath {
    private final String node;
    private final XaeroPath parent;
    private final XaeroPath root;
    private final int nodeCount;
    private final boolean caseSensitive;
    private String fullString;
    private int hash;
    private boolean hashCalculated;

    private XaeroPath(String node, XaeroPath parent, XaeroPath root, boolean caseSensitive) {
        if (node.indexOf(47) != -1) {
            throw new IllegalArgumentException();
        }
        this.node = node;
        this.parent = parent;
        this.root = root == null ? this : root;
        this.nodeCount = parent == null ? 1 : parent.nodeCount + 1;
        this.caseSensitive = caseSensitive;
    }

    public XaeroPath resolve(String node) {
        return new XaeroPath(node, this, this.root, this.caseSensitive);
    }

    public XaeroPath resolve(XaeroPath path) {
        if (path == null) {
            return this;
        }
        XaeroPath result = this;
        for (int i = 0; i < path.getNodeCount(); ++i) {
            result = result.resolve(path.getAtIndex(i));
        }
        return result;
    }

    public XaeroPath resolveSibling(String node) {
        if (this.parent == null) {
            return XaeroPath.root(node, this.caseSensitive);
        }
        return this.parent.resolve(node);
    }

    public XaeroPath getSubPath(int startIndex) {
        if (startIndex >= this.nodeCount) {
            return null;
        }
        XaeroPath result = null;
        for (int i = startIndex; i < this.nodeCount; ++i) {
            String nodeValueAtIndex = this.getAtIndex(i).getLastNode();
            result = result == null ? XaeroPath.root(nodeValueAtIndex, this.caseSensitive) : result.resolve(nodeValueAtIndex);
        }
        return result;
    }

    public XaeroPath getAtIndex(int index) {
        XaeroPath result = this;
        for (int steps = this.nodeCount - 1 - index; steps > 0; --steps) {
            result = result.getParent();
        }
        return result;
    }

    public boolean isSubOf(XaeroPath other) {
        if (this.nodeCount <= other.nodeCount) {
            return false;
        }
        if (Objects.equals(other, this.parent)) {
            return true;
        }
        return this.parent != null && this.parent.isSubOf(other);
    }

    public Path applyToFilePath(Path path) {
        for (int i = 0; i < this.nodeCount; ++i) {
            path = path.resolve(this.getAtIndex(i).getLastNode());
        }
        return path;
    }

    public String toString() {
        if (this.fullString == null) {
            this.fullString = this.parent == null ? this.node : String.valueOf(this.parent) + "/" + this.node;
        }
        return this.fullString;
    }

    public int getNodeCount() {
        return this.nodeCount;
    }

    public String getLastNode() {
        return this.node;
    }

    public XaeroPath getParent() {
        return this.parent;
    }

    public XaeroPath getRoot() {
        return this.root;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        XaeroPath other = (XaeroPath)obj;
        if (this.nodeCount != other.nodeCount) {
            return false;
        }
        if (this.hashCode() != other.hashCode()) {
            return false;
        }
        if (this.caseSensitive) {
            return this.toString().equals(other.toString());
        }
        return this.toString().equalsIgnoreCase(other.toString());
    }

    public int hashCode() {
        if (!this.hashCalculated) {
            this.hash = (this.caseSensitive ? this.toString() : this.toString().toLowerCase()).hashCode();
            this.hashCalculated = true;
        }
        return this.hash;
    }

    public static XaeroPath root(String node) {
        return XaeroPath.root(node, false);
    }

    public static XaeroPath root(String node, boolean caseSensitive) {
        return new XaeroPath(node, null, null, caseSensitive);
    }
}

