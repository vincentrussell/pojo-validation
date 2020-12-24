package com.github.vincentrussell.validation.defaultValidators;


import com.github.vincentrussell.validation.ValidationError;
import com.github.vincentrussell.validation.Validator;
import com.github.vincentrussell.validation.util.ValidationUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * The annotated element validates that the property is not null or empty.
 * Supported types are:
 * <p>
 * CharSequence (length of character sequence is evaluated)
 * Collection (collection size is evaluated)
 * Map (map size is evaluated)
 * Array (array length is evaluated)
 */
public final class NotEmptyValidator implements Validator {

    @Override
    public ValidationError validate(final Object object) {
        if (CharSequence.class.isInstance(object)) {
            return ValidationUtils.isTrue(
                    !StringUtils.isEmpty((CharSequence) object),
                    "string is empty");
        } else if (Collection.class.isInstance(object)) {
            return ValidationUtils.isTrue(
                    !((Collection) object).isEmpty(),
                    "collection is empty");
        } else if (Map.class.isInstance(object)) {
            return ValidationUtils.isTrue(
                    !((Map) object).isEmpty(),
                    "map is empty");
        } else if (object != null && object.getClass().isArray()) {
            int length = ((Object[]) object).length;
            return ValidationUtils.isTrue(
                    length > 0,
                    "array is empty");
        }
        return null;
    }
}
