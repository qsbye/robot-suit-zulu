/*
 * Decompiled with CFR 0.152.
 */
package xaero.common.validator;

import xaero.common.validator.NumericFieldValidator;

public class WaypointCoordinateFieldValidator
extends NumericFieldValidator {
    @Override
    protected boolean charIsValid(char c, int index) {
        return c == '~' && index == 0 || !this.stringBuilder.toString().equals("~") && super.charIsValid(c, index);
    }

    @Override
    protected boolean checkNumberFormat(boolean validated) {
        if (this.stringBuilder.toString().equals("~")) {
            return validated;
        }
        return super.checkNumberFormat(validated);
    }
}

