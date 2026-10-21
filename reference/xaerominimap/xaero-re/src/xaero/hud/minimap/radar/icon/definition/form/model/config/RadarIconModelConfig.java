/*
 * Decompiled with CFR 0.152.
 */
package xaero.hud.minimap.radar.icon.definition.form.model.config;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;

public class RadarIconModelConfig {
    @Expose
    public float baseScale = 1.0f;
    @Expose
    public float rotationY;
    @Expose
    public float rotationX;
    @Expose
    public float rotationZ;
    @Expose
    public float offsetX;
    @Expose
    public float offsetY;
    @Expose
    public boolean modelPartsRotationReset = true;
    @Expose
    public Boolean renderingFullModel;
    @Expose
    public ArrayList<String> modelMainPartFieldAliases;
    @Expose
    public ArrayList<String> modelPartsFields;
    @Expose
    public ArrayList<ArrayList<String>> modelRootPath;
    @Expose
    public boolean layersAllowed = true;
}

