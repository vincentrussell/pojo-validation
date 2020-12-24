package com.github.vincentrussell.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The result of calling {@link com.github.vincentrussell.validation.ValidationService#validate(Object)} i}.
 */
public class ValidationResponse {
    private List<ValidationError> validationErrors = new ArrayList<>();

    /**
     * Add an error to this {@link com.github.vincentrussell.validation.ValidationResponse}.
     *
     * @param validationError
     */
    public void addError(final ValidationError validationError) {
        validationErrors.add(validationError);
    }

    /**
     * Whether or not this {@link com.github.vincentrussell.validation.ValidationResponse} is valid.
     *
     * @return true if there are validation errors.
     */
    public boolean isValid() {
        return validationErrors.isEmpty();
    }

    /**
     * get the errors associated with this {@link com.github.vincentrussell.validation.ValidationResponse}.
     *
     * @return an unmodifiable list of the {@link com.github.vincentrussell.validation.ValidationError}.
     */
    public List<ValidationError> getValidationErrors() {
        return Collections.unmodifiableList(validationErrors);
    }

    /**
     * get the errors associated with this {@link com.github.vincentrussell.validation.ValidationResponse}.
     *
     * @param field the field to filter on.
     * @return an unmodifiable list of matching {@link com.github.vincentrussell.validation.ValidationError}s.
     */
    public List<ValidationError> findValidationErrorsForField(final String field) {
        return Collections.unmodifiableList(validationErrors.stream().filter(
                validationError -> field.equals(validationError.getField())).collect(Collectors.toList()));
    }
}
