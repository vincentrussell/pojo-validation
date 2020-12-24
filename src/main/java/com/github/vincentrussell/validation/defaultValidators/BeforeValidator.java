package com.github.vincentrussell.validation.defaultValidators;

import com.github.vincentrussell.validation.ValidationError;
import com.github.vincentrussell.validation.Validator;
import com.github.vincentrussell.validation.annotation.ConstructorParam;
import com.github.vincentrussell.validation.util.ValidationUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class BeforeValidator implements Validator {

    private final ZonedDateTime dateTime;

    /**
     * Default constructor.
     * @param format
     * @param dateTime
     */
    public BeforeValidator(@ConstructorParam("format") final String format,
                           @ConstructorParam("dateTime") final String dateTime) {
        this.dateTime = ZonedDateTime.parse(dateTime, DateTimeFormatter.ofPattern(format));
    }

    @Override
    public ValidationError validate(final Object object) {
        return ValidationUtils.isDateBefore(object, dateTime);
    }
}

