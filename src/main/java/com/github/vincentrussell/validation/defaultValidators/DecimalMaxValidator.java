package com.github.vincentrussell.validation.defaultValidators;

import com.github.vincentrussell.validation.ValidationError;
import com.github.vincentrussell.validation.Validator;
import com.github.vincentrussell.validation.annotation.ConstructorParam;
import com.github.vincentrussell.validation.util.ValidationUtils;

import java.math.BigDecimal;

/**
 * The {@link Validator} that enforces that a number whose value must be lower or equal to the specified maximum.
 *
 * Supported types are:
 * <ul>
 * <li>BigDecimal</li>
 * <li>BigInteger</li>
 * <li>byte, double, float, int, long, short and their respective wrappers or any subclass of Number</li>
 * </ul>
 */
public final class DecimalMaxValidator implements Validator {

    private final BigDecimal max;

    /**
     * Default constructor.
     *
     * @param max the maximum value
     */
    public DecimalMaxValidator(@ConstructorParam("max") final String max) {
        this.max = new BigDecimal(max);
    }

    @Override
    public ValidationError validate(final Object object) {
        if (Number.class.isInstance(object)) {
            return ValidationUtils.isTrue(ValidationUtils.bigDecimalCompareTo((Number) object, max) <= 0,
                    "value %s is more than %s", object, max);
        }
        return null;
    }


}
