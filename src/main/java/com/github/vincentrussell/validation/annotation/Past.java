package com.github.vincentrussell.validation.annotation;

import com.github.vincentrussell.validation.defaultValidators.PastValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validate that a date value is before "now".  "Now" is calculated at runtime.
 * <p>
 * Supported types are:
 * <ul>
 * <li>ZonedDateTime</li>
 * <li>LocalDateTime</li>
 * <li>Date</li>
 * <li>Calendar</li>
 * <li>Timestamp</li>
 * <li>LocalDate</li>
 * <li>Long</li>
 * </ul>
 * </p>
 */
@Target(value = {FIELD})
@Retention(value = RUNTIME)
@Documented
@Validator(name = "com.github.vincentrussell.validation.annotation.Past",
        validatedBy = PastValidator.class)
public @interface Past {

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
}
