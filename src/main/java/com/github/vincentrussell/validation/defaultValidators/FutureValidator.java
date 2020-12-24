package com.github.vincentrussell.validation.defaultValidators;

import com.github.vincentrussell.validation.ValidationError;
import com.github.vincentrussell.validation.Validator;
import com.github.vincentrussell.validation.util.ValidationUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class FutureValidator implements Validator {

    @Override
    public ValidationError validate(final Object object) {
        return ValidationUtils.isDateAfter(object, ZonedDateTime.now(ZoneId.of("UTC")));
    }
}

