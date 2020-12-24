package com.github.vincentrussell.validation.annotation;

import com.github.vincentrussell.validation.defaultValidators.NotEmptyValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validates that the property is not null or empty.
 * Supported types are:
 * <p>
 * CharSequence (length of character sequence is evaluated)
 * Collection (collection size is evaluated)
 * Map (map size is evaluated)
 * Array (array length is evaluated)
 */
@Target(value = {FIELD})
@Retention(value = RUNTIME)
@Documented
@Validator(name = "com.github.vincentrussell.validation.annotation.NotEmpty",
        validatedBy = NotEmptyValidator.class)
public @interface NotEmpty {

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
