package com.github.vincentrussell.validation;

/**
 * The principal interface that is used for validators to validate pojos.  The difference between this interface and
 * {@link com.github.vincentrussell.validation.Validator} is that this interface allows you to pass down the top level
 * object that is being validated down to the validator.
 *
 * @param <T> The type of field that will be validated.
 * @param <K> The type of the main object
 */
public interface ValidatorWithMainObject<T, K> extends Validator {

    /**
     * This function is called from the @{@link com.github.vincentrussell.validation.ValidationService} in order to
     * validate a pojo.
     *
     * @param object
     * @param mainObject
     * @return a {@link com.github.vincentrussell.validation.ValidationError} if there are errors.  Returns nothing if
     * there are no errors
     */
    ValidationError validate(T object, K mainObject);


    /**
     * This method should not be used for {@link ValidatorWithMainObject}.
     * Use {@link ValidatorWithMainObject#validate(Object, Object)} instead.
     *
     * @param object
     * @return a {@link com.github.vincentrussell.validation.ValidationError} if there are errors.  Returns nothing if
     * there are no errors
     */
    default ValidationError validate(Object object) {
        throw new UnsupportedOperationException("use the other method that takes the object"
                + "to validate and the main object.");
    }
}
