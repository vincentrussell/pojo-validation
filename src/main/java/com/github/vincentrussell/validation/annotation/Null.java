package com.github.vincentrussell.validation.annotation;

import com.github.vincentrussell.validation.defaultValidators.NullValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Null annotation to on fields for validation.
 */
@Target(value = {FIELD})
@Retention(value = RUNTIME)
@Documented
@Validator(name = "com.github.vincentrussell.validation.annotation.Null",
        validatedBy = NullValidator.class)
public @interface Null {

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
