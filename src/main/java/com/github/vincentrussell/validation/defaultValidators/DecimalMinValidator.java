package com.github.vincentrussell.validation.defaultValidators;

import com.github.vincentrussell.validation.ValidationError;
import com.github.vincentrussell.validation.Validator;
import com.github.vincentrussell.validation.annotation.ConstructorParam;
import com.github.vincentrussell.validation.util.ValidationUtils;

import java.math.BigDecimal;

/**
 * The {@link Validator} that enforces that a number whose value must be higher or equal to the specified minimum.
 *
 * Supported types are:
 * <ul>
 * <li>BigDecimal</li>
 * <li>BigInteger</li>
 * <li>byte, double, float, int, long, short and their respective wrappers or any subclass of Number</li>
 * </ul>
 */
public final class DecimalMinValidator implements Validator {

    private final BigDecimal min;

    /**
     * Default constructor.
     *
     * @param min
     */
    public DecimalMinValidator(@ConstructorParam("min") final String min) {
        this.min = new BigDecimal(min);
    }

    @Override
    public ValidationError validate(final Object object) {
        if (Number.class.isInstance(object)) {
            return ValidationUtils.isTrue(ValidationUtils.bigDecimalCompareTo((Number) object, min) >= 0,
                    "value %s is less than %s", object, min);
        }
       return null;
    }

}
