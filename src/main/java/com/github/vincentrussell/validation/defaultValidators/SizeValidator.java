package com.github.vincentrussell.validation.defaultValidators;

import com.github.vincentrussell.validation.ValidationError;
import com.github.vincentrussell.validation.Validator;
import com.github.vincentrussell.validation.annotation.ConstructorParam;
import com.github.vincentrussell.validation.util.ValidationUtils;

import java.util.Collection;
import java.util.Map;

/**
 * A {@link com.github.vincentrussell.validation.Validator} that is used to validate the size.  The following
 * types ore supported:
 * <ul>
 * <li>CharSequence (length of character sequence is evaluated)</li>
 * <li>Collection (collection size is evaluated)</li>
 * <li>Map (map size is evaluated)</li>
 * <li>Array (array length is evaluated)</li>
 * </ul>
 */
public final class SizeValidator implements Validator {

    private final int min;
    private final int max;

    /**
     * Default constructor.
     *
     * @param min
     * @param max
     */
    public SizeValidator(@ConstructorParam("min") final int min,
                         @ConstructorParam("max") final int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public ValidationError validate(final Object object) {
        if (CharSequence.class.isInstance(object)) {
            int length = ((CharSequence) object).length();
            return ValidationUtils.isTrue(
                    lengthCheck(length),
                    "string length %d didn't between required min %d and max %d",
                    length, min, max);
        } else if (Collection.class.isInstance(object)) {
            int length = ((Collection) object).size();
            return ValidationUtils.isTrue(
                    lengthCheck(length),
                    "collection length %d didn't between required min %d and max %d",
                    length, min, max);
        } else if (Map.class.isInstance(object)) {
            int length = ((Map) object).size();
            return ValidationUtils.isTrue(
                    lengthCheck(length),
                    "map length %d didn't between required min %d and max %d",
                    length, min, max);
        } else if (object != null && object.getClass().isArray()) {
            int length = ((Object[]) object).length;
            return ValidationUtils.isTrue(
                    lengthCheck(length),
                    "array length %d didn't between required min %d and max %d",
                    length, min, max);
        }
        return null;
    }

    private boolean lengthCheck(final int length) {
        boolean minLengthCheck = min == -1 || length >= min;
        boolean maxLengthCheck = max == -1 || length <= max;
        return minLengthCheck && maxLengthCheck;
    }
}
