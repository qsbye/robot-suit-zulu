/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.category.ui.setting;

public final class EditorSettingType {
    public static final EditorSettingType ITERATION_BUTTON = new EditorSettingType(true, "iteration button");
    public static final EditorSettingType SLIDER = new EditorSettingType(true, "slider");
    public static final EditorSettingType EXPANDING = new EditorSettingType(true, "expanding");
    private final boolean usingIndices;
    private final String id;

    private EditorSettingType(boolean usingIndices, String id) {
        this.usingIndices = usingIndices;
        this.id = id;
    }

    public boolean isUsingIndices() {
        return this.usingIndices;
    }
}

