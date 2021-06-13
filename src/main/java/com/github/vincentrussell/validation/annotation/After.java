package com.github.vincentrussell.validation.annotation;

import com.github.vincentrussell.validation.defaultValidators.AfterValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validate that a date value is after the specified date.
 * <p>
 * Supported types are:
 * </p>
 * <ul>
 * <li>ZonedDateTime</li>
 * <li>LocalDateTime</li>
 * <li>Date</li>
 * <li>Calendar</li>
 * <li>Timestamp</li>
 * <li>LocalDate</li>
 * <li>Long</li>
 * </ul>
 */
@Target(value = {FIELD})
@Retention(value = RUNTIME)
@Documented
@Validator(name = "com.github.vincentrussell.validation.annotation.After",
        validatedBy = AfterValidator.class)
public @interface After {

    /**
     * the message that will be used when validation fails.
     *
     * @return nothing.
     */
    String errorMessage() default "";

    /**
     * the types that this validation applies to. A {@link com.github.vincentrussell.validation.type.TypeDeterminer}
     * is required.
     *
     * @return nothing.
     */
    String[] types() default {};

    /**
     * The SimpleDateFormat of the {@link After#dateTime()}.
     *
     * @return nothing.
     */
    String format() default "yyyy-MM-dd'T'HH:mm'Z'";

    /**
     * The date/time that will be created based on the format provded.
     *
     * @return nothing.
     */
    String dateTime();
}
