package com.github.vincentrussell.validation;

/**
 * The principal interface that is used for validators to validate pojos.
 *
 * @param <T> the type of object that is to be validated.
 */
public interface Validator<T> {

    /**
     * This function is called from the @{@link com.github.vincentrussell.validation.ValidationService} in order to
     * validate a pojo.
     *
     * @param object the object to be validated
     * @return a {@link com.github.vincentrussell.validation.ValidationError} if there are errors.  Returns nothing if
     * there are no errors
     */
    ValidationError validate(T object);

    /**
     * Specify the name fo the validator that can be used with the
     * {@link com.github.vincentrussell.validation.annotation.Validation#validators()} annotation.
     * @return the name of this validator.
     */
    default String getName() {
        return null;
    }
}
