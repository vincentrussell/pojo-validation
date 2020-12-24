package com.github.vincentrussell.validation.defaultValidators;

import com.github.vincentrussell.validation.ValidationError;
import com.github.vincentrussell.validation.Validator;
import com.github.vincentrussell.validation.annotation.ConstructorParam;
import com.github.vincentrussell.validation.util.ValidationUtils;

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
public final class MinValidator implements Validator {

    private final long min;

    /**
     * Default constructor.
     *
     * @param min
     */
    public MinValidator(@ConstructorParam("min") final long min) {
        this.min = min;
    }

    @Override
    public ValidationError validate(final Object object) {
        if (Number.class.isInstance(object)) {
            long value = ((Number) object).longValue();
            return ValidationUtils.isTrue(value >= min,
                    "value %s is less than %s", object, min);
        }
        return null;
    }


}
