/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import xaero.common.HudMod;
import xaero.common.gui.GuiWaypointContainers;
import xaero.common.gui.GuiWaypointSets;
import xaero.common.gui.GuiWaypointWorlds;
import xaero.common.gui.GuiWaypoints;
import xaero.common.gui.MySuperTinyButton;
import xaero.common.gui.TooltipButton;
import xaero.common.gui.WaypointEditForm;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.misc.OptimizedMath;
import xaero.common.validator.NumericFieldValidator;
import xaero.hud.minimap.MinimapLogs;
import xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions;
import xaero.hud.minimap.config.primary.option.MinimapPrimaryClientConfigOptions;
import xaero.hud.minimap.module.MinimapSession;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.WaypointPurpose;
import xaero.hud.minimap.waypoint.WaypointRenderInfo;
import xaero.hud.minimap.waypoint.WaypointVisibilityType;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;
import xaero.hud.minimap.world.MinimapWorldManager;
import xaero.hud.path.XaeroPath;
import xaero.lib.client.config.ClientConfigManager;
import xaero.lib.client.gui.ScreenBase;
import xaero.lib.client.gui.util.GuiUtils;
import xaero.lib.client.gui.widget.MySmallButton;
import xaero.lib.client.gui.widget.Tooltip;
import xaero.lib.client.gui.widget.dropdown.DropDownWidget;
import xaero.lib.client.gui.widget.dropdown.IDropDownContainer;
import xaero.lib.client.gui.widget.dropdown.IDropDownWidgetCallback;
import xaero.lib.common.config.option.ConfigOption;
import xaero.lib.common.config.single.SingleConfigManager;

