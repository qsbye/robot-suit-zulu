/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.validator;

import xaero.common.validator.NumericFieldValidator;
import xaero.common.validator.WaypointCoordinateFieldValidator;

public class FieldValidatorHolder {
    private NumericFieldValidator numericFieldValidator;
    private WaypointCoordinateFieldValidator wpCoordFieldValidator;

    public FieldValidatorHolder(NumericFieldValidator numericFieldValidator, WaypointCoordinateFieldValidator wpCoordFieldValidator) {
        this.numericFieldValidator = numericFieldValidator;
        this.wpCoordFieldValidator = wpCoordFieldValidator;
    }

    public NumericFieldValidator getNumericFieldValidator() {
        return this.numericFieldValidator;
    }

    public WaypointCoordinateFieldValidator getWpCoordFieldValidator() {
        return this.wpCoordFieldValidator;
    }
}

