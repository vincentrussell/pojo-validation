package com.github.vincentrussell.validation.defaultValidators;


import com.github.vincentrussell.validation.ValidationError;
import com.github.vincentrussell.validation.Validator;
import com.github.vincentrussell.validation.annotation.ConstructorParam;
import com.github.vincentrussell.validation.util.ValidationUtils;

/**
 * Default Boolean {@link com.github.vincentrussell.validation.annotation.Validator}.
 */
public final class BoolValidator implements Validator {

    private final Boolean bool;

    /**
     * Default constructor.
     * @param bool the boolean that this validator should match
     */
    public BoolValidator(@ConstructorParam("value") final Boolean bool) {
        this.bool = bool;
    }

    @Override
    public ValidationError validate(final Object object) {
        if (object != null && Boolean.class.isInstance(object)) {
            return ValidationUtils.isTrue(bool.equals(object),
                    "value %s doesn't match the boolean %s", object, bool);
        }
        return null;
    }
}