public class GuiAddWaypoint
extends ScreenBase
implements IDropDownWidgetCallback {
    private static final Tooltip VISIBILITY_TYPE_TOOLTIP = new Tooltip("gui.xaero_box_visibility_type");
    private static final Tooltip TYPE_TOOLTIP = new Tooltip("gui.xaero_box_waypoint_type");
    private final MinimapSession session;
    private MinimapWorldManager manager;
    protected String screenTitle;
    private Button leftButton;
    private Button rightButton;
    private Button modeSwitchButton;
    private Button resetButton;
    private EditBox nameTextField;
    private EditBox xTextField;
    private EditBox yTextField;
    private EditBox zTextField;
    private EditBox yawTextField;
    private EditBox initialTextField;
    private WaypointEditForm mutualForm;
    private ArrayList<WaypointEditForm> editForms;
    private int selectedWaypointIndex;
    private int defaultContainer;
    private MinimapWorld defaultWorld;
    private GuiWaypointContainers containers;
    private GuiWaypointWorlds worlds;
    private GuiWaypointSets sets;
    private DropDownWidget containersDD;
    private DropDownWidget worldsDD;
    private DropDownWidget setsDD;
    private DropDownWidget colorDD;
    private String fromSet;
    private ArrayList<Waypoint> waypointsEdited;
    private Button disableButton;
    private Button visibilityTypeButton;
    private NumericFieldValidator fieldValidator;
    private NumericFieldValidator fieldYValidator;
    private boolean adding;
    private boolean prefilled;
    private boolean startPrefilled;
    private String namePlaceholder;
    private String xPlaceholder;
    private String yPlaceholder;
    private String zPlaceholder;
    private String yawPlaceholder;
    private String initialPlaceholder;
    private Component colorPlaceholder;
    private Button defaultYawButton;
    private Button defaultDisabledButton;
    private Button defaultVisibilityTypeButton;
    protected Button confirmButton;
    private boolean censorCoordsIfNeeded;
    private final XaeroPath frozenAutoWorldPath;
    private BiFunction<String, Integer, String> censoredTextFormatterString;
    private EditBox.TextFormatter censoredTextFormatter;
    private boolean hasForcedPlayerPos;
    private int forcedPlayerX;
    private int forcedPlayerY;
    private int forcedPlayerZ;
    private double forcedPlayerScale;
    private MinimapWorld forcedCoordSrcWorld;
    private boolean ignoreEditBoxChanges = true;
    private boolean canBeLabyMod = true;
    private final HudMod modMain;

    public GuiAddWaypoint(HudMod modMain, MinimapSession session, Screen par1GuiScreen, Screen escapeScreen, ArrayList<Waypoint> waypointsEdited, XaeroPath defaultParentContainer, MinimapWorld defaultWorld, String waypointSet, boolean adding, boolean hasForcedPlayerPos, int forcedPlayerX, int forcedPlayerY, int forcedPlayerZ, double forcedPlayerScale, MinimapWorld forcedCoordSrcWorld) {
        super(par1GuiScreen, escapeScreen, (Component)Component.literal((String)""));
        this.modMain = modMain;
        this.session = session;
        this.hasForcedPlayerPos = hasForcedPlayerPos;
        this.forcedPlayerX = forcedPlayerX;
        this.forcedPlayerY = forcedPlayerY;
        this.forcedPlayerZ = forcedPlayerZ;
        this.forcedPlayerScale = forcedPlayerScale;
        this.forcedCoordSrcWorld = forcedCoordSrcWorld;
        this.waypointsEdited = waypointsEdited;
        this.manager = session.getWorldManager();
        this.fromSet = waypointSet;
        this.defaultWorld = defaultWorld;
        this.frozenAutoWorldPath = session.getWorldState().getAutoWorldPath();
        this.containers = new GuiWaypointContainers(modMain, this.manager, defaultParentContainer, this.frozenAutoWorldPath);
        this.defaultContainer = this.containers.current;
        this.worlds = new GuiWaypointWorlds(this.manager.getRootWorldContainer(defaultParentContainer), session, defaultWorld.getFullPath(), this.frozenAutoWorldPath);
        this.sets = new GuiWaypointSets(false, defaultWorld, this.fromSet);
        this.prefilled = !waypointsEdited.isEmpty();
        this.startPrefilled = this.prefilled;
        this.createForms();
        this.fieldValidator = modMain.getFieldValidators().getNumericFieldValidator();
        this.fieldYValidator = modMain.getFieldValidators().getWpCoordFieldValidator();
        this.adding = adding;
        this.namePlaceholder = "- " + I18n.get((String)"gui.xaero_waypoint_name", (Object[])new Object[0]);
        this.xPlaceholder = "- x";
        this.yPlaceholder = "- y";
        this.zPlaceholder = "- z";
        this.yawPlaceholder = "- " + I18n.get((String)"gui.xaero_yaw", (Object[])new Object[0]);
        this.initialPlaceholder = "- " + I18n.get((String)"gui.xaero_initial", (Object[])new Object[0]);
        this.colorPlaceholder = Component.literal((String)"-").withStyle(ChatFormatting.DARK_GRAY);
        this.censorCoordsIfNeeded = true;
        this.censoredTextFormatterString = (p_195610_0_, p_195610_1_) -> {
            if (!this.censorCoordsIfNeeded) {
                return p_195610_0_;
            }
            return p_195610_0_.replaceAll(".", "#");
        };
        Style defaultTextStyle = Style.EMPTY;
        this.censoredTextFormatter = (s, i) -> {
            String censoredString = this.censoredTextFormatterString.apply(s, i);
            return cc -> {
                for (int j = 0; j < censoredString.length(); ++j) {
                    cc.accept(j, defaultTextStyle, (int)censoredString.charAt(j));
                }
                return true;
            };
        };
        this.canSkipWorldRender = true;
    }

    public GuiAddWaypoint(HudMod modMain, MinimapSession session, Screen par1GuiScreen, Screen escapeScreen, ArrayList<Waypoint> waypointsEdited, XaeroPath defaultParentContainer, MinimapWorld defaultWorld, String waypointSet, boolean adding) {
        this(modMain, session, par1GuiScreen, escapeScreen, waypointsEdited, defaultParentContainer, defaultWorld, waypointSet, adding, false, 0, 0, 0, Minecraft.getInstance().level.dimensionType().coordinateScale(), null);
    }

    public GuiAddWaypoint(HudMod modMain, MinimapSession session, Screen par1GuiScreen, ArrayList<Waypoint> waypointsEdited, XaeroPath defaultParentContainer, MinimapWorld defaultWorld, boolean adding) {
        this(modMain, session, par1GuiScreen, null, waypointsEdited, defaultParentContainer, defaultWorld, defaultWorld.getCurrentWaypointSetId(), adding);
    }

    private void fillFormWaypoint(WaypointEditForm form, Waypoint w) {
        form.name = w.getLocalizedName();
        form.xText = "" + w.getX();
        form.yText = w.isYIncluded() ? "" + w.getY() : "~";
        form.zText = "" + w.getZ();
        form.yawText = w.isRotation() ? "" + w.getYaw() : "";
        form.initial = w.getInitials();
        form.disabledOrTemporary = w.isDestination() ? 3 : (w.isTemporary() ? 2 : (w.isDisabled() ? 1 : 0));
        form.color = w.getWaypointColor();
        form.visibilityType = w.getVisibility();
        form.thirdParty = w.isThirdParty();
        if (form.initial.length() == 0) {
            form.autoInitial = true;
        }
    }

    private double getDimDiv(double waypointDimScale) {
        double playerDimScale = this.hasForcedPlayerPos ? this.forcedPlayerScale : this.minecraft.getCameraEntity().level().dimensionType().coordinateScale();
        return playerDimScale / waypointDimScale;
    }

    private int getAutomaticX(double waypointDimScale) {
        int playerX = this.hasForcedPlayerPos ? this.forcedPlayerX : OptimizedMath.myFloor(this.minecraft.getCameraEntity().getX());
        return OptimizedMath.myFloor((double)playerX * this.getDimDiv(waypointDimScale));
    }

    private String getAutomaticYInput(MinimapWorld destinationWorld) {
        if (this.hasForcedPlayerPos && (this.forcedPlayerY == Short.MAX_VALUE || this.forcedCoordSrcWorld != null && this.forcedCoordSrcWorld != destinationWorld)) {
            return "~";
        }
        int playerY = this.hasForcedPlayerPos ? this.forcedPlayerY : OptimizedMath.myFloor(this.minecraft.getCameraEntity().getY() + 0.0625);
        return "" + OptimizedMath.myFloor(playerY);
    }

    private int getAutomaticZ(double waypointDimScale) {
        int playerZ = this.hasForcedPlayerPos ? this.forcedPlayerZ : OptimizedMath.myFloor(this.minecraft.getCameraEntity().getZ());
        return OptimizedMath.myFloor((double)playerZ * this.getDimDiv(waypointDimScale));
    }

    private void fillFormAutomatic(WaypointEditForm form) {
        form.xText = "";
        form.yText = "";
        form.zText = "";
        form.color = WaypointColor.getRandom();
        form.autoInitial = true;
    }

    private void createForms() {
        this.editForms = new ArrayList();
        this.mutualForm = new WaypointEditForm();
        for (int i = 0; i < this.waypointsEdited.size(); ++i) {
            Waypoint w = this.waypointsEdited.get(i);
            WaypointEditForm form = new WaypointEditForm();
            this.fillFormWaypoint(form, w);
            this.editForms.add(form);
        }
        if (!this.startPrefilled) {
            WaypointEditForm createdForm = new WaypointEditForm();
            this.fillFormAutomatic(createdForm);
            this.editForms.add(createdForm);
        }
        this.updateMutual();
    }

    private void resetCurrentForm() {
        if (this.selectedWaypointIndex >= this.waypointsEdited.size()) {
            WaypointEditForm freshForm = new WaypointEditForm();
            this.fillFormAutomatic(freshForm);
            this.editForms.set(this.selectedWaypointIndex, freshForm);
        } else {
            Waypoint w = this.waypointsEdited.get(this.selectedWaypointIndex);
            WaypointEditForm freshForm = new WaypointEditForm();
            this.fillFormWaypoint(freshForm, w);
            this.editForms.set(this.selectedWaypointIndex, freshForm);
        }
    }

    private void updateMutual() {
        String nameTextMutual = "";
        String initialMutual = "";
        String yawMutual = "";
        String xTextMutual = "";
        String yTextMutual = "";
        String zTextMutual = "";
        int waypointDisabledOrTemporaryMutual = 0;
        WaypointVisibilityType waypointVisibilityTypeMutual = WaypointVisibilityType.LOCAL;
        WaypointColor colorMutual = null;
        xTextMutual = "";
        yTextMutual = "";
        zTextMutual = "";
        WaypointEditForm firstForm = this.editForms.get(0);
        this.mutualForm.keepName = this.differentValues(WaypointEditForm::getName);
        this.mutualForm.keepXText = this.editForms.size() > 1 && firstForm.xText.isEmpty() || this.differentValues(WaypointEditForm::getxText);
        this.mutualForm.keepYText = this.editForms.size() > 1 && firstForm.yText.isEmpty() || this.differentValues(WaypointEditForm::getyText);
        this.mutualForm.keepZText = this.editForms.size() > 1 && firstForm.zText.isEmpty() || this.differentValues(WaypointEditForm::getzText);
        this.mutualForm.defaultKeepYawText = this.mutualForm.keepYawText = this.differentValues(WaypointEditForm::getYawText);
        this.mutualForm.keepInitial = this.differentValues(WaypointEditForm::getInitial);
        this.mutualForm.autoInitial = this.editForms.size() == 1 && firstForm.autoInitial;
        this.mutualForm.defaultKeepDisabledOrTemporary = this.mutualForm.keepDisabledOrTemporary = this.differentValues(WaypointEditForm::getDisabledOrTemporary);
        this.mutualForm.defaultKeepVisibilityType = this.mutualForm.keepVisibilityType = this.differentValues(WaypointEditForm::getVisibilityType);
        this.mutualForm.defaultKeepColor = this.differentValues(WaypointEditForm::getColor);
        if (!this.mutualForm.keepName) {
            nameTextMutual = firstForm.name;
        }
        if (!this.mutualForm.keepXText) {
            xTextMutual = firstForm.xText;
        }
        if (!this.mutualForm.keepYText) {
            yTextMutual = firstForm.yText;
        }
        if (!this.mutualForm.keepZText) {
            zTextMutual = firstForm.zText;
        }
        if (!this.mutualForm.keepYawText) {
            yawMutual = firstForm.yawText;
        }
        if (!this.mutualForm.keepInitial) {
            initialMutual = firstForm.initial;
        }
        if (!this.mutualForm.keepDisabledOrTemporary) {
            waypointDisabledOrTemporaryMutual = firstForm.disabledOrTemporary;
        }
        if (!this.mutualForm.keepVisibilityType) {
            waypointVisibilityTypeMutual = firstForm.visibilityType;
        }
        if (!this.mutualForm.defaultKeepColor) {
            colorMutual = firstForm.color;
        }
        this.mutualForm.name = nameTextMutual;
        this.mutualForm.xText = xTextMutual;
        this.mutualForm.yText = yTextMutual;
        this.mutualForm.zText = zTextMutual;
        this.mutualForm.yawText = yawMutual;
        this.mutualForm.initial = initialMutual;
        this.mutualForm.disabledOrTemporary = waypointDisabledOrTemporaryMutual;
        this.mutualForm.visibilityType = waypointVisibilityTypeMutual;
        this.mutualForm.color = colorMutual;
        this.mutualForm.thirdParty = !this.differentValues(WaypointEditForm::isThirdParty) && firstForm.thirdParty;
    }

    private void confirmMutual() {
        for (int i = 0; i < this.editForms.size(); ++i) {
            WaypointEditForm individualForm = this.editForms.get(i);
            if (!this.mutualForm.keepName && !individualForm.thirdParty) {
                individualForm.name = this.mutualForm.name;
            }
            if (!this.mutualForm.keepXText && !individualForm.thirdParty) {
                individualForm.xText = this.mutualForm.xText;
            }
            if (!this.mutualForm.keepYText && !individualForm.thirdParty) {
                individualForm.yText = this.mutualForm.yText;
            }
            if (!this.mutualForm.keepZText && !individualForm.thirdParty) {
                individualForm.zText = this.mutualForm.zText;
            }
            if (!this.mutualForm.keepYawText && !individualForm.thirdParty) {
                individualForm.yawText = this.mutualForm.yawText;
            }
            if (!this.mutualForm.keepInitial) {
                if (!individualForm.initial.equals(this.mutualForm.initial)) {
                    individualForm.autoInitial = false;
                }
                individualForm.initial = this.mutualForm.initial;
            }
            if (!this.mutualForm.keepDisabledOrTemporary) {
                individualForm.disabledOrTemporary = this.mutualForm.disabledOrTemporary;
            }
            if (!this.mutualForm.keepVisibilityType) {
                individualForm.visibilityType = this.mutualForm.visibilityType;
            }
            if (this.mutualForm.color == null) continue;
            individualForm.color = this.mutualForm.color;
        }
    }

    private boolean differentValues(Function<WaypointEditForm, Object> s) {
        if (this.editForms.size() == 1) {
            return false;
        }
        WaypointEditForm testWaypoint = this.editForms.get(0);
        for (int i = 1; i < this.editForms.size(); ++i) {
            WaypointEditForm w = this.editForms.get(i);
            if (s.apply(w).equals(s.apply(testWaypoint))) continue;
            return true;
        }
        return false;
    }

    public Component[] createColorOptions() {
        boolean unchangedOption = this.getCurrent().defaultKeepColor;
        Component[] options = new Component[WaypointColor.values().length + (unchangedOption ? 1 : 0)];
        if (unchangedOption) {
            options[0] = this.colorPlaceholder;
        }
        for (int i = 0; i < WaypointColor.values().length; ++i) {
            if (i == 0) {
                options[i + (unchangedOption ? 1 : 0)] = WaypointColor.values()[i].getName();
                continue;
            }
            WaypointColor waypointColor = WaypointColor.values()[i];
            options[i + (unchangedOption ? 1 : 0)] = WaypointColor.values()[i].getName().copy().withStyle(s -> s.withColor(waypointColor.getHex()));
        }
        return options;
    }

    private boolean getMutualEditConfig() {
        SingleConfigManager primaryConfigManager = HudMod.INSTANCE.getHudConfigs().getPrimaryClientConfigManager();
        return (Boolean)primaryConfigManager.getEffective((ConfigOption)MinimapPrimaryClientConfigOptions.WAYPOINT_MUTUAL_EDIT);
    }

    private void setMutualEditConfig(boolean value) {
        SingleConfigManager primaryConfigManager = HudMod.INSTANCE.getHudConfigs().getPrimaryClientConfigManager();
        primaryConfigManager.getConfig().set((ConfigOption)MinimapPrimaryClientConfigOptions.WAYPOINT_MUTUAL_EDIT, (Object)value);
    }

    public void init() {
        super.init();
        this.ignoreEditBoxChanges = true;
        String string = this.screenTitle = this.adding ? I18n.get((String)"gui.xaero_new_waypoint", (Object[])new Object[0]) : I18n.get((String)"gui.xaero_edit_waypoint", (Object[])new Object[0]);
        if (this.editForms.size() > 1) {
            this.screenTitle = this.screenTitle + (String)(this.editForms.size() > 1 ? " (" + (String)(this.getMutualEditConfig() ? "" : this.selectedWaypointIndex + 1 + "/") + this.editForms.size() + ")" : "");
        }
        this.nameTextField = this.applyEditBoxResponder(new EditBox(this.font, this.width / 2 - 100, 104, 200, 20, (Component)Component.translatable((String)"gui.xaero_waypoint_name")));
        this.xTextField = this.applyEditBoxResponder(new EditBox(this.font, this.width / 2 - 109, 134, 50, 20, (Component)Component.literal((String)"x")));
        this.yTextField = this.applyEditBoxResponder(new EditBox(this.font, this.width / 2 - 53, 134, 50, 20, (Component)Component.literal((String)"y")));
        this.zTextField = this.applyEditBoxResponder(new EditBox(this.font, this.width / 2 + 3, 134, 50, 20, (Component)Component.literal((String)"z")));
        ClientConfigManager configManager = this.modMain.getHudConfigs().getClientConfigManager();
        boolean hideWaypointCoordinatesConfig = (Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.HIDE_WAYPOINT_COORDINATES);
        if (hideWaypointCoordinatesConfig) {
            this.xTextField.addFormatter(this.censoredTextFormatter);
            this.yTextField.addFormatter(this.censoredTextFormatter);
            this.zTextField.addFormatter(this.censoredTextFormatter);
        }
        this.yawTextField = this.applyEditBoxResponder(new EditBox(this.font, this.width / 2 + 59, 134, 50, 20, (Component)Component.translatable((String)"gui.xaero_yaw")));
        this.initialTextField = this.applyEditBoxResponder(new EditBox(this.font, this.width / 2 - 25, 164, 50, 20, (Component)Component.translatable((String)"gui.xaero_initial")));
        this.addWidget((GuiEventListener)this.nameTextField);
        this.addWidget((GuiEventListener)this.xTextField);
        this.addWidget((GuiEventListener)this.yTextField);
        this.addWidget((GuiEventListener)this.zTextField);
        this.addWidget((GuiEventListener)this.yawTextField);
        this.addWidget((GuiEventListener)this.initialTextField);
        this.confirmButton = new MySmallButton(0, this.width / 2 - 155, this.height / 6 + 168, (Component)Component.translatable((String)"gui.xaero_confirm"), b -> {
            Screen patt0$temp;
            if (this.getMutualEditConfig()) {
                this.confirmMutual();
            }
            boolean creatingAWaypoint = this.adding && this.waypointsEdited.size() < this.editForms.size();
            XaeroPath destinationWorldKeys = (XaeroPath)this.worlds.getCurrentKey();
            MinimapWorld destinationWorld = this.manager.getWorld(destinationWorldKeys);
            double waypointDimScale = this.session.getDimensionHelper().getDimCoordinateScale(destinationWorld);
            int initialEditedWaypointsSize = this.waypointsEdited.size();
            for (int i = 0; i < this.editForms.size(); ++i) {
                WaypointRenderInfo renderInfoEditDest;
                Waypoint w2;
                int z;
                boolean shouldCreate;
                boolean bl = shouldCreate = i >= initialEditedWaypointsSize;
                if (!creatingAWaypoint && shouldCreate) break;
                WaypointEditForm waypointForm = this.editForms.get(i);
                String nameString = waypointForm.name;
                String xString = waypointForm.xText;
                String yString = waypointForm.yText;
                if (yString.equals("-") || yString.isEmpty()) {
                    yString = this.getAutomaticYInput(destinationWorld);
                }
                String zString = waypointForm.zText;
                String initialString = waypointForm.initial;
                WaypointColor color = waypointForm.color;
                boolean yIncluded = !yString.equals("~");
                int x = xString.equals("-") || xString.isEmpty() ? this.getAutomaticX(waypointDimScale) : Integer.parseInt(xString);
                int y = !yIncluded ? 0 : Integer.parseInt(yString);
                int n = z = zString.equals("-") || zString.isEmpty() ? this.getAutomaticZ(waypointDimScale) : Integer.parseInt(zString);
                if (shouldCreate) {
                    w2 = new Waypoint(x, y, z, nameString, initialString, color, WaypointPurpose.NORMAL, false, yIncluded);
                    renderInfoEditDest = w2.getRenderInfoEditDest();
                    this.waypointsEdited.add(w2);
                } else {
                    w2 = this.waypointsEdited.get(i);
                    renderInfoEditDest = w2.getRenderInfoEditDest();
                    if (w2.getPurpose() != WaypointPurpose.DEATH || !nameString.equals(I18n.get((String)"gui.xaero_deathpoint", (Object[])new Object[0]))) {
                        w2.setName(nameString);
                        if (w2.getPurpose() != WaypointPurpose.NORMAL) {
                            w2.setPurpose(WaypointPurpose.NORMAL);
                        }
                    }
                    w2.setX(x);
                    w2.setY(y);
                    w2.setZ(z);
                    renderInfoEditDest.setInitials(initialString);
                    renderInfoEditDest.setWaypointColor(color);
                    w2.setYIncluded(yIncluded);
                }
                String yawText = waypointForm.yawText;
                int disableOrTemporary = waypointForm.disabledOrTemporary;
                boolean yawIsUsable = yawText.length() > 0 && !yawText.equals("-");
                w2.setRotation(yawIsUsable);
                if (yawIsUsable) {
                    w2.setYaw(Integer.parseInt(yawText));
                }
                if (w2.isDestination() != (disableOrTemporary == 3)) {
                    w2.setPurpose(!w2.isThirdParty() && disableOrTemporary == 3 ? WaypointPurpose.DESTINATION : WaypointPurpose.NORMAL);
                }
                w2.setTemporary(false);
                renderInfoEditDest.setDisabled(disableOrTemporary == 1);
                if (!w2.isThirdParty() && disableOrTemporary == 2) {
                    w2.setTemporary(true);
                }
                renderInfoEditDest.setVisibility(waypointForm.visibilityType);
                if (renderInfoEditDest == w2) continue;
                renderInfoEditDest.nullEverythingMatching(w2);
            }
            MinimapWorld sourceWorld = this.defaultWorld;
            WaypointSet sourceSet = sourceWorld.getWaypointSet(this.fromSet);
            String destinationSetKey = this.sets.getCurrentSetKey();
            WaypointSet destinationSet = destinationWorld.getWaypointSet(destinationSetKey);
            List justPersonalWaypoints = this.waypointsEdited.stream().filter(w -> !w.isThirdParty()).toList();
            if (this.adding || sourceSet != destinationSet) {
                if (!((Boolean)configManager.getEffective((ConfigOption)MinimapProfiledConfigOptions.NEW_WAYPOINTS_TO_BOTTOM)).booleanValue()) {
                    destinationSet.addAll(justPersonalWaypoints, true);
                } else {
                    destinationSet.addAll(justPersonalWaypoints);
                }
            }
            if (sourceSet != destinationSet) {
                sourceSet.removeAll(justPersonalWaypoints);
            }
            try {
                this.session.getWorldManagerIO().saveWorld(sourceWorld);
                if (destinationWorld != sourceWorld) {
                    this.session.getWorldManagerIO().saveWorld(destinationWorld);
                }
            }
            catch (IOException e) {
                MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
            }
            if (this.waypointsEdited.size() != justPersonalWaypoints.size()) {
                this.session.getWorldManagerIO().getRootConfigIO().save(sourceWorld.getContainer().getRoot());
            }
            if ((patt0$temp = this.parent) instanceof GuiWaypoints) {
                GuiWaypoints waypointsScreen = (GuiWaypoints)patt0$temp;
                waypointsScreen.clearFilter(false);
            }
            this.goBack();
        });
        this.addRenderableWidget((GuiEventListener)this.confirmButton);
        this.addRenderableWidget((GuiEventListener)new MySmallButton(0, this.width / 2 + 5, this.height / 6 + 168, (Component)Component.translatable((String)"gui.xaero_cancel", (Object[])new Object[0]), b -> this.goBack()));
        this.leftButton = Button.builder((Component)Component.literal((String)"<"), b -> {
            --this.selectedWaypointIndex;
            if (this.selectedWaypointIndex < 0) {
                this.selectedWaypointIndex = 0;
            }
            boolean restoreFocus = this.getFocused() == b;
            this.init(this.width, this.height);
            if (restoreFocus) {
                boolean activeBU = this.leftButton.active;
                this.leftButton.active = true;
                this.setFocused((GuiEventListener)this.leftButton);
                this.leftButton.active = activeBU;
            }
        }).bounds(this.width / 2 - 203, 104, 20, 20).build();
        this.rightButton = Button.builder((Component)Component.literal((String)">"), b -> {
            ++this.selectedWaypointIndex;
            if (this.selectedWaypointIndex >= this.editForms.size()) {
                this.selectedWaypointIndex = this.editForms.size() - 1;
            }
            boolean restoreFocus = this.getFocused() == b;
            this.init(this.width, this.height);
            if (restoreFocus) {
                boolean activeBU = this.rightButton.active;
                this.rightButton.active = true;
                this.setFocused((GuiEventListener)this.rightButton);
                this.rightButton.active = activeBU;
            }
        }).bounds(this.width / 2 + 183, 104, 20, 20).build();
        this.modeSwitchButton = Button.builder((Component)Component.translatable((String)(this.getMutualEditConfig() ? "gui.xaero_waypoints_edit_mode_all" : "gui.xaero_waypoints_edit_mode_individually")), b -> {
            this.setMutualEditConfig(!this.getMutualEditConfig());
            HudMod.INSTANCE.getHudConfigs().getPrimaryClientConfigManagerIO().save();
            if (this.getMutualEditConfig()) {
                this.prefilled = true;
                this.updateMutual();
            } else {
                this.confirmMutual();
            }
            boolean restoreFocus = this.getFocused() == b;
            this.init(this.width, this.height);
            if (restoreFocus) {
                boolean activeBU = this.modeSwitchButton.active;
                this.modeSwitchButton.active = true;
                this.setFocused((GuiEventListener)this.modeSwitchButton);
                this.modeSwitchButton.active = activeBU;
            }
        }).bounds(this.width / 2 + 106, 56, 99, 20).build();
        if (this.editForms.size() > 1) {
            this.addRenderableWidget((GuiEventListener)this.leftButton);
            this.addRenderableWidget((GuiEventListener)this.rightButton);
            this.addRenderableWidget((GuiEventListener)this.modeSwitchButton);
        }
        this.resetButton = Button.builder((Component)Component.translatable((String)"gui.xaero_waypoints_edit_reset"), b -> {
            if (this.getMutualEditConfig()) {
                this.createForms();
                boolean restoreFocus = this.getFocused() == b;
                this.init(this.width, this.height);
                if (restoreFocus) {
                    boolean activeBU = this.resetButton.active;
                    this.resetButton.active = true;
                    this.setFocused((GuiEventListener)this.resetButton);
                    this.resetButton.active = activeBU;
                }
            } else {
                this.resetCurrentForm();
                boolean restoreFocus = this.getFocused() == b;
                this.init(this.width, this.height);
                if (restoreFocus) {
                    boolean activeBU = this.resetButton.active;
                    this.resetButton.active = true;
                    this.setFocused((GuiEventListener)this.resetButton);
                    this.resetButton.active = activeBU;
                }
            }
        }).bounds(this.width / 2 - 204, 56, 99, 20).build();
        this.addRenderableWidget((GuiEventListener)this.resetButton);
        this.nameTextField.setValue(this.getCurrent().name);
        this.xTextField.setValue(this.getCurrent().xText);
        this.yTextField.setValue(this.getCurrent().yText);
        this.zTextField.setValue(this.getCurrent().zText);
        this.yawTextField.setValue(this.getCurrent().yawText);
        this.initialTextField.setValue(this.getCurrent().initial);
        this.disableButton = new TooltipButton(this.width / 2 + 31, 164, 79, 20, this.getDisableButtonText(), b -> {
            this.getCurrent().disabledOrTemporary = (this.getCurrent().disabledOrTemporary + 1) % 4;
            this.disableButton.setMessage(this.getDisableButtonText());
            this.getCurrent().keepDisabledOrTemporary = false;
            if (this.defaultDisabledButton != null) {
                this.defaultDisabledButton.active = true;
            }
        }, () -> TYPE_TOOLTIP);
        this.addRenderableWidget((GuiEventListener)this.disableButton);
        this.visibilityTypeButton = new TooltipButton(this.width / 2 - 109, 164, 79, 20, this.getCurrent().visibilityType.getTranslation(), b -> {
            this.getCurrent().visibilityType = WaypointVisibilityType.values()[(this.getCurrent().visibilityType.ordinal() + 1) % WaypointVisibilityType.values().length];
            this.visibilityTypeButton.setMessage(this.getCurrent().visibilityType.getTranslation());
            this.getCurrent().keepVisibilityType = false;
            if (this.defaultVisibilityTypeButton != null) {
                this.defaultVisibilityTypeButton.active = true;
            }
        }, () -> VISIBILITY_TYPE_TOOLTIP);
        this.addRenderableWidget((GuiEventListener)this.visibilityTypeButton);
        if (this.getCurrent().defaultKeepYawText) {
            this.defaultYawButton = Button.builder((Component)Component.literal((String)"-"), b -> {
                this.getCurrent().keepYawText = true;
                this.getCurrent().yawText = "";
                this.yawTextField.setValue(this.getCurrent().yawText);
                b.active = false;
            }).bounds(this.width / 2 + 111, 134, 20, 20).build();
            this.addRenderableWidget((GuiEventListener)this.defaultYawButton);
            boolean bl = this.defaultYawButton.active = !this.getCurrent().keepYawText;
        }
        if (this.getCurrent().defaultKeepDisabledOrTemporary) {
            this.defaultDisabledButton = Button.builder((Component)Component.literal((String)"-"), b -> {
                this.getCurrent().keepDisabledOrTemporary = true;
                this.getCurrent().disabledOrTemporary = 0;
                this.disableButton.setMessage(this.getDisableButtonText());
                b.active = false;
            }).bounds(this.width / 2 + 110, 164, 20, 20).build();
            this.addRenderableWidget((GuiEventListener)this.defaultDisabledButton);
            boolean bl = this.defaultDisabledButton.active = !this.getCurrent().keepDisabledOrTemporary;
        }
        if (this.getCurrent().defaultKeepVisibilityType) {
            this.defaultVisibilityTypeButton = Button.builder((Component)Component.literal((String)"-"), b -> {
                this.getCurrent().keepVisibilityType = true;
                this.getCurrent().visibilityType = WaypointVisibilityType.LOCAL;
                this.visibilityTypeButton.setMessage(this.getCurrent().visibilityType.getTranslation());
                b.active = false;
            }).bounds(this.width / 2 - 130, 164, 20, 20).build();
            this.addRenderableWidget((GuiEventListener)this.defaultVisibilityTypeButton);
            boolean bl = this.defaultVisibilityTypeButton.active = !this.getCurrent().keepVisibilityType;
        }
        if (hideWaypointCoordinatesConfig) {
            this.addRenderableWidget((GuiEventListener)new MySuperTinyButton(this.width / 2 + 115, 134, (Component)Component.translatable((String)(this.censorCoordsIfNeeded ? "gui.xaero_waypoints_edit_show" : "gui.xaero_waypoints_edit_hide")), b -> {
                this.censorCoordsIfNeeded = !this.censorCoordsIfNeeded;
                b.setMessage((Component)Component.translatable((String)(this.censorCoordsIfNeeded ? "gui.xaero_waypoints_edit_show" : "gui.xaero_waypoints_edit_hide")));
            }));
        }
        WaypointColor currentColor = this.getCurrent().color;
        this.colorDD = DropDownWidget.Builder.begin().setOptions(this.createColorOptions()).setX(this.width / 2 - 60).setY(82).setW(120).setSelected(Integer.valueOf((currentColor == null ? -1 : currentColor.ordinal()) + (this.getCurrent().defaultKeepColor ? 1 : 0))).setCallback((IDropDownWidgetCallback)this).setContainer((IDropDownContainer)this).setNarrationTitle((Component)Component.translatable((String)"gui.xaero_dropdown_waypoint_color")).build();
        this.addWidget((GuiEventListener)this.colorDD);
        this.setsDD = this.createSetsDropdown();
        this.addWidget((GuiEventListener)this.setsDD);
        this.containersDD = this.createContainersDropdown();
        this.addWidget((GuiEventListener)this.containersDD);
        this.worldsDD = this.createWorldsDropdown();
        this.addWidget((GuiEventListener)this.worldsDD);
        if (this.getCurrent().thirdParty) {
            this.nameTextField.setEditable(false);
            this.nameTextField.active = false;
            this.xTextField.setEditable(false);
            this.xTextField.active = false;
            this.yTextField.setEditable(false);
            this.yTextField.active = false;
            this.zTextField.setEditable(false);
            this.zTextField.active = false;
            this.yawTextField.setEditable(false);
            this.yawTextField.active = false;
        } else {
            this.setFocused((GuiEventListener)this.nameTextField);
            this.nameTextField.setFocused(true);
        }
        this.updateConfirmButton();
    }

    protected void setInitialFocus() {
    }

    public EditBox applyEditBoxResponder(EditBox box) {
        box.setResponder(s -> {
            if (!this.ignoreEditBoxChanges) {
                this.postType((GuiEventListener)box);
            }
        });
        return box;
    }

    private DropDownWidget createSetsDropdown() {
        return DropDownWidget.Builder.begin().setOptions(this.sets.getOptions()).setX(this.width / 2 - 101).setY(60).setW(201).setSelected(Integer.valueOf(this.sets.getCurrentSet())).setCallback((IDropDownWidgetCallback)this).setContainer((IDropDownContainer)this).setNarrationTitle((Component)Component.translatable((String)"gui.xaero_dropdown_waypoint_set")).build();
    }

    private DropDownWidget createContainersDropdown() {
        return DropDownWidget.Builder.begin().setOptions(this.containers.options).setX(this.width / 2 - 203).setY(38).setW(200).setSelected(Integer.valueOf(this.containers.current)).setCallback((IDropDownWidgetCallback)this).setContainer((IDropDownContainer)this).setNarrationTitle((Component)Component.translatable((String)"gui.xaero_dropdown_waypoint_container")).build();
    }

    private DropDownWidget createWorldsDropdown() {
        return DropDownWidget.Builder.begin().setOptions(this.worlds.options).setX(this.width / 2 + 2).setY(38).setW(200).setSelected(Integer.valueOf(this.worlds.current)).setCallback((IDropDownWidgetCallback)this).setContainer((IDropDownContainer)this).setNarrationTitle((Component)Component.translatable((String)"gui.xaero_dropdown_waypoint_world")).build();
    }

    private Component getDisableButtonText() {
        return Component.translatable((String)(this.getCurrent().disabledOrTemporary == 3 ? "gui.xaero_destination" : (this.getCurrent().disabledOrTemporary == 1 ? "gui.xaero_toggle_disabled" : (this.getCurrent().disabledOrTemporary == 0 ? "gui.xaero_toggle_enabled" : "gui.xaero_temporary2"))));
    }

    private WaypointEditForm getCurrent() {
        return this.getMutualEditConfig() ? this.mutualForm : this.editForms.get(this.selectedWaypointIndex);
    }

    public boolean keyPressed(KeyEvent event) {
        GuiEventListener focused = this.getFocused();
        this.preType(focused);
        this.ignoreEditBoxChanges = false;
        boolean result = super.keyPressed(event);
        if (this.ignoreEditBoxChanges) {
            this.canBeLabyMod = false;
        }
        if (focused instanceof EditBox && this.canConfirm() && (event.key() == 40 || event.key() == 88)) {
            this.confirmButton.onClick(new MouseButtonEvent(0.0, 0.0, new MouseButtonInfo(0, 0)), false);
            return true;
        }
        return result;
    }

    public boolean charTyped(CharacterEvent event) {
        GuiEventListener focused = this.getFocused();
        this.preType(focused);
        this.ignoreEditBoxChanges = false;
        boolean result = super.charTyped(event);
        if (this.ignoreEditBoxChanges) {
            this.canBeLabyMod = false;
        }
        return result;
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        this.ignoreEditBoxChanges = false;
        boolean result = super.mouseClicked(event, doubleClick);
        if (this.ignoreEditBoxChanges) {
            this.canBeLabyMod = false;
        }
        return result;
    }

    public boolean mouseReleased(MouseButtonEvent event) {
        this.ignoreEditBoxChanges = false;
        boolean result = super.mouseReleased(event);
        if (this.ignoreEditBoxChanges) {
            this.canBeLabyMod = false;
        }
        return result;
    }

    private void preType(GuiEventListener focused) {
        if (focused == null) {
            return;
        }
    }

    private void postType(GuiEventListener focused) {
        this.ignoreEditBoxChanges = true;
        if (focused == null) {
            return;
        }
        if (this.nameTextField == focused) {
            if (!(!this.getCurrent().autoInitial || this.nameTextField.getValue().length() <= 0 || this.getCurrent().keepInitial && this.getMutualEditConfig())) {
                this.initialTextField.setValue(this.nameTextField.getValue().substring(0, 1).toUpperCase());
            }
        } else if (this.initialTextField == focused) {
            this.getCurrent().autoInitial = false;
        }
        this.checkFields(focused);
        this.updateConfirmButton();
    }

    public void setFocused(GuiEventListener l) {
        this.preType(l);
        GuiEventListener currentFocused = this.getFocused();
        if (currentFocused != null && currentFocused != l && currentFocused instanceof EditBox) {
            ((EditBox)currentFocused).setFocused(false);
        }
        super.setFocused(l);
    }

    private boolean canConfirm() {
        WaypointEditForm current = this.getCurrent();
        return !(!current.keepName && current.name.length() <= 0 || !current.keepInitial && current.initial.length() <= 0);
    }

    private void updateConfirmButton() {
        this.confirmButton.active = this.modeSwitchButton.active = this.canConfirm();
        this.leftButton.active = !this.getMutualEditConfig() && this.canConfirm() && this.selectedWaypointIndex > 0;
        this.rightButton.active = !this.getMutualEditConfig() && this.canConfirm() && this.selectedWaypointIndex < this.editForms.size() - 1;
    }

    private void handleCoordinateInputSpaces(EditBox coordinateBox, EditBox nextBox) {
        String startingBoxValue = coordinateBox.getValue();
        int indexOfFirstSpace = startingBoxValue.indexOf(32);
        if (indexOfFirstSpace != -1) {
            String subStringToCut = startingBoxValue.substring(indexOfFirstSpace + 1);
            coordinateBox.setValue(startingBoxValue.substring(0, indexOfFirstSpace));
            coordinateBox.moveCursorToStart(false);
            nextBox.setValue(nextBox.getValue() + subStringToCut);
            if (this.getFocused() == coordinateBox) {
                coordinateBox.setFocused(false);
                nextBox.setFocused(true);
                this.setFocused((GuiEventListener)nextBox);
                nextBox.moveCursorToEnd(false);
            }
        }
    }

    protected void checkFields(GuiEventListener focused) {
        this.handleCoordinateInputSpaces(this.xTextField, this.yTextField);
        this.handleCoordinateInputSpaces(this.yTextField, this.zTextField);
        this.handleCoordinateInputSpaces(this.zTextField, this.yawTextField);
        this.fieldValidator.validate(this.yawTextField);
        if (this.yawTextField == focused) {
            this.getCurrent().keepYawText = false;
            if (this.defaultYawButton != null) {
                this.defaultYawButton.active = true;
            }
        }
        this.fieldValidator.validate(this.xTextField);
        this.fieldYValidator.validate(this.yTextField);
        this.fieldValidator.validate(this.zTextField);
        WaypointEditForm current = this.getCurrent();
        current.name = this.nameTextField.getValue();
        current.xText = this.xTextField.getValue();
        current.yText = this.yTextField.getValue();
        current.zText = this.zTextField.getValue();
        current.yawText = this.yawTextField.getValue();
        current.initial = this.initialTextField.getValue();
        if (current.initial.length() > 2) {
            current.initial = current.initial.substring(0, 2);
            this.initialTextField.setValue(current.initial);
        }
        if (current.yawText.length() > 4) {
            current.yawText = current.yawText.substring(0, 4);
            this.yawTextField.setValue(current.yawText);
        }
        if (this.prefilled && this.editForms.size() > 1 && this.getMutualEditConfig()) {
            current.keepName = current.name.isEmpty();
            current.keepXText = current.xText.isEmpty();
            current.keepYText = current.yText.isEmpty();
            current.keepZText = current.zText.isEmpty();
            current.keepInitial = current.initial.isEmpty();
        }
    }

    public void tick() {
        if (this.minecraft.getCameraEntity() == null) {
            this.minecraft.gui.setScreen(null);
            return;
        }
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphics, int par1, int par2, float par3) {
        this.renderEscapeScreen(guiGraphics, 0, 0, par3);
        super.extractBackground(guiGraphics, par1, par2, par3);
    }

    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int par1, int par2, float par3) {
        super.extractRenderState(guiGraphics, par1, par2, par3);
        super.renderTooltips(guiGraphics, par1, par2, par3);
    }

    protected void renderPreDropdown(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        super.renderPreDropdown(guiGraphics, mouseX, mouseY, partial);
        guiGraphics.centeredText(this.font, this.screenTitle, this.width / 2, 20, -1);
        WaypointEditForm current = this.getCurrent();
        if (!this.canBeLabyMod) {
            this.ignoreEditBoxChanges = true;
        }
        if (this.ignoreEditBoxChanges) {
            if (!this.nameTextField.isFocused() && current.keepName) {
                GuiUtils.setFieldText((EditBox)this.nameTextField, (String)this.namePlaceholder, (int)-11184811);
                this.nameTextField.moveCursorTo(0, false);
            }
            XaeroPath destinationWorldKeys = (XaeroPath)this.worlds.getCurrentKey();
            MinimapWorld destinationWorld = this.manager.getWorld(destinationWorldKeys);
            double waypointDimScale = this.session.getDimensionHelper().getDimCoordinateScale(destinationWorld);
            if (current.keepXText) {
                if (!this.xTextField.isFocused()) {
                    GuiUtils.setFieldText((EditBox)this.xTextField, (String)this.xPlaceholder, (int)-11184811);
                }
            } else if (current.xText.isEmpty()) {
                GuiUtils.setFieldText((EditBox)this.xTextField, (String)("" + this.getAutomaticX(waypointDimScale)), (int)-11184811);
                this.xTextField.moveCursorTo(0, false);
            }
            if (current.keepYText) {
                if (!this.yTextField.isFocused()) {
                    GuiUtils.setFieldText((EditBox)this.yTextField, (String)this.yPlaceholder, (int)-11184811);
                }
            } else if (current.yText.isEmpty()) {
                GuiUtils.setFieldText((EditBox)this.yTextField, (String)this.getAutomaticYInput(destinationWorld), (int)-11184811);
                this.yTextField.moveCursorTo(0, false);
            }
            if (current.keepZText) {
                if (!this.zTextField.isFocused()) {
                    GuiUtils.setFieldText((EditBox)this.zTextField, (String)this.zPlaceholder, (int)-11184811);
                }
            } else if (current.zText.isEmpty()) {
                GuiUtils.setFieldText((EditBox)this.zTextField, (String)("" + this.getAutomaticZ(waypointDimScale)), (int)-11184811);
                this.zTextField.moveCursorTo(0, false);
            }
            if (!this.yawTextField.isFocused() && current.yawText.isEmpty()) {
                if (current.keepYawText) {
                    GuiUtils.setFieldText((EditBox)this.yawTextField, (String)this.yawPlaceholder, (int)-11184811);
                } else {
                    GuiUtils.setFieldText((EditBox)this.yawTextField, (String)I18n.get((String)"gui.xaero_yaw", (Object[])new Object[0]), (int)-11184811);
                }
                this.yawTextField.moveCursorTo(0, false);
            }
            if (!this.initialTextField.isFocused() && current.initial.isEmpty()) {
                if (current.keepInitial) {
                    GuiUtils.setFieldText((EditBox)this.initialTextField, (String)this.initialPlaceholder, (int)-11184811);
                } else {
                    GuiUtils.setFieldText((EditBox)this.initialTextField, (String)I18n.get((String)"gui.xaero_initial", (Object[])new Object[0]), (int)-11184811);
                }
                this.initialTextField.moveCursorTo(0, false);
            }
        }
        this.nameTextField.extractRenderState(guiGraphics, mouseX, mouseY, partial);
        this.xTextField.extractRenderState(guiGraphics, mouseX, mouseY, partial);
        this.yTextField.extractRenderState(guiGraphics, mouseX, mouseY, partial);
        this.zTextField.extractRenderState(guiGraphics, mouseX, mouseY, partial);
        this.yawTextField.extractRenderState(guiGraphics, mouseX, mouseY, partial);
        this.initialTextField.extractRenderState(guiGraphics, mouseX, mouseY, partial);
        if (this.ignoreEditBoxChanges) {
            GuiUtils.setFieldText((EditBox)this.nameTextField, (String)current.name);
            GuiUtils.setFieldText((EditBox)this.xTextField, (String)current.xText);
            GuiUtils.setFieldText((EditBox)this.yTextField, (String)current.yText);
            GuiUtils.setFieldText((EditBox)this.zTextField, (String)current.zText);
            GuiUtils.setFieldText((EditBox)this.yawTextField, (String)current.yawText);
            GuiUtils.setFieldText((EditBox)this.initialTextField, (String)current.initial);
        }
        this.ignoreEditBoxChanges = true;
    }

    public boolean onSelected(DropDownWidget menu, int selected) {
        if (menu == this.setsDD) {
            this.sets.setCurrentSet(selected);
            if (this.session.getWorldState().getCurrentWorldPath().equals(this.worlds.getCurrentKey())) {
                this.manager.getCurrentWorld().setCurrentWaypointSetId(this.sets.getCurrentSetKey());
                try {
                    this.session.getWorldManagerIO().saveWorld(this.manager.getCurrentWorld());
                }
                catch (IOException e) {
                    MinimapLogs.LOGGER.error("suppressed exception", (Throwable)e);
                }
            }
        } else if (menu == this.colorDD) {
            this.getCurrent().color = !this.getCurrent().defaultKeepColor ? WaypointColor.fromIndex(selected) : (selected == 0 ? null : WaypointColor.fromIndex(selected - 1));
        } else if (menu == this.containersDD) {
            this.containers.current = selected;
            MinimapWorld currentWorld = this.containers.current != this.defaultContainer ? this.manager.getRootWorldContainer((String)this.containers.getCurrentKey()).getFirstWorld() : this.defaultWorld;
            this.sets = new GuiWaypointSets(false, currentWorld, this.containers.current == this.defaultContainer ? this.fromSet : currentWorld.getCurrentWaypointSetId());
            this.worlds = new GuiWaypointWorlds(this.manager.getRootWorldContainer((String)this.containers.getCurrentKey()), this.session, currentWorld.getFullPath(), this.frozenAutoWorldPath);
            this.setsDD = this.createSetsDropdown();
            this.replaceWidget((AbstractWidget)this.setsDD, (AbstractWidget)this.setsDD);
            this.worldsDD = this.createWorldsDropdown();
            this.replaceWidget((AbstractWidget)this.worldsDD, (AbstractWidget)this.worldsDD);
        } else if (menu == this.worldsDD) {
            MinimapWorld currentWorld;
            this.worlds.current = selected;
            XaeroPath worldKeys = (XaeroPath)this.worlds.getCurrentKey();
            this.sets = new GuiWaypointSets(false, currentWorld, (currentWorld = this.manager.getWorld(worldKeys)) == this.defaultWorld ? this.fromSet : currentWorld.getCurrentWaypointSetId());
            this.setsDD = this.createSetsDropdown();
            this.replaceWidget((AbstractWidget)this.setsDD, (AbstractWidget)this.setsDD);
        }
        return true;
    }
}

