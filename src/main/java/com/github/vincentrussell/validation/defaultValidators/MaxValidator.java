package com.github.vincentrussell.validation.defaultValidators;

import com.github.vincentrussell.validation.ValidationError;
import com.github.vincentrussell.validation.Validator;
import com.github.vincentrussell.validation.annotation.ConstructorParam;
import com.github.vincentrussell.validation.util.ValidationUtils;

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
public final class MaxValidator implements Validator {

    private final long max;

    /**
     * Default constructor.
     *
     * @param max the max value
     */
    public MaxValidator(@ConstructorParam("max") final long max) {
        this.max = max;
    }

    @Override
    public ValidationError validate(final Object object) {
        if (Number.class.isInstance(object)) {
            long value = ((Number) object).longValue();
            return ValidationUtils.isTrue(value <= max,
                    "value %s is more than %s", object, max);
        }
        return null;
    }


}
