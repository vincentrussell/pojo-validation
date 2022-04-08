package com.github.vincentrussell.validation.defaultValidators;


import com.github.vincentrussell.validation.ValidationError;
import com.github.vincentrussell.validation.Validator;

/**
 * Required {@link com.github.vincentrussell.validation.annotation.Validator}.
 * Delegates to {@link NotNullValidator} and {@link NotEmptyValidator}
 */
public final class RequiredValidator implements Validator {

    private final NotNullValidator notNullValidator = new NotNullValidator();
    private final NotEmptyValidator notEmptyValidator = new NotEmptyValidator();


    @Override
    public ValidationError validate(final Object object) {
        ValidationError validationError =  notEmptyValidator.validate(object);
        if (validationError == null) {
            return notNullValidator.validate(object);
        }
        return validationError;
    }
}
