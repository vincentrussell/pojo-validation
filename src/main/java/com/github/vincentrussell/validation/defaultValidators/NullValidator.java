package com.github.vincentrussell.validation.defaultValidators;


import com.github.vincentrussell.validation.ValidationError;
import com.github.vincentrussell.validation.Validator;
import com.github.vincentrussell.validation.util.ValidationUtils;

/**
 * Null {@link com.github.vincentrussell.validation.annotation.Validator}.
 */
public final class NullValidator implements Validator {


    @Override
    public ValidationError validate(final Object object) {
        return ValidationUtils.isTrue(object == null, "value is not null");
    }
}
